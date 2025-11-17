package com.globus.userservice.service.impl;

import com.globus.userservice.dto.AuthRequest;
import com.globus.userservice.dto.UserPatchRequest;
import com.globus.userservice.dto.UserRegistrationRequest;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.entity.AuditLog;
import com.globus.userservice.entity.Privilege;
import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;
import com.globus.userservice.entity.enums.OperationAction;
import com.globus.userservice.exception.IllegalUserStateException;
import com.globus.userservice.integration.IntegrationMessageSender;
import com.globus.userservice.mapper.UserMapper;
import com.globus.userservice.repository.AuditLogRepository;
import com.globus.userservice.repository.UserRepository;
import com.globus.userservice.service.interfaces.PrivilegeService;
import com.globus.userservice.service.interfaces.RoleService;
import com.globus.userservice.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IntegrationMessageSender messageSender;
    private final AuditLogRepository auditLogRepository;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Деактивирует пользователя, если он активен. Проводит логирование и рассылает уведомления.
     * <p>
     * Метод требует 2 пользователей: the affected user (которого требуется деактивировать) и acting user (кто проводит операцию).
     * Если пользователь уже неактивен, выбрасывается исключение. При успешной деактивации,
     * записывается лог и отпрвляются сообщения через интеграционного отправщика сообщений.
     *
     * @param id      UUID подвергнутого пользователя (которого требуется деактивировать)
     * @param user_id UUID действующего пользователя (кто проводит операцию)
     * @return UserResponseDto содержит информацию пользователя после деактивации
     * @throws EntityNotFoundException   если любой из пользователей (подвергнутый или действующий) не существует/не найден
     * @throws IllegalUserStateException если подвергнутый пользователь уже неактивен
     */
    @Override
    @Transactional
    public UserResponseDto deleteUser(UUID id, UUID user_id) {
        User affectedUser = getUserById(id);
        User actingUser = getUserById(user_id);

        if (affectedUser.getIsActive()) {
            affectedUser.setIsActive(false);
        } else {
            throw new IllegalUserStateException("Пользователь уже не активен");
        }

        messageSender.sendMessage();

        auditLogRepository.save(prepareLog(actingUser, affectedUser));

        return userMapper.toUserResponseDto(userRepository.save(actingUser));
    }

    /**
     * Запрашивает пользователя через id или выбрасывает исключение, если таковой не находится.
     *
     * @param id UUID пользователя для запроса
     * @return Пользователь, связаный с предоставленным UUID
     * @throws EntityNotFoundException если не находится пользователя по требуемому UUID
     */
    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
    }

    /**
     * Запрашивает пользователя через email или выбрасывает исключение, если таковой не находится.
     *
     * @param email Почта пользователя
     * @return Сущность пользователя
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с email: %s не найден.", email)));
    }

    /**
     * Создает лог аудита для фиксирования операции.
     * <p>
     * Заполняет поля лога: действующий пользователь, подверженый пользователь, тип операции,
     * и укеазаное описание.
     *
     * @param actingUser   действующий пользователь
     * @param affectedUser подверженый пользователь
     * @return Подготовленый лог аудита
     */
    private AuditLog prepareLog(User actingUser, User affectedUser) {
        AuditLog log = new AuditLog();
        log.setUser(actingUser);
        log.setAffectedUser(affectedUser);
        log.setAction(OperationAction.CHANGE_STATUS);
        log.setDescription("Изменение статуса пользователя");
        return log;
    }

    /**
     * Поиск данных о пользователе, необходимые для аутентификации
     *
     * @param email Почта пользователя
     * @return UserDetails данные о пользователе.
     * @throws UsernameNotFoundException Исключение, которое возвращает метод getUserByEmail,
     *  если пользователь не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        return new org.springframework.security.core.userdetails.User(
                email, user.getPassword(), getAuthority(user.getPrivileges()));
    }

    /**
     * Преобразует список привилегий пользователя в коллекцию GrantedAuthority
     *
     * @param privileges Список привилегий пользователя
     * @return Коллекция GrantedAuthority
     */
    private Collection<? extends GrantedAuthority> getAuthority(List<Privilege> privileges) {
        return privileges.stream().map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Регистрация нового пользователя
     *
     * @param req Данные, необходимые для регистрации
     * @return UUID id созданного пользователя
     */
    @Override
    @Transactional
    public UUID registerUser(UserRegistrationRequest req) {
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPosition(req.getPosition());
        user.setEmail(req.getEmail());
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoles(new ArrayList<>());
        user.setPrivileges(new ArrayList<>());

        // Роль
        if (req.getRoleName() != null) {
            Role role = roleService.getRoleByName(req.getRoleName());
            user.getRoles().add(role);
        }

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    /**
     * Изменение пароля пользователя по его email
     *
     * @param request Объект, содержащий email пользователя и новый пароль
     */
    @Override
    public void updatePasswordByEmail(AuthRequest request) {
        User user = getUserByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));// В базе всегда хеш
        userRepository.save(user);
    }

    /**
     * Добавление пользователю ролей
     *
     * @param id UUID идентификатор пользователя
     * @param roleName название роли
     * @return UserResponseDto Данные о пользователе
     */
    @Override
    public UserResponseDto assignRole(UUID id, String roleName) {
        User user = getUserById(id);
        Role role = roleService.getRoleByName(roleName);
        user.getRoles().add(role);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    /**
     * Удаление ролей у пользователя
     *
     * @param id UUID идентификатор пользователя
     * @param roleName название роли
     * @return UserResponseDto Данные о пользователе
     */
    @Override
    public UserResponseDto removeRole(UUID id, String roleName) {
        User user = getUserById(id);
        Role role = roleService.getRoleByName(roleName);
        user.getRoles().removeIf(r -> r.equals(role));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    /**
     * Добавление пользователю привилегий
     *
     * @param id UUID идентификатор пользователя
     * @param privilegeName название привилегии
     * @return UserResponseDto Данные о пользователе
     */
    @Override
    public UserResponseDto assignPrivilege(UUID id, String privilegeName) {
        User user = getUserById(id);
        Privilege privilege = privilegeService.getPrivilegeByName(privilegeName);
        user.getPrivileges().add(privilege);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    /**
     * Удаление у пользователя привилегии
     *
     * @param id UUID идентификатор пользователя
     * @param privilegeName название привилегии
     * @return UserResponseDto Данные о пользователе
     */
    @Override
    public UserResponseDto removePrivilege(UUID id, String privilegeName) {
        User user = getUserById(id);
        Privilege privilege = privilegeService.getPrivilegeByName(privilegeName);
        user.getPrivileges().removeIf(p -> p.equals(privilege));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    /**
     * Изменение параметров пользователя
     *
     * @param id UUID идентификатор пользователя
     * @param req Новые данные о пользователе
     * @return UserResponseDto Данные о пользователе
     */
    @Override
    public UserResponseDto updateUser(UUID id, UserPatchRequest req) {
        User user = getUserById(id);

        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getPosition() != null) user.setPosition(req.getPosition());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getRoleName() != null) {
            Role role = roleService.getRoleByName(req.getRoleName());
            user.getRoles().clear();
            user.getRoles().add(role);
        }

        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    /**
     * Поиск пользователей по части имени или фамилии
     *
     * @param search Часть имени
     * @return Список пользователей, имеющих в имени или фамилии искомую комбинацию
     */
    @Override
    public List<UserResponseDto> searchUsers(String search) {
        return userRepository.searchUsers(search).stream()
                .map(userMapper::toUserResponseDto).toList();
    }
}

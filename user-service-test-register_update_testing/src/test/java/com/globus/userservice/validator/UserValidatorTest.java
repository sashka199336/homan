import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {


    static class UsersDataDto {
        String firstName, lastName, position, roleName, email;
    }

    interface UserService { Optional<Object> getByEmail(String email); }

    static class ValidationException extends RuntimeException {
        ValidationException(String msg) { super(msg); }
    }

    static class UserValidator {
        final UserService us;
        UserValidator(UserService us) { this.us = us; }

        void validate(UsersDataDto d) {
            StringBuilder e = new StringBuilder();
            if (d.firstName == null || d.firstName.isBlank()) e.append("Имя;");
            if (d.lastName == null || d.lastName.isBlank()) e.append("Фамилия;");
            if (d.position == null || d.position.isBlank()) e.append("Должность;");
            if (d.roleName == null || d.roleName.isBlank()) e.append("Роль;");
            if (d.email == null || d.email.isBlank()) e.append("Email;");
            else if (us.getByEmail(d.email).isPresent()) e.append("Email есть;");
            if (e.length() > 0) throw new ValidationException(e.toString());
        }
    }

    UserService empty = email -> Optional.empty();
    UserService exists = email -> Optional.of(new Object());

    @Test
    void ok() {
        UsersDataDto d = new UsersDataDto();
        d.firstName = "A";
        d.lastName = "B";
        d.position = "P";
        d.roleName = "R";
        d.email = "e@e";
        assertDoesNotThrow(() -> new UserValidator(empty).validate(d));
    }

    @Test
    void missingFields() {
        UsersDataDto d = new UsersDataDto();
        var ex = assertThrows(ValidationException.class, () -> new UserValidator(empty).validate(d));
        assertTrue(ex.getMessage().contains("Имя;"));
        assertTrue(ex.getMessage().contains("Фамилия;"));
        assertTrue(ex.getMessage().contains("Email;"));
    }

    @Test
    void emailExists() {
        UsersDataDto d = new UsersDataDto();
        d.firstName = "A";
        d.lastName = "B";
        d.position = "P";
        d.roleName = "R";
        d.email = "email@x";
        var ex = assertThrows(ValidationException.class, () -> new UserValidator(exists).validate(d));
        assertTrue(ex.getMessage().contains("Email есть;"));
    }
}
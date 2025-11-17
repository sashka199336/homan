package com.globus.financesystem.service.impl;

import com.globus.financesystem.kafka.dto.OpenAccountDto;
import com.globus.financesystem.kafka.dto.OpenAccountErrorDto;
import com.globus.financesystem.kafka.dto.OpenAccountResponseDto;
import com.globus.financesystem.mapper.AccountMapper;
import com.globus.financesystem.mapper.OpenAccountResponseMapper;
import com.globus.financesystem.model.entity.Account;
import com.globus.financesystem.repository.AccountRepository;
import com.globus.financesystem.service.AccountService;
import com.globus.financesystem.service.util.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OpenAccountResponseMapper openAccountResponseMapper;
    private final OutboxService outboxService;

    @Override
    @Transactional
    public void createAccount(OpenAccountDto dto) {

        if (dto.claimId() == null || dto.customerId() == null ||
                dto.customerId().isBlank() || !"OPEN".equalsIgnoreCase(dto.operation())) {
            log.warn("[OpenAccountService] Некорректные данные для открытия счета: {}", dto);

            OpenAccountErrorDto errorDto = new OpenAccountErrorDto(
                    dto.claimId(),
                    dto.customerId(),
                    "account_error"
            );

            outboxService.saveEvent("Account", dto.customerId(), "AccountError", errorDto);
            return;
        }

        Account account = accountMapper.toEntity(dto);
        account.setAccountNumber(generateUniqueAccountNumber());

        accountRepository.save(account);
        log.info("[OpenAccountService] Аккаунт сохранён в базе с id={} и accountNumber={}",
                account.getId(), account.getAccountNumber());

        OpenAccountResponseDto responseDto = openAccountResponseMapper.toDto(dto, account);

        outboxService.saveEvent("Account", dto.customerId(), "AccountOpened", responseDto);

    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            int number = ThreadLocalRandom.current().nextInt(100000, 1000000);
            accountNumber = "ACC-" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}

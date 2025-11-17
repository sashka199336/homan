package com.globus.claim_service.mapper;

import com.globus.claim_service.event.ClaimRejectedEvent;
import com.globus.claim_service.event.LoanAppEvent;
import com.globus.claim_service.event.NotificationEvent;
import com.globus.claim_service.event.PaymentResponse;
import com.globus.claim_service.model.Claim;
import com.globus.claim_service.model.enums.ClaimStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "notificationId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientId", source = "customerId")
    @Mapping(target = "message", expression = "java(createRejectionMessage(event.getReason()))")
    NotificationEvent toRejectedNotificationEvent(ClaimRejectedEvent event);

    default String createRejectionMessage(String reason) {
        return String.format("Заявка отклонена. Причина" +
                (reason != null ? reason : "статус клиента не активен") +
                ". Пожалуйста, обновите ваш профиль."
                );
    }

    @Mapping(target = "notificationId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientId", source = "customerId")
    @Mapping(target = "message", expression = "java(createSuccessMessage(event))")
    NotificationEvent toSuccessNotificationEvent(LoanAppEvent event);

    default String createSuccessMessage(LoanAppEvent event) {
        return String.format(
                "Заявка #%s на сумму %s сроком на %s месяцев успешно отправлена в кредитный сервис.",
                event.getClaimId(),
                event.getAmount(),
                event.getCreditTermMonth()
        );
    }

    @Mapping(target = "notificationId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientId", expression = "java(claim.getCustomerId().toString())")
    @Mapping(target = "message", expression = "java(createPaymentResultMessage(response, claim))")
    NotificationEvent toPaymentCompletedMessage (PaymentResponse response, Claim claim);

    default String createPaymentResultMessage (PaymentResponse response, Claim claim) {
        if (response.getClaimStatus().equals(ClaimStatus.REJECTED)) {
            return String.format(
                    "Перевод средств по заявке %s в сумме %s отклонен финансовой службой. Для уточнения причин обратитесь в клиентскую службу.",
                    response.getClaimId(), claim.getAmount());
        } else {
            return String.format(
                    "Перевод средств по заявке %s в сумме %s выполнен. Проверьте баланс Вашего счета.",
                    response.getClaimId(), claim.getAmount());
        }
    }
}

package com.globus_bank.customer_service.utils;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.entity.enums.ActionType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }
    
    @Before("serviceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        String id = extractIdFromArgs(args);
        ActionType actionType = determineActionType(methodName);
        logMethodCall(actionType, id);
    }
    
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        
        String id = extractIdFromArgs(args);
        ActionType actionType = determineActionType(methodName);
        logMethodSuccess(actionType, id);
    }
    
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        
        String id = extractIdFromArgs(args);
        ActionType actionType = determineActionType(methodName);
        logMethodException(actionType, id, ex);
    }

    @Before("execution(* com.globus_bank.customer_service.kafka.NotificationRuleProducer.sendNotificationRule(..))")
    public void logBeforeSendNotification(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof List<?> contactsDtos) {
            log.info("Отправка уведомления в топик {}: {}", "notifications-rule-topic", contactsDtos);
        }
    }

    @AfterReturning(pointcut = "execution(* com.globus_bank.customer_service.kafka.NotificationRuleProducer.sendNotificationRule(..))")
    public void logAfterSuccessfulSendNotification(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID id;
        if (args.length > 0 && args[0] instanceof List<?> contactsList) {
            if (contactsList.getFirst() instanceof ContactsDto contactsDto) {
                id = contactsDto.getCustomerId();
                log.info("NotificationRule отправлено успешно. Id: {}", id);
            }
            else{
                log.info("NotificationRule отправлено успешно.");
            }
        }
    }

    @AfterThrowing(pointcut = "execution(* com.globus_bank.customer_service.kafka.NotificationRuleProducer.sendNotificationRule(..))", throwing = "ex")
    public void logAfterFailedSendNotification(JoinPoint joinPoint, Throwable ex) {
        Object[] args = joinPoint.getArgs();
        UUID id;
        if (args.length > 0 && args[0] instanceof List<?> contactsList) {
            if (contactsList.getFirst() instanceof ContactsDto contactsDto) {
                id = contactsDto.getCustomerId();
                log.error("Не удалось отправить NotificationRule. Id: {}", id, ex);
            }
            else{
                log.error("Не удалось отправить NotificationRule. ", ex);
            }
        }
    }
    
    private ActionType determineActionType(String methodName) {
        if(methodName == null) {
            return null;
        }
        String lowerCaseMethodName = methodName.toLowerCase();
        if(lowerCaseMethodName.contains("find") || lowerCaseMethodName.contains("get")) {
            return ActionType.VIEW;
        } else if(lowerCaseMethodName.contains("update")) {
            return ActionType.UPDATE;
        } else if(lowerCaseMethodName.contains("save")) {
            return ActionType.CREATE;
        }
        
        return null;
    }
    
    private void logMethodCall(ActionType actionType, String id) {
        log.info("ActionType={}, eventTimestamp={}, id={}, result={}",
                actionType,
                LocalDateTime.now(),
                id,
                "Совершен запрос.");
    }
    
    private void logMethodSuccess(ActionType actionType, String id) {
        log.info("ActionType={}, eventTimestamp={}, id={}, result={}",
                actionType,
                LocalDateTime.now(),
                id,
                "Запрос выполнен успешно.");
    }
    
    private void logMethodException(ActionType actionType, String id, Throwable ex) {
        log.info("ActionType={}, eventTimestamp={}, id={}, exception={}",
                actionType,
                LocalDateTime.now(),
                id,
                "Произошла ошибка: " + ex.getMessage());
    }
    
    private String extractIdFromArgs(Object[] args) {
        if(args == null)
            return null;
        
        for(Object arg : args) {
            if(arg instanceof UUID) {
                return arg.toString();
            }
        }
        
        return null;
    }
}

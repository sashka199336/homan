package com.globus.userservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut(value = "execution(public * com.globus.userservice.*Controller.*(..))")
    public void controllerPointcut() {
    }

    @Pointcut(value = "execution(public * com.globus.userservice.service..*.*(..))")
    public void servicePointcut() {
    }

    @Before("controllerPointcut()")
    public void loggingControllers(JoinPoint joinPoint) {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String path = request.getRequestURI();
        log.info("Контроллер: %s вызван метод %s по пути %s".formatted(controllerName, methodName, path));
    }

    @Before("servicePointcut()")
    public void loggingServices(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(","));

        if (params.isEmpty()) {
            params = "без параметров";
        }
        log.info("Сервис: %s вызван метод %s с параметрами - %s".formatted(className, methodName, params));
    }

    @AfterReturning(value = "servicePointcut()", returning = "response")
    public void loggingServicesReturn(JoinPoint joinPoint, Object response) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (response == null) {
            log.info("%s из %s ничего не возвращает через контроллер".formatted(methodName, className));
        } else if (response instanceof List<?> responseList) {
            log.info("%s из %s возвращает список объектов через контроллер. Фрагмент:".formatted(methodName, className));
            responseList.stream()
                    .limit(10)
                    .map(Object::toString)
                    .forEach(log::info);
        } else {
            log.info("%s из %s возвращает через контроллер: %s".formatted(methodName, className, response));
        }
    }
}

package com.globus.userservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LoggingAspectTest {

    LoggingAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new LoggingAspect();
    }

    @Test
    void controllerPointcut() {

        aspect.controllerPointcut();
    }

    @Test
    void servicePointcut() {

        aspect.servicePointcut();
    }

    @Test
    void loggingControllers() {

        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);


        class UserControllerFake {}
        Object target = new UserControllerFake();

        Mockito.when(joinPoint.getTarget()).thenReturn(target);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getName()).thenReturn("getUser");

        // Мок  request
        HttpServletRequest   request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/user/1");

        ServletRequestAttributes attributes = Mockito.mock(ServletRequestAttributes.class);
        Mockito.when(attributes.getRequest()).thenReturn(request);

        // Мок RequestContextHolder
        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::currentRequestAttributes).thenReturn(attributes);


            aspect.loggingControllers(joinPoint);
        }
    }

    @Test
    void loggingServices() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);

        Mockito.when(signature.getDeclaringType()).thenReturn(this.getClass());
        Mockito.when(signature.getName()).thenReturn("someServiceMethod");
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{"param1", 2});

        aspect.loggingServices(joinPoint);

        // Тест без параметров
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{});
        aspect.loggingServices(joinPoint);
    }

    @Test
    void loggingServicesReturn() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);

        Mockito.when(signature.getDeclaringType()).thenReturn(this.getClass());
        Mockito.when(signature.getName()).thenReturn("myMethod");
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);

        // response null
        aspect.loggingServicesReturn(joinPoint, null);

        // response List
        aspect.loggingServicesReturn(joinPoint, Arrays.asList("a", "b", "c"));

        // response не List
        aspect.loggingServicesReturn(joinPoint, "some result");
    }
}
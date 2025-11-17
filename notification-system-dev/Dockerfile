FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /opt/app

COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY email-notification-ms ./email-notification-ms
COPY sms-notification-ms ./sms-notification-ms
COPY notification-app ./notification-app
COPY kafka-test-producer ./kafka-test-producer

ARG MODULE

RUN gradle :${MODULE}:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser && \
    mkdir -p /opt/app && \
    chown appuser:appuser /opt/app

WORKDIR /opt/app

ARG MODULE=${MODULE}

COPY --from=builder --chown=appuser:appuser /opt/app/${MODULE}/build/libs/*.jar ./app.jar

RUN chmod 550 /opt/app/app.jar

USER appuser

ARG APP_PORT
EXPOSE ${APP_PORT}
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
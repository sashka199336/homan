FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /opt/app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

RUN useradd -m appuser && \
    mkdir -p /app && \
    chown appuser:appuser /app

WORKDIR /opt/app

COPY --from=builder --chown=appuser:appuser /opt/app/build/libs/*.jar ./app.jar

RUN chmod 550 /opt/app/app.jar

USER appuser

EXPOSE 80
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
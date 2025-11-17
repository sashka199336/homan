# Этап сборки
FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /workspace
COPY gradle* .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/
RUN ./gradlew dependencies --no-daemon
RUN ./gradlew clean build --no-daemon

# Этап выполнения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
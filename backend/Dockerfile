# Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle/wrapper ./gradle/wrapper
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

# Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

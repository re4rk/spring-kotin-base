FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle/
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY api/build.gradle.kts api/
COPY domain/build.gradle.kts domain/

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet || true

COPY api/src api/src/
COPY domain/src domain/src/

RUN ./gradlew :api:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/api/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

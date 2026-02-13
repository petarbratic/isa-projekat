# Multi-stage: build pa run
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# wget za Docker healthcheck (actuator/health)
RUN apk add --no-cache wget

RUN adduser -D -u 1000 appuser
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

# Za klaster: port može biti overridovan; replike dijele istu bazu (URL preko env)
ENTRYPOINT ["java", "-jar", "app.jar"]

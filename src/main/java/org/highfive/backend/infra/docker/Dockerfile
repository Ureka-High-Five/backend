FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

ENV OTEL_AGENT_VERSION=1.39.0

RUN apk add --no-cache curl ca-certificates && \
    curl -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o opentelemetry-javaagent.jar && \
    chmod 644 opentelemetry-javaagent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]

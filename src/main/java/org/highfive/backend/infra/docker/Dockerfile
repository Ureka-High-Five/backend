FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# build/libs 아래 .jar 복사
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
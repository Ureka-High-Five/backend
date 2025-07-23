FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk update && apk add curl

COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

COPY alert-watcher.sh /app/alert-watcher.sh
RUN chmod +x /app/alert-watcher.sh

EXPOSE 8080

CMD sh -c "java -jar app.jar & /app/alert-watcher.sh"
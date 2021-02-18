FROM openjdk:15-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${BD_URL} ${BD_USERNAME} ${DRIVER} ${BD_PASSWORD} ${BOT_USERNAME} ${BOT_TOKEN} -jar /app.jar"]
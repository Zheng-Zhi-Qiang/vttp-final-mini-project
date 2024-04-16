FROM maven:3-eclipse-temurin-21 AS builder2

WORKDIR /app

COPY backend/.mvn .mvn
COPY backend/src src
COPY backend/pom.xml .
COPY backend/mvnw .

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-slim

WORKDIR /apps

COPY --from=builder2 /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080 
ENV SPRING_RABBITMQ_HOST= SPRING_RABBITMQ_PORT= OKTA_OAUTH2_AUDIENCE= OKTA_OAUTH2_ISSUER= SPRING_DATA_MONGODB_URI=
ENV SPRING_DATASOURCE_URL= SPRING_DATASOURCE_USERNAME= SPRING_DATASOURCE_PASSWORD= S3_ACCESS_KEY= S3_SECRET_KEY=
ENV AUTH0_MANAGEMENT_API_CLIENT_SECRET= AUTH0_MANAGEMENT_API_CLIENT_ID= AUTH0_MANAGEMENT_API_AUDIENCE=
ENV AUTH0_MANAGEMENT_API_CLIENT_GRANT_TYPE= AUTH0_MANAGEMENT_API_DOMAIN= RABBITMQ_QUEUE= RABBITMQ_TOPIC_EXCHANGE=
ENV RABBITMQ_ROUTING_KEY= STRIPE_API_KEY= FCM_SERVER_KEY= SPRING_MAIL_HOST= SPRING_MAIL_PORT=0
ENV SPRING_MAIL_USERNAME= SPRING_MAIL_PASSWORD= APP_URL= SPRING_RABBITMQ_USERNAME= SPRING_RABBITMQ_PASSWORD=
ENV SPRING_REDIS_HOST= SPRING_REDIS_PORT= SPRING_REDIS_USERNAME= SPRING_REDIS_PASSWORD=

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar
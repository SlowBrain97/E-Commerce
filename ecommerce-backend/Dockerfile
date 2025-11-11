
FROM maven:3.9.2-eclipse-temurin-21 AS build
WORKDIR /app


COPY pom.xml .

RUN mvn dependency:go-offline -B


COPY src ./src


RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


ENV JAVA_OPTS=""


ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]

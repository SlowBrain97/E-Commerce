
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app


COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .


RUN ./mvnw dependency:go-offline -B


COPY src ./src


RUN ./mvnw clean package -DskipTests -B


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]


FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app


RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*


COPY pom.xml .


RUN mvn dependency:go-offline -B


COPY src ./src


RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


ENV JAVA_OPTS=""


ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]

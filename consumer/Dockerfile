FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests -B -pl consumer -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/consumer/target/consumer*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests -B -pl provider -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/provider/target/provider*.jar app.jar
EXPOSE 20880
ENTRYPOINT ["java", "-jar", "app.jar"]

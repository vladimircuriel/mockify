
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-ubi10-minimal AS runtime

WORKDIR /app
COPY --from=build /app/target/mockify-2.0.0.jar .
CMD ["java", "-jar", "mockify-2.0.0.jar"]
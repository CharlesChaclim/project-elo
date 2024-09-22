FROM maven:3.8.6-amazoncorretto-17 AS build

WORKDIR /app

COPY backend/pom.xml .

RUN mvn dependency:go-offline

COPY backend/src ./src

RUN mvn package -DskipTests

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

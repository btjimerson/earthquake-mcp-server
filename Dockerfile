FROM maven:3.9.11-eclipse-temurin-25 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM eclipse-temurin:25-noble
RUN apt update
WORKDIR /opt
EXPOSE 8080
COPY --from=build /home/app/target/*.jar /opt/app.jar
CMD ["java", "-jar", "/opt/app.jar"]
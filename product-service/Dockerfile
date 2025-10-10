FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/product-service-0.1.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

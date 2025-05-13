FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY api-gateway/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
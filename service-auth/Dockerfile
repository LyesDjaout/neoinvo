FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY service-auth/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
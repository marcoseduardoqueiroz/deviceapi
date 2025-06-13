# Etapa 1: Build com Maven
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime com JDK leve
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Porta correta (8080)
EXPOSE 8080

# Inicia a aplicação na porta 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080"]

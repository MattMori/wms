# --- Etapa 1: Construção (Build) ---
# Usa uma imagem oficial do Maven com Java 21 para compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia tudo do projeto para dentro do container
COPY . .

# Faz o build (pula os testes para ser mais rápido e não dar erro de banco)
RUN mvn clean package -DskipTests

# --- Etapa 2: Execução (Run) ---
# Usa uma imagem leve do Java 21 apenas para rodar
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Pega o arquivo .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Libera a porta 8080
EXPOSE 8080

# Comando para iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]
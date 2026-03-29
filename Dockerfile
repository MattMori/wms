# Estágio 1: Build 
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Cache de dependências 
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copia o código e gera o artefato
COPY src ./src
RUN mvn clean package -DskipTests -B

# Estágio 2: Runtime 
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1.Instalar certificados CA e garantir que o Java os reconheça
# O pacote ca-certificates é essencial para o SSL do NeonDB.
USER root
RUN apk add --no-cache ca-certificates && update-ca-certificates

# 2. Segurança: Criar usuário comum (mantido seu padrão)
RUN addgroup -S wmsgroup && adduser -S wmsuser -G wmsgroup
USER wmsuser

# 3. Copia apenas o .jar final do estágio de build
COPY --from=build /app/target/*.jar app.jar

# 4. Configurações de porta e execução
EXPOSE 8080

# Adicionei a flag de DNS para evitar cache de IP do Neon, que muda com frequência (proxy)
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=60", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
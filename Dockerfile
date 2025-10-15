# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar apenas pom.xml primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (pulando testes para acelerar o build)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Criar usuário não-root para segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Configurar JVM para containers
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

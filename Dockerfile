# Etapa 1: Construcción usando el Maven Wrapper de tu proyecto
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . .
# Le damos permisos al wrapper y compilamos
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Servidor ligero final)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
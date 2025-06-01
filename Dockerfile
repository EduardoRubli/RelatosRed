# Etapa de construcción
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiar el proyecto completo
COPY . .

# Construir la app (sin tests para acelerar)
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar solo el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

# Construcción con Maven usando Eclipse Temurin JDK 17.
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Crear directorio de trabajo.
WORKDIR /app

# Copia solo los archivos necesarios para descargar dependencias.
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (capa cacheable)
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código fuente
COPY src ./src

# Construir el proyecto.
RUN ./mvnw clean package -DskipTests -B

# Imagen de ejecución ligera con JDK 17.
FROM eclipse-temurin:17-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR
COPY --from=build /app/target/*.jar ./app.jar

# Exponer puerto
EXPOSE 8080

# Usar variables de entorno de Railway
ENTRYPOINT ["java", "-jar", "app.jar"]

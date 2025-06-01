# Etapa de construcción.
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar solo los archivos necesarios para la caché.
COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .

# Descargar dependencias (capa cacheable).
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente.
COPY src ./src

# Construir el proyecto optimizando para producción.
RUN ./mvnw clean package -DskipTests -B \
    -Dspring-boot.repackage.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true

# Etapa de producción.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR minimizado.
COPY --from=build /app/target/*.jar ./app.jar

# Configurar JVM para producción.
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+IdleTuningCompactOnIdle -XX:+IdleTuningGcOnIdle"

# Exponer puerto.
EXPOSE 8080

# Usar variables de entorno.
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

# Etapa 1: construcción con Maven usando Eclipse Temurin JDK 21 (alpine)
FROM eclipse-temurin:21-jdk-alpine AS build

# Instalar herramientas necesarias para Maven Wrapper (tar, gzip)
RUN apk add --no-cache tar gzip

# Crear directorio de trabajo
WORKDIR /app

# Copiar los archivos del Maven Wrapper y pom.xml para descargar dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargar dependencias sin compilar (capa cacheable)
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código fuente
COPY src ./src

# Construir el proyecto empaquetando el JAR (se omiten tests para acelerar)
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: imagen de ejecución ligera con JDK 21 (alpine)
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo en el contenedor de ejecución
WORKDIR /app

# Copiar el JAR resultante desde la etapa de construcción
COPY --from=build /app/target/*.jar ./app.jar

# (Opcional) Exponer el puerto de la app; Spring Boot usa 8080 por defecto.
EXPOSE 8080

# Usar variables de entorno de Railway en tiempo de ejecución. Por ejemplo:
#   - La aplicación lee server.port desde ${PORT} (configuración en application.properties).
#   - Las variables MYSQLHOST, MYSQLPORT, MYSQLUSER, MYSQLPASSWORD, MYSQLDATABASE se usan en conexión JDBC.
# El Dockerfile no fija estas variables; Railway las inyectará al correr el contenedor.
#
# Comando por defecto: ejecutar el JAR empaquetado
ENTRYPOINT ["java", "-jar", "app.jar"]

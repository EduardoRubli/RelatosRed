# Etapa de construcción
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiar solo lo necesario para caché.
COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .
RUN chmod +x mvnw

# Descargar dependencias sin compilar.
RUN ./mvnw dependency:go-offline -B

# Copiar el proyecto completo
COPY . .

# Construir la app (sin tests para acelerar)
RUN ./mvnw clean package -DskipTests -B \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true

# Etapa de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar solo el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

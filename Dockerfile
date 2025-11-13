# Imagen base de Java
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiar solo el jar compilado desde target/
COPY target/*.jar app.jar

# Exponer el puerto donde corre Spring Boot
EXPOSE 8085

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

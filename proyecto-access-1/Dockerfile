# Usar una imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR al contenedor
COPY target/practica-access-1-0.0.1-SNAPSHOT.jar app.jar

# Configurar la variable de entorno para la base de datos
ENV SPRING_DATASOURCE_URL=jdbc:ucanaccess:///app/database/Prod_Data.mdb

# Exponer el puerto de la aplicación
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
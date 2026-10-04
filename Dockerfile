# ─────────────────────────────────────────────
# STAGE 1: Build con Maven
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copiar solo el pom primero: las dependencias quedan en caché
# mientras el pom no cambie.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Separar el JAR en capas (dependencias / código) para que Docker
# reutilice la capa de dependencias entre despliegues.
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination extracted

# ─────────────────────────────────────────────
# STAGE 2: Imagen final liviana solo con JRE
# ─────────────────────────────────────────────
FROM eclipse-temurin:25-jre

WORKDIR /app

# No ejecutar como root
RUN useradd --system --no-create-home spring
USER spring

COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

# Render indica el puerto en la variable PORT (ver server.port en application.yaml)
EXPOSE 8080

# MaxRAMPercentage: usa el 75% de la memoria del contenedor para el heap
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

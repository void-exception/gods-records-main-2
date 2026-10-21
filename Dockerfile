# ---------- Этап 1: сборка ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Сначала только pom.xml — зависимости кешируются, пока pom не менялся
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Затем исходники
COPY src ./src
# maven.test.skip=true — тесты не собираем и не компилируем
RUN mvn -B -q clean package -Dmaven.test.skip=true

# ---------- Этап 2: запуск ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Не запускаем приложение от root
RUN useradd --system --create-home appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

# Render передаёт порт через переменную PORT
ENV PORT=8080
# В контейнере используем PostgreSQL (application-prod.properties).
# Параметры подключения (DB_URL, DB_USER, DB_PASSWORD) задаются в Render.
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# forward-headers-strategy=framework — чтобы за прокси Render редиректы
# (например, после логина) шли по https, а не по http
CMD ["sh", "-c", "java -XX:MaxRAMPercentage=75 -Dserver.port=${PORT} -Dserver.forward-headers-strategy=framework -jar app.jar"]

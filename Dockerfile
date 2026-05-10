FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build -- --configuration production

FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /app/backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q
COPY backend/src ./src
COPY --from=frontend /app/frontend/dist/app ./src/main/resources/static
RUN mvn clean package -DskipTests -q


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /app/backend/target/*.jar mailtrack.jar
ENTRYPOINT ["java", "-jar", "mailtrack.jar"]

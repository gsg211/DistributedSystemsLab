FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon
COPY src ./src
RUN gradle dependencies --no-daemon || true
RUN ./gradlew bootJar --no-daemon -x test -Porg.gradle.java.installations.auto-download=false

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app


COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
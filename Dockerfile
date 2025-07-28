# 빌드 스테이지
FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 실행 스테이지
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENV JAVA_OPTS="-Xmx512m -Xms256m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
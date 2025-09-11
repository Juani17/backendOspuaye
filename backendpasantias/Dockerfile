FROM openjdk:17-jdk-slim

COPY build/libs/*[!-plain].jar backendospuaye.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "backendospuaye.jar"]

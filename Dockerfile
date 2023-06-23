# docker build -t svip .
# docker run -p 8080:8080 svip
FROM gradle:8.1.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/api/build/libs/api-1.0.0-alpha.jar /app/SVIP.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/SVIP.jar"]
# ENTRYPOINT ["java", "-jar", "/app/SVIP.jar"]

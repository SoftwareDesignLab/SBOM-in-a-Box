#
# File: Dockerfile
# The Dockerfile to build and run the SVIP API inside of a Docker container.
#
# Author: Ian Dunn
#

FROM gradle:8.1.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Append "-x test" argument to skip tests; this is useful for development builds.
# Docker takes ~3-5m to build the initial image, it's faster than waiting
RUN gradle build --no-daemon -x test # todo re comment before merge to dev

FROM openjdk:17-jdk-slim

EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/api/build/libs/api-1.0.0-alpha.jar /app/SVIP.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/SVIP.jar"]
# ENTRYPOINT ["java", "-jar", "/app/SVIP.jar"]

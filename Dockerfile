# File: Dockerfile
# The Dockerfile to build and run the SVIP API inside of a Docker container.
#
# Author: Ian Dunn
# Author: Derek Garcia

FROM gradle:8.14-jdk21-alpine AS build
WORKDIR /tmp/sbox
COPY --chown=gradle:gradle api api
COPY --chown=gradle:gradle core core
COPY --chown=gradle:gradle settings.gradle .
# Append "-x test" argument to skip tests; this is useful for development builds.
# Docker takes ~3-5m to build the initial image, it's faster than waiting
RUN gradle build --no-daemon # -x test


FROM eclipse-temurin:21-jre-alpine-3.21 AS runtime
# create user
RUN adduser -H -D sbox
USER sbox
# copy jar
WORKDIR /app
COPY --from=build /tmp/sbox/api/build/libs/api-1.0.0-alpha.jar /app/sbox.jar
# launch api server
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","sbox.jar"]
# ENTRYPOINT ["java", "-jar", "sbox.jar"]

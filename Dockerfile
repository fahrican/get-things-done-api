FROM openjdk:20-slim as builder

WORKDIR /build
ADD . .

RUN ./gradlew build

FROM openjdk:20-slim

RUN groupadd -r app && useradd --no-log-init -r -g app app

WORKDIR /app
COPY --from=builder /build/build/libs/get_things_done-0.0.1-SNAPSHOT.jar get_things_done.jar

USER app
ENTRYPOINT ["java", "-jar", "-DSERVER_PORT=${SERVER_PORT}", \
                            "-DSPRING_JPA_DATABASE=${JPA_DATABASE}", \
                            "-DSPRING_DATASOURCE_URL=${DATASOURCE_URL}", \
                            "-DSPRING_DATASOURCE_USERNAME=${DATASOURCE_USERNAME}", \
                            "-DSPRING_DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}",   "get_things_done.jar"]
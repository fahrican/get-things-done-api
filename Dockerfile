FROM openjdk:20-slim as builder

WORKDIR /build
ADD . .

RUN ./gradlew build

FROM openjdk:20-slim

RUN groupadd -r app && useradd --no-log-init -r -g app app

WORKDIR /app
COPY --from=builder /build/build/libs/get_things_done-0.0.1-SNAPSHOT.jar get_things_done.jar

USER app
ENTRYPOINT ["java", "-jar", "get_things_done.jar"]
# define base docker image
FROM openjdk:11
LABEL maintainer="onecosys.com"
ADD build/libs/get_things_done-0.0.1-SNAPSHOT.jar get_things_done.jar
ENTRYPOINT ["java", "-jar", "get_things_done.jar"]
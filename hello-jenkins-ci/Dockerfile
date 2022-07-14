FROM openjdk:8-jdk-alpine as build

###### author name #######
MAINTAINER SURAJIT

WORKDIR /app

# ARG variable available at the time of creating image 
ARG config_file=./customers.json
COPY ${config_file} .

ARG JAR_FILE=target/*.jar 

ARG EXTERNAL_PORT=8080
EXPOSE ${EXTERNAL_PORT}

RUN echo "Docker container will listen to EXPOSED_PORT : ${EXTERNAL_PORT}"

ADD ${JAR_FILE} app.jar

ARG default_profile=Development
ARG default_msg=Albal

# ENV to be available when container is Live
ENV SPRING_PROFILE=${default_profile}
ENV WELCOME_MSG=${default_msg}
ENV JSON_CONFIG=${config_file}

ENTRYPOINT java -jar --spring.profiles.active=${SPRING_PROFILE} --welcome.salutation=${WELCOME_MSG:-BAPOK} --my.json.config=${JSON_CONFIG} app.jar
# ENTRYPOINT java -jar -Dspring.profiles.active=${SPRING_PROFILE} -Dwelcome.salutation=${WELCOME_MSG:-BAPOK} -Dmy.json.config=${JSON_CONFIG} app.jar 

# docker build --build-arg default_profile=Development --build-arg default_msg=HELLO -t jeetsuro/hello-jenkins-ci:v1 .
# docker run --rm -p 9090:8080 -d -e SPRING_PROFILE=DEVELOPMENT -e WELCOME_MSG=Hello-Kelo jeetsuro/hello-jenkins-ci:v1
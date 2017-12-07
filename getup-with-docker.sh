#!/bin/bash

sh mvnw clean install

docker-compose rm -f
docker rmi -f backendscoretest_app
docker-compose up -d --force-recreate

#docker rm -f score-app

#docker build score-module-application -t score-app
#docker run -t score-app

#!/bin/bash -e

sh mvnw clean install

docker-compose rm -f
docker rmi -f backendscoretest_app
docker-compose up -d --force-recreate
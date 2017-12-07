#!/bin/bash -e

if [[ $* == *--debug* ]]
then
    mvnDebug spring-boot:run -Dspring.profiles.active=dev -f score-module-application/pom.xml
else
    ./mvnw spring-boot:run -Dspring.profiles.active=dev -f score-module-application/pom.xml
fi
FROM openjdk:8-jdk-alpine

ADD ./target/score-application.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=docker", "-jar","/app.jar >  /logs/application.log"]
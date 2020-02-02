FROM maven:3.3-jdk-8 as build
COPY . /app
WORKDIR /app
RUN mvn package && \
apk add --no-cache rsyslog && \
cat

FROM openjdk:8-jdk-alpine
EXPOSE 8100 514
WORKDIR /myapp
COPY --from=build /app/target/auth-course-0.0.1-SNAPSHOT.jar .
CMD java -jar auth-course-0.0.1-SNAPSHOT.jar

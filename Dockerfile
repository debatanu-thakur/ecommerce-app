FROM maven:3.3-jdk-8 as build
COPY . /app
WORKDIR /app
RUN mvn package

FROM openjdk:8-jdk-alpine
EXPOSE 8100
WORKDIR /myapp
COPY --from=build /app/target/auth-course-0.0.1-SNAPSHOT.jar .
CMD java -jar auth-course-0.0.1-SNAPSHOT.jar

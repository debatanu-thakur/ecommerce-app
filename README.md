# eCommerce Application
This is a sample application of an e-commerce website. This created using spring boot mvc and h2 database

## Background
The features include
1. Spring boot mvc using CRUD
2. Tests coverage 60%
3. JWT tokenization including authentication and authorization
4. Logging using sfl4j
5. Jenkins build and deployment
6. Splunk indexing and dashboard

## How To Run
1. Have Java 1.8 installed
2. To test, run `mvn test`
3. To run the application make sure port 8100 is available, and then run `mvn spring-boot:run` or `docker-compose up -d`
4. Then you can test the application in http://localhost:8100
5. The jenkins build files are found in `jenkins_home` folder and the splunk screenshots in `dashboard` folder.

### License
MIT
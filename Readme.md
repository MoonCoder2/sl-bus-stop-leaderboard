## Prerequisite 
Java 17

## To run
1. git clone
2. set environment variable apiKey <br />
Eg in windows: $env:apiKey = '<...>' <br />
in Mac: export apiKey='<...>'
3. mvn spring-boot:run -Pprod
4. FE url http://localhost:8080 
5. BE swagger url http://localhost:8080/swagger-ui/index.html

## Under the hood
FE is based on react, the source code is available under /app. <br />
frontend-maven-plugin is used to integrate it with Spring Boot.

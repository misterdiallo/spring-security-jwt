FROM openjdk:17
ADD target/spring-security-jwt.jar spring-security-jwt.jar
ENTRYPOINT ["java", "-jar","spring-security-jwt.jar"]
EXPOSE 8080
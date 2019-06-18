
FROM openjdk:8
COPY /target/reports-core-0.0.1-SNAPSHOT.jar /user/src/
WORKDIR /user/src/
CMD ["java", "-jar", "reports-core-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080
EXPOSE 8081
EXPOSE 8095
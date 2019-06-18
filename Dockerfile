FROM maven:3.5-jdk-8 as builder
RUN mkdir /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app

RUN  mvn clean
RUN  mvn package

FROM tomcat:8.0.47-jre8-alpine
RUN apk --update add fontconfig ttf-dejavu
RUN apk --no-cache add msttcorefonts-installer fontconfig && \
    update-ms-fonts && \
    fc-cache -f
COPY --from=builder  /usr/src/app/target/reports-core.jar /user/src/
WORKDIR /user/src/
CMD ["java", "-jar", "reports-core.jar --spring.profiles.active=production"]



EXPOSE 8080
EXPOSE 8081
EXPOSE 8095
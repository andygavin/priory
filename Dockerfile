FROM openjdk:8-alpine

COPY target/uberjar/priory.jar /priory/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/priory/app.jar"]

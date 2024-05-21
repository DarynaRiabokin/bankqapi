FROM chainguard/jdk-lts
VOLUME /tmp
COPY target/banq-0.0.1-SNAPSHOT.jar bankqapi.jar
ENTRYPOINT ["java","-jar","bankqapi.jar"]
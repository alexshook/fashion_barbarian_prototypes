FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/plebs-dept-clojure-0.0.1-SNAPSHOT-standalone.jar /plebs-dept-clojure/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/plebs-dept-clojure/app.jar"]

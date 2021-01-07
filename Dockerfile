FROM maven:3.6.3-openjdk-11-slim as DEPENDENCIES
WORKDIR /opt/app
COPY zookeeper-client/pom.xml zookeeper-client/pom.xml
COPY pom.xml .
RUN mvn -B -e -C org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

# Copy the dependencies from the DEPS stage with the advantage
# of using docker layer caches. If something goes wrong from this
# line on, all dependencies from DEPS were already downloaded and
# stored in docker's layers.
FROM maven:3.6.3-openjdk-11-slim as BUILDER
WORKDIR /opt/app
COPY --from=DEPENDENCIES /root/.m2 /root/.m2
COPY --from=DEPENDENCIES /opt/app/ /opt/app
COPY zookeeper-client/src /opt/app/zookeeper-client/src
RUN mvn -B -e clean package -DskipTests=true

FROM azul/zulu-openjdk-alpine:11-jre-headless
WORKDIR /opt/app
COPY --from=BUILDER /opt/app/zookeeper-client/target/zookeeper-client.jar .
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "zookeeper-client.jar"]
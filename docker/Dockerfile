FROM hseeberger/scala-sbt:11.0.9.1_1.4.3_2.13.4 as build

COPY docker/repositories /root/.sbt/repositories
COPY build.sbt /src/build.sbt
COPY project/build.properties /src/project/build.properties
COPY project/plugins.sbt /src/project/plugins.sbt
ADD src /src/src
WORKDIR /src
RUN sbt clean assembly

FROM openjdk:11-jre as release
COPY --from=build /src/target/scala-2.12/analyzer-assembly-0.1.jar /analyzer.jar
COPY docker/entrypoint.sh /bin/entrypoint.sh
ENTRYPOINT [ "/bin/entrypoint.sh" ]

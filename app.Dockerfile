FROM gradle:6.7-jdk8

ADD src src
ADD build.gradle .
ADD settings.gradle .
RUN gradle shadowJar

FROM openjdk:12-jdk-alpine
COPY --from=0 /home/gradle/build/libs/legacy-asset-move-1.0-all.jar app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
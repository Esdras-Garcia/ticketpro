FROM gradle:jdk8 AS BUILD
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle war --no-daemon

FROM tomcat:9.0-jre8-openjdk
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /home/gradle/src/build/libs/*.war /usr/local/tomcat/ebapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
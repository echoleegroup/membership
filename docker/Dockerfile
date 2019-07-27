FROM frolvlad/alpine-oraclejdk8:slim
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Taipei /etc/localtime

ENV APP_HOME=/Users/beau/Downloads/git/newGit/java/MembershipGit
WORKDIR $APP_HOME

ADD build/libs/* aviopoint.war
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${version:develop}", "-jar","aviopoint.war"]
FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get install -y unzip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk8-installer;

RUN apt-get update && \
    apt-get install -y ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk8-installer;

RUN \
  mkdir /travel && \
  mkdir /travel/data && \
  mkdir /travel/app && \
  mkdir /tmp/data

ADD travel-1.0.jar /travel/app
ADD start.sh /travel/app
# ADD data.zip /tmp/data

RUN \
  chmod +x /travel/app/start.sh

# Define working directory.
WORKDIR /travel/app

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/

EXPOSE 80

# Define default command.
CMD /travel/app/start.sh

FROM ubuntu:latest

ENV HADOOP_DIST https://archive.apache.org/dist/hadoop/common/hadoop
ENV MAVEN_DIST https://dlcdn.apache.org/maven/maven-3

ENV HADOOP_VERSION 3.4.0
ENV MAVEN_VERSION 3.9.9

ENV INITRD no
ENV DEBIAN_FRONTEND noninteractive

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV HADOOP_HOME /usr/local/hadoop
ENV MAVEN_HOME /usr/local/maven
ENV PATH $PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$MAVEN_HOME/bin

COPY ./build-scripts/*.sh /build-scripts/
COPY ./fixes/ /fixes/
COPY ./config/ /tmp/
COPY ./start.sh /start.sh
COPY ./downloads/ /downloads/

# Install hadoop

# --- Online method: might be slow if you want rebuild the image regularly using UTD's internet
# ADD $HADOOP_DIST-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz /
# RUN tar -x -z -f /hadoop-$HADOOP_VERSION.tar.gz && mv /hadoop-$HADOOP_VERSION $HADOOP_HOME

# --- Online once then offline method: if you want to rebuild the image regularly
RUN tar -x -z -f /downloads/hadoop-$HADOOP_VERSION.tar.gz && mv /hadoop-$HADOOP_VERSION $HADOOP_HOME

# Install maven
ADD $MAVEN_DIST/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz /
RUN tar -x -z -f /apache-maven-$MAVEN_VERSION-bin.tar.gz && mv /apache-maven-$MAVEN_VERSION $MAVEN_HOME

RUN chmod +x /build-scripts/prepare.sh
RUN chmod +x /start.sh

RUN /build-scripts/prepare.sh 

CMD ["/bin/bash", "/start.sh"]

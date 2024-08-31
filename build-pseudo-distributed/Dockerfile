FROM ubuntu:latest

ENV HADOOP_VERSION 3.4.0

ENV INITRD no
ENV DEBIAN_FRONTEND noninteractive

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV HADOOP_HOME /usr/local/hadoop
ENV PATH $PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

COPY ./build-scripts/*.sh /build-scripts/
COPY ./config/ /tmp/
COPY ./start.sh /start.sh

RUN chmod +x /build-scripts/prepare.sh
RUN chmod +x /start.sh

RUN /build-scripts/prepare.sh 

CMD ["/bin/bash", "-l", "-c", "/start.sh"]

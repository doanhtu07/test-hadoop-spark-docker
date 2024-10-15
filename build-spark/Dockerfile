FROM bitnami/spark

USER root

COPY ./fixes/ /fixes/
COPY ./config/ /config/

# Issue with pyspark: https://github.com/bitnami/containers/issues/38139

RUN apt-get update && apt-get install -y --no-install-recommends \
    sudo \
    vim \
    ncurses-bin

RUN rm -rf /tmp/* /var/tmp/*
RUN rm -rf /var/lib/apt/lists/*
RUN apt-get clean

RUN mv /fixes/pyspark/pyspark-new.sh /opt/bitnami/spark/bin/pyspark
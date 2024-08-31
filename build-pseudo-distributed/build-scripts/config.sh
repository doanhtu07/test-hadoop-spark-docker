# This file intended to be sourced

HADOOP_DIST="https://dlcdn.apache.org/hadoop/common/hadoop"

minimal_apt_get_args='-y --no-install-recommends'

## Build time dependencies ##
BUILD_PACKAGES="curl"

## Build and run time dependencies
BUILD_RUN_PACKAGES="openssh-server openssh-client sudo"

## Run time dependencies ##
# RUN_PACKAGES="openjdk-11-jre-headless"
RUN_PACKAGES="openjdk-11-jdk-headless iproute2 vim"

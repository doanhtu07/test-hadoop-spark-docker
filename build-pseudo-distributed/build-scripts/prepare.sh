#!/bin/bash -xe

# Configure our environment
. /build-scripts/config.sh

# Install build dependencies
apt-get update -y
apt-get install $minimal_apt_get_args $BUILD_PACKAGES $BUILD_RUN_PACKAGES

# Add ssh config for passphraseless ssh
ssh-keygen -t rsa -N "" -f $HOME/.ssh/id_rsa
cat $HOME/.ssh/id_rsa.pub >>$HOME/.ssh/authorized_keys
mv /tmp/ssh_config $HOME/.ssh/config

# Move our hadoop config files into place
mv /tmp/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
mv /tmp/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml
mv /tmp/mapred-site.xml $HADOOP_HOME/etc/hadoop/mapred-site.xml
mv /tmp/yarn-site.xml $HADOOP_HOME/etc/hadoop/yarn-site.xml

# Configure JAVA_HOME for hadoop
sed -i "s,^. export JAVA_HOME.*,export JAVA_HOME=$JAVA_HOME," $HADOOP_HOME/etc/hadoop/hadoop-env.sh

# Set root user for hadoop
# Change / create these user settings
sed -i '/^export HDFS_NAMENODE_USER=.*/{s/.*/export HDFS_NAMENODE_USER=root/;h};${x;/^$/{s//export HDFS_NAMENODE_USER=root/;H};x}' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
sed -i '/^export HDFS_DATANODE_USER=.*/{s/.*/export HDFS_DATANODE_USER=root/;h};${x;/^$/{s//export HDFS_DATANODE_USER=root/;H};x}' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
sed -i '/^export HDFS_SECONDARYNAMENODE_USER=.*/{s/.*/export HDFS_SECONDARYNAMENODE_USER=root/;h};${x;/^$/{s//export HDFS_SECONDARYNAMENODE_USER=root/;H};x}' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
sed -i '/^export YARN_RESOURCEMANAGER_USER=.*/{s/.*/export YARN_RESOURCEMANAGER_USER=root/;h};${x;/^$/{s//export YARN_RESOURCEMANAGER_USER=root/;H};x}' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
sed -i '/^export YARN_NODEMANAGER_USER=.*/{s/.*/export YARN_NODEMANAGER_USER=root/;h};${x;/^$/{s//export YARN_NODEMANAGER_USER=root/;H};x}' $HADOOP_HOME/etc/hadoop/hadoop-env.sh

if [ ! -e "~/.bashrc" ]; then
  touch ~/.bashrc
fi

cat >~/.bashrc <<EOF
INITRD=no
DEBIAN_FRONTEND=noninteractive

JAVA_HOME=$JAVA_HOME
HADOOP_HOME=$HADOOP_HOME
MAVEN_HOME=$MAVEN_HOME
PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$MAVEN_HOME/apache-maven-$MAVEN_VERSION/bin

export INITRD
export DEBIAN_FRONTEND

export JAVA_HOME
export HADOOP_HOME
export MAVEN_HOME
export PATH
EOF

if [ ! -e "~/.bash_profile" ]; then
  touch ~/.bash_profile
fi

cat ~/.bash_profile <<EOF
if [ -f ~/.bashrc ]; then
  . ~/.bashrc
fi
EOF

# Set permissions for ssh
chmod 700 $HOME/.ssh
chmod 600 $HOME/.ssh/authorized_keys

# Set permissions for hadoop
chmod 744 -R $HADOOP_HOME

# Install the run-time dependencies
apt-get install $minimal_apt_get_args $RUN_PACKAGES

# Remove build-time dependencies
apt-get remove --purge -y $BUILD_PACKAGES

# Uninstall packages that are no longer necessary, clean temporary files
rm -rf /tmp/* /var/tmp/*
apt-get clean
rm -rf /var/lib/apt/lists/*

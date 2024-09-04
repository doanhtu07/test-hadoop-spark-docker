sed -i "s,/usr/lib/jvm/java-11-openjdk-amd64,/usr/lib/jvm/java-11-openjdk-arm64,g" $HOME/.bashrc $HADOOP_HOME/etc/hadoop/hadoop-env.sh
source $HOME/.bashrc

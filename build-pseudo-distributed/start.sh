service ssh start

echo "================================================================================"
echo "                              Hadoop Docker Container                           "
echo "================================================================================"
echo

start_dfs(){
    echo "Starting HDFS..."
    
    if [ ! -d "/hdfs/namenode/" ]; then
        echo "Creating /hdfs/namenode/ directory..."
        mkdir -p "/hdfs/namenode/"
    fi
    
    # Create /hdfs/datanode/ directory if it doesn't exist
    if [ ! -d "/hdfs/datanode/" ]; then
        echo "Creating /hdfs/datanode/ directory..."
        mkdir -p "/hdfs/datanode/"
    fi

    if [ ! -d "/hdfs/namenode/current" ] || [ -z "$(ls -A /hdfs/namenode/current)" ]; then
        echo "Formatting NameNode..."
        yes "Y" | ${HADOOP_HOME}/bin/hdfs namenode -format
    else
        echo "NameNode repository is not empty. Skipping formatting."
    fi

    ${HADOOP_HOME}/sbin/start-dfs.sh
}

start_yarn(){
    echo "Starting YARN..."
    $HADOOP_HOME/sbin/start-yarn.sh
}

start_dfs
start_yarn

tail -f /dev/null

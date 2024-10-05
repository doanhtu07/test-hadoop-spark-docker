source /src/homework1/scripts/Q1c-clean.sh

cd /src/homework1 && mvn clean && mvn package

HDFS_DIR="/input/hw1/"

LOCAL_PATH="/src/io/hw1/input_hw1.txt"
HDFS_PATH="/input/hw1/input_hw1.txt"

# Check if the file exists in HDFS
hdfs dfs -test -e $HDFS_PATH

# $? holds the exit status of the last command executed
if [ $? -eq 0 ]; then
    echo "File already exists in HDFS at $HDFS_PATH"
else
    echo "File does not exist in HDFS. Uploading now..."
    # Put the file into HDFS
    hdfs dfs -mkdir -p $HDFS_DIR
    hdfs dfs -put $LOCAL_PATH $HDFS_DIR
    echo "File uploaded to HDFS successfully."
fi

hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1c $HDFS_PATH /output/hw1/Q1c/

hdfs dfs -get /output/hw1/Q1c/part-r-00000 /src/io/hw1/Q1c-output.txt

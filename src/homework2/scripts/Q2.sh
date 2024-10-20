source /src/homework2/scripts/Q2-clean.sh

cd /src/homework2 && mvn clean && mvn package

HDFS_DIR="/input/hw2/"

LOCAL_CITY_PATH="/src/io/hw2/city_temperature.csv"
HDFS_CITY_PATH="/input/hw2/city_temperature.csv"

LOCAL_COUNTRY_PATH="/src/io/hw2/country-list.csv"
HDFS_COUNTRY_PATH="/input/hw2/country-list.csv"

# Check if the file exists in HDFS
hdfs dfs -test -e $HDFS_CITY_PATH

# $? holds the exit status of the last command executed
if [ $? -eq 0 ]; then
    echo "File already exists in HDFS at $HDFS_CITY_PATH"
else
    echo "File does not exist in HDFS. Uploading now..."
    # Put the file into HDFS
    hdfs dfs -mkdir -p $HDFS_DIR
    hdfs dfs -put $LOCAL_CITY_PATH $HDFS_DIR
    echo "File uploaded to HDFS successfully."
fi

# Check if the file exists in HDFS
hdfs dfs -test -e $HDFS_COUNTRY_PATH

# $? holds the exit status of the last command executed
if [ $? -eq 0 ]; then
    echo "File already exists in HDFS at $HDFS_COUNTRY_PATH"
else
    echo "File does not exist in HDFS. Uploading now..."
    # Put the file into HDFS
    hdfs dfs -mkdir -p $HDFS_DIR
    hdfs dfs -put $LOCAL_COUNTRY_PATH $HDFS_DIR
    echo "File uploaded to HDFS successfully."
fi

hadoop jar /src/homework2/target/homework2-1.0.0.jar com.homework2.Q2 $HDFS_CITY_PATH $HDFS_COUNTRY_PATH /output/hw2/Q2/

hdfs dfs -get /output/hw2/Q2/part-r-00000 /src/io/hw2/Q2-output.txt

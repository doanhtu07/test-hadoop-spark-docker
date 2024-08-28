# Setup according to Docker official Hadoop image

https://hub.docker.com/r/apache/hadoop

# Source code

https://github.com/apache/hadoop/tree/docker-hadoop-3

- There is a hadoop runner image
- The image that bases on hadoop runner to install hadoop

Hadoop is installed into `/opt/hadoop`, which also means that's `$HADOOP_HOME`

# Run

```
docker compose up -d
```

# Destroy

```
docker compose down
```

# Test

```
cd /opt/hadoop
yarn jar ./share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar pi 10 10
```

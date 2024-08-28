# Setup according to Docker official Hadoop image

https://hub.docker.com/r/apache/hadoop

# Source code

https://github.com/apache/hadoop/tree/docker-hadoop-3

- There is a hadoop runner image (branch `https://github.com/apache/hadoop/tree/docker-hadoop-runner`)

- And the docker hadoop image that bases on hadoop runner to install hadoop

  - Includes `docker-hadoop-3` and `docker-hadoop-2` branches

Hadoop is installed into `/opt/hadoop`, which also means that's the `$HADOOP_HOME`.

# Run

1. Run 4 containers

```
docker compose up -d
```

2. Check containers

```
docker ps -a
```

2. Run a terminal inside any container you want. Remember to use the proper name on your machine

- You should already see the name of the containers after step 2

```
docker exec -it <project-dir>-<container-name> /bin/bash
```

Example: `docker exec -it test-hadoop-docker-namenode-1 /bin/bash`

# Destroy

```
docker compose down
```

# Test

```
cd /opt/hadoop
yarn jar ./share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar pi 10 10
```

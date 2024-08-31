# Setup Hadoop with Docker (in different modes)

Modes: https://blog.naveenpn.com/hadoop-distribution-modes

## Run fully distributed cluster mode

### Source

https://hub.docker.com/r/apache/hadoop

### Source code

https://github.com/apache/hadoop/tree/docker-hadoop-3

- There is a hadoop runner image (branch `https://github.com/apache/hadoop/tree/docker-hadoop-runner`)

- And the docker hadoop image that bases on hadoop runner to install hadoop

  - Includes `docker-hadoop-3` and `docker-hadoop-2` branches

Hadoop is installed into `/opt/hadoop`, which also means that's the `$HADOOP_HOME`.

### Step 1. Run 4 containers

```
docker compose -f ./build-fully-distributed/docker-compose.yml up -d
```

### Step 2. Check containers

```
docker ps -a
```

### Step 3. Run a terminal inside any container you want

- NOTE: Remember to use the proper name on your machine

- You should already see the name of the containers after step 2

```
docker exec -it <container-name> /bin/bash
```

Example: `docker exec -it docker-namenode-1 /bin/bash`

### Step 4. Test run an example

Assuming you are in workdir `/opt/hadoop`, which is also `$HADOOP_HOME`

```
yarn jar ./share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar pi 10 10
```

or

```
yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar pi 10 10
```

### Step 5. Exit out of the docker terminal if you want

```
exit
```

### Destroy containers

```
docker compose down
```

## Run pseudo-distributed mode

### Source code

- https://github.com/loum/hadoop-pseudo/blob/main/Dockerfile
- https://gist.github.com/pedrogomes29/863fc62b3eba55c8b9d53b236ed8e692

### Step 1. Run single hadoop container

```
docker compose -f ./build-pseudo-distributed/docker-compose.yml up -d
```

### Other steps

Pretty much the same as above. Get into the container and play around.

```
docker exec -it build-pseudo-distributed-hadoop-1 /bin/bash
```

### Check processes with jps

```
jps
```

### Check ports

Input the PID in there.

```
ss -tulnp | grep <PID>
```

### Exposed ports

Currently, I've exposed 2 ports:

- 9870: namenode port
- 8088: resource manager port

### Mount code

If you want to mount any code from your machine to Docker, create a folder `src` and write files there.

Let's say you want to write a Java program on your machine and send it to Docker.

You can write it directly under `src`, and your file will magically appear under `/src` in Docker container.

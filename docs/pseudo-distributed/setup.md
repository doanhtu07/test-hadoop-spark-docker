Build pseudo distributed hadoop on docker

# Source code

- https://github.com/loum/hadoop-pseudo/blob/main/Dockerfile
- https://gist.github.com/pedrogomes29/863fc62b3eba55c8b9d53b236ed8e692

# Step 1. Install Hadoop

As of now, I've supported 2 ways to install Hadoop. Choose one!

## A. Fully online

You can let Docker handling downloading Hadoop for you, but this can be **SLOW** considering UTD's internet

- Go to `build-pseudo-distributed/Dockerfile` and find the section below

```Dockerfile
# Install hadoop

# --- Online method: might be slow if you want rebuild the image regularly using UTD's internet
# ADD $HADOOP_DIST-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz /
# RUN tar -x -z -f /hadoop-$HADOOP_VERSION.tar.gz && mv /hadoop-$HADOOP_VERSION $HADOOP_HOME

# --- Online once then offline method: if you want to rebuild the image regularly
RUN tar -x -z -f /downloads/hadoop-$HADOOP_VERSION.tar.gz && mv /hadoop-$HADOOP_VERSION $HADOOP_HOME
```

- Uncomment the `online` method code and comment out the `offline` method

- Follow further steps below

## B. Offline (Partial online)

This method requires you to go to https://hadoop.apache.org/release/3.4.0.html

Technically you only have to download once and reuse that to build further images super **FAST**

- Download the `tar.gz` file option from the link above

- Save that file to `build-pseudo-distributed/downloads/` folder

- Comment out the `online` method and uncomment the `offline` method

- Follow further steps below

# Step 2. Run single hadoop container

```
docker compose -f ./build-pseudo-distributed/docker-compose.yml up -d
```

# Step 3. Execute container's shell

Get into the container and play around.

```
docker exec -it build-pseudo-distributed-hadoop-1 /bin/bash
```

# Step 4. Check processes with jps

```
jps
```

# Step 5. Check ports

Input the PID of processes here to see their ports inside the container

```
ss -tulnp | grep <PID>
```

# Exposed ports

Currently, I've exposed 2 ports:

- 9870: namenode port
- 8088: resource manager port

# Mount code

If you want to mount any code from your machine to Docker, create a folder `src` and write files there.

Let's say you want to write a Java program on your machine and send it to Docker.

You can write it directly under `src`, and your file will magically appear under `/src` in Docker container.

# Issues

## Mac M1 / M2 / M3

When Docker installs openjdk 11 on Mac M1 / M2 / M3, it will probably installs as ARM format

But right now, I set `JAVA_HOME` as `/usr/lib/jvm/java-11-openjdk-amd64`

Same thing as in `/usr/local/hadoop/etc/hadoop/hadoop-env.sh` where the default `JAVA_HOME` is `/usr/lib/jvm/java-11-openjdk-amd64`

So you might want to change it to `/usr/lib/jvm/java-11-openjdk-arm64`

---

1. Run script to fix JAVA_HOME

```
source /fixes/apple-silicon-java-home.sh
```

---

2. Rerun `/start.sh`

So since the JAVA_HOME was not set correctly and you've just fixed it, now we need to start the cluster again using my custom script.

```
/bin/bash /start.sh
```

---

3. Create a new shell with `docker exec` and test commands like `jps` there

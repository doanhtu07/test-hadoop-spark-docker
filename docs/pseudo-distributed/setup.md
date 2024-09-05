Build pseudo distributed hadoop on docker

# Source code

- https://github.com/loum/hadoop-pseudo/blob/main/Dockerfile
- https://gist.github.com/pedrogomes29/863fc62b3eba55c8b9d53b236ed8e692

# Step 1. Run single hadoop container

```
docker compose -f ./build-pseudo-distributed/docker-compose.yml up -d
```

# Step 2. Execute container's shell

Get into the container and play around.

```
docker exec -it build-pseudo-distributed-hadoop-1 /bin/bash
```

# Step 3. Check processes with jps

```
jps
```

# Step 4. Check ports

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

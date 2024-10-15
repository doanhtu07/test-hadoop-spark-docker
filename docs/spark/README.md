# Tutorial

- https://www.youtube.com/watch?v=jrv7J40Qs9A
- https://github.com/bitnami/containers/blob/main/bitnami/spark/docker-compose.yml

# Run commands

```
docker compose -f ./build-spark/docker-compose.yml up -d
docker exec -it build-spark-master-1 /bin/bash
```

# To develop a Python Spark file

You can write code directly in Docker

Or if you want to edit on your machine, you will need pyspark available on your machine

- I install pyspark with `pip install pyspark` inside my conda base environment

- You can also create a python virual environment to isolate your dependencies

# Run pyspark program

```
spark-submit <python file>
```

# Run example WordCount program

## Prereqisite

Before following the guide below, you must have finished setting up Maven project for WordCount.java

If you've not done it yet, follow `maven.md` inside this directory

**NOTE**: By default, my repository already sets this up for you. But you should delete everything inside `src` folder and try again from scratch yourself.

## Learning hdfs

- Create directory inside HDFS

```
hdfs dfs -mkdir <directory name you want to create>
```

- Display all files inside a directory in HDFS. Root directory is `/`

```
hdfs dfs -ls <directory name you want to look inside>
```

- Remove

```
hdfs dfs -rm -r <directory name you want to delete/remove>
```

```
hdfs dfs -rm <file name you want to delete/remove>
```

- Transfer/copy local file to HDFS

```
hdfs dfs -put <filename in local file system/source> <target directory in HDFS>
```

- Transfer/copy HDFS file to local file system

```
hdfs dfs -get <source file in HDFS> <target directory in local file system>
```

**NOTE**: Remember that although you see it seems to be a local system, HDFS is a distributed file system. This is really interesting!!!

## Actual test run

### Step 1. Create a directory in HDFS

```
hdfs dfs -mkdir -p /user/<yourname>/input
```

### Step 2. Copy our input file to HDFS

```
hdfs dfs -put /src/io/test/wordcount-test-input.txt /user/<yourname>/input
```

### Step 3. Run our WordCount jar program

```
hadoop jar <path of the JAR file> <class name> <input path of file in HDFS> <directory you want to output>
```

```
hadoop jar /src/wordcount-test/target/wordcount-test-1.0.0.jar com.wordcount.test.WordCount /user/<yourname>/input/wordcount-test-input.txt /outputCountWords/
```

### Step 4. Get successful job result

```
hdfs dfs -get /outputCountWords/part-r-00000 /src/io/test/wordcount-test-output.txt
```

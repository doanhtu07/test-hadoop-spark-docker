# Create template maven project with archetype

https://maven.apache.org/guides/introduction/introduction-to-archetypes.html

https://medium.com/analytics-vidhya/testing-your-hadoop-program-with-maven-on-intellij-42d534db7974

## Setup maven template

```
mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DjavaCompilerVersion=11
```

**groupId**: com.wordcount.test
**artifactId**: wordcount-test
**version**: 1.0.0

## Setup dependencies with hadoop libraries

- Make sure the versions here (in `pom.xml`) match with the versions inside our `Docker` environment
  - So that it can run smoothly without any dependency conflicts

```xml
<!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-mapreduce-client-core -->
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-mapreduce-client-core</artifactId>
    <version>3.4.0</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common -->
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-common</artifactId>
    <version>3.4.0</version>
</dependency>
```

## Change code to WordCount.java

Copy `WordCount.java` file from example code to `wordcount-test/src/main/java/com/wordcount/test`

## Run package

```
mvn package
```

## The compiled program

It's a jar file inside `target` folder

- `target/wordcount-test-1.0.0.jar`

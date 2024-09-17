# Requirements

My Github repo: https://github.com/doanhtu07/test-hadoop-docker

- Hadoop version 3.4
- Java version 11
- Maven version 3.9.9 (Optional)

I use Maven version 3.9.9 to build my project by the way. You can use the jar file directly or attempt to build my Maven project again. It's your choice.

# Question 1

## Part a

**Goal**: Count the number of occurrences for each word (word count)

- Split the input data by space then count the number of occurrences for each word
- This counting program is case sensitive for the fullest information!

**NOTE**: Part a only needs input path and output path.

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1a /input/hw1/input_hw1.txt /output/hw1/Q1a/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1a /input/hw1/input_hw1.txt /output/hw1/Q1a/
```

## Part b

**Goal**: Find word count of specific words "america", "president", "washington"

- Since the question does not mention anything about case sensitivity, I'm assuming we are looking for non-case-sensitive
- Meaning we are looking for "america", "president", and "washington" as well as other upper and lower case variations
- Because I tested with case-sensitive and got no count for "america" and "washington"
- Please keep this in mind

Same thing as `part a` but change the class name and output path

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1b /input/hw1/input_hw1.txt /output/hw1/Q1b/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1b /input/hw1/input_hw1.txt /output/hw1/Q1b/
```

## Part c

**Goal**: Find the number of unique words with the same length and the same first character

- The question gives an example that wants non-case-sensitive
- So my program converts everything to lowercase as well

### Outline

- Mapper: Length + First character -> Word
- Reducer 1: Length + First character -> Unique word count

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1c /input/hw1/input_hw1.txt /output/hw1/Q1c/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1c /input/hw1/input_hw1.txt /output/hw1/Q1c/
```

# Question 2

## Part a

**Goal**: Find the average of AvgTemperature for each Region.

### Outline

- Mapper: Region -> Temperature
- Reducer: Region -> Average temperature

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2a /input/hw1/city_temperature.csv /output/hw1/Q2a/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2a /input/hw1/city_temperature.csv /output/hw1/Q2a/
```

## Part b

**Goal**: Find the average of AvgTemperature by Year for countries only located in the “Asia” Region

### Outline

- Mapper: Year -> Temperature
- Reducer: Year -> Average temperature

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2b /input/hw1/city_temperature.csv /output/hw1/Q2b/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2b /input/hw1/city_temperature.csv /output/hw1/Q2b/
```

## Part c

**Goal**: Find the average of AvgTemperature by City only located in the Country “Spain”

### Outline

- Mapper: City -> Temperature (**NOTE**: Filtered by country Spain)
- Reducer: City -> Average temperature

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /output/hw1/Q2c/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /output/hw1/Q2c/
```

## Part d

**Goal**: For each country, find the capital and average of AvgTemperature of that capital city

### Outline

For this part, I combine two input files and throw them into Mapper 1. So Mapper 1 is responsible for handling two types of data.

- Mapper: City -> Temperature **AND** Capital city -> Country
- Reducer: Capital city -> Average temperature

### Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /input/hw1/country-list.csv /output/hw1/Q2d/
```

### Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /input/hw1/country-list.csv /output/hw1/Q2d/
```

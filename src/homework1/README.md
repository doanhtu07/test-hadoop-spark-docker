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

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1a /input/hw1/input_hw1.txt /output/hw1/Q1a/
```

- Inside Docker

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

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1b /input/hw1/input_hw1.txt /output/hw1/Q1b/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1b /input/hw1/input_hw1.txt /output/hw1/Q1b/
```

## Part c

**Goal**: Find the number of unique words with the same length and the same first character

- The question gives an example that wants non-case-sensitive
- So my program converts everything to lowercase as well

- Mapper 1: Word -> 1
- Reducer 1: Word -> Count

- Mapper 2: length + "|" + first character -> 1
- Reducer 2: length + "|" + first character -> Count

In `part c`, I use 2 jobs chaining together so we need input path, intermediate output path, and final output path

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q1c /input/hw1/input_hw1.txt /intermediate/hw1/Q1c/ /output/hw1/Q1c/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q1c /input/hw1/input_hw1.txt /intermediate/hw1/Q1c/ /output/hw1/Q1c/
```

# Question 2

## Part a

**Goal**: Find the average of AvgTemperature for each Region.

- Mapper 1: Region -> Temperature
- Reducer 1: Region -> < Total temperature, number of occurences >

- Mapper 2: Region -> Average temperature
- Reducer 2: Identity reducer

In `part a` of Q2, we have one input path (city temperature), one intermediate output path, and one final output path

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2a /input/hw1/city_temperature.csv /intermediate/hw1/Q2a/ /output/hw1/Q2a/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2a /input/hw1/city_temperature.csv /intermediate/hw1/Q2a/ /output/hw1/Q2a/
```

## Part b

**Goal**: Find the average of AvgTemperature by Year for countries only located in the “Asia” Region

- Mapper 1: Year -> Temperature
- Reducer 1: Year -> < Total temperature, number of occurences >

- Mapper 2: Year -> Average temperature
- Reducer 2: Identity reducer

In `part b` of Q2, we have one input path (city temperature), one intermediate output path, and one final output path

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2b /input/hw1/city_temperature.csv /intermediate/hw1/Q2b/ /output/hw1/Q2b/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2b /input/hw1/city_temperature.csv /intermediate/hw1/Q2b/ /output/hw1/Q2b/
```

## Part c

**Goal**: Find the average of AvgTemperature by City only located in the Country “Spain”

- Mapper 1: City -> Temperature (**NOTE**: Filtered by country Spain)
- Reducer 1: City -> < Total temperature, number of occurences >

- Mapper 2: City -> Average temperature
- Reducer 2: Identity reducer

In `part c` of Q2, we have one input path (city temperature), one intermediate output path, and one final output path

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /intermediate/hw1/Q2c/ /output/hw1/Q2c/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /intermediate/hw1/Q2c/ /output/hw1/Q2c/
```

## Part d

**Goal**: For each country, find the capital and average of AvgTemperature of that capital city

For this part, I combine two input files and throw them into Mapper 1. So Mapper 1 is responsible for handling two types of data.

- Mapper 1: City -> Temperature **AND** Capital city -> Country
- Reducer 1: Capital city -> < Total temperature, number of occurences >

- Mapper 2: Capital city -> Average temperature
- Reducer 2: Identity reducer

In `part d` of Q2, we have two input paths (city temperature, then country list), one intermediate output path, and one final output path

- Standalone jar file

```
hadoop jar homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /input/hw1/country-list.csv /intermediate/hw1/Q2d/ /output/hw1/Q2d/
```

- Inside Docker

```
hadoop jar /src/homework1/target/homework1-1.0.0.jar com.homework1.Q2c /input/hw1/city_temperature.csv /input/hw1/country-list.csv /intermediate/hw1/Q2d/ /output/hw1/Q2d/
```

# Requirements

My Github repo: https://github.com/doanhtu07/test-hadoop-docker

- Hadoop version 3.4
- Java version 11
- Maven version 3.9.9 (Optional)

I use Maven version 3.9.9 to build my project by the way. You can use the jar file directly or attempt to build my Maven project again. It's your choice.

# Question 1

Mapper emits:

- Country, Capital -> avgTemp

=> Reducer simply calculates average temperature since the key is already country + capital

### Standalone jar file

```
hadoop jar homework2-1.0.0.jar com.homework2.Q1 /input/hw2/city_temperature.csv /input/hw2/country-list.csv /output/hw2/Q1/
```

### Inside Docker

```
hadoop jar /src/homework2/target/homework2-1.0.0.jar com.homework2.Q1 /input/hw2/city_temperature.csv /input/hw2/country-list.csv /output/hw2/Q1/
```

# Question 2

Mapper emits:

- Country, City -> Float.NaN (country-list)
- Country, City -> Average temperature (city_temperature)

=> Reducer simply calculates average temperature since the key is already country + city

### Standalone jar file

```
hadoop jar homework2-1.0.0.jar com.homework2.Q2 /input/hw2/city_temperature.csv /input/hw2/country-list.csv /output/hw2/Q2/
```

### Inside Docker

```
hadoop jar /src/homework2/target/homework2-1.0.0.jar com.homework2.Q2 /input/hw2/city_temperature.csv /input/hw2/country-list.csv /output/hw2/Q2/
```

# Question 3

Mapper emits:

- Country -> AvgTemp

Combiner takes local max of all AvgTemp in the mapper output

Reducer takes global max of all AvgTemp in the country

### Standalone jar file

```
hadoop jar homework2-1.0.0.jar com.homework2.Q3 /input/hw2/city_temperature.csv /output/hw2/Q3/
```

### Inside Docker

```
hadoop jar /src/homework2/target/homework2-1.0.0.jar com.homework2.Q3 /input/hw2/city_temperature.csv /output/hw2/Q3/
```

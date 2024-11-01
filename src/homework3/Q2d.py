from pyspark.sql import SparkSession
import os
import shutil

CITY_TEMPERATURE_PATH = "/src/io/hw3/city_temperature.csv"
COUNTRY_LIST_PATH = "/src/io/hw3/country-list.csv"
OUTPUT_DIR_PATH = "/src/io/hw3/Q2d-output"
OUTPUT_PATH = "/src/io/hw3/Q2d-output.txt"

spark = SparkSession.builder \
                    .appName("Q2d") \
                    .getOrCreate()

# Access SparkContext through SparkSession
sc = spark.sparkContext
sc.setLogLevel("ERROR")


def clearPrint(*args):
    print("\n", *args, "\n")


clearPrint("=== === === My Program === === ===")

# === Input ===

rdd = sc.textFile(CITY_TEMPERATURE_PATH)
countryRdd = sc.textFile(COUNTRY_LIST_PATH)
clearPrint("INPUT", rdd)

# === For each country, find the capital and average of AvgTemperature of that capital city ===

# Split each line by comma (adjust the delimiter if necessary)
rdd_split = rdd.map(lambda line: line.split(','))
countryRdd_split = countryRdd.map(lambda line: line.split(','))

header = rdd_split.first()  # Get the first line (header)
rdd_no_header = rdd_split.filter(lambda row: row != header)

header = countryRdd_split.first()  # Get the first line (header)
countryRdd_no_header = countryRdd_split.filter(lambda row: row != header)

countryList = countryRdd_no_header \
    .map(lambda line: (line[0][1:len(line[0])-1], line[1][1:len(line[1])-1]))
countryList = dict(countryList.collect())  # country, capital
clearPrint("COUNTRY LIST", countryList)

avgTemp = rdd_no_header \
    .filter(lambda line: line[1] in countryList and countryList[line[1]] == line[3]) \
    .map(lambda line: ((line[1], line[3]), (float(line[7]), 1))) \
    .reduceByKey(lambda x, y: (x[0] + y[0], x[1] + y[1])) \
    .map(lambda x: (x[0], x[1][0] / x[1][1])) \
    .sortBy(lambda x: x[0])

# === Output ===

if os.path.exists(OUTPUT_DIR_PATH):
    # If it exists, remove the directory
    shutil.rmtree(OUTPUT_DIR_PATH)

if os.path.exists(OUTPUT_PATH):
    os.remove(OUTPUT_PATH)

# Coalesce into a single partition (one file)
avgTemp.coalesce(1).saveAsTextFile(OUTPUT_DIR_PATH)

shutil.move(os.path.join(OUTPUT_DIR_PATH, "part-00000"), OUTPUT_PATH)
shutil.rmtree(OUTPUT_DIR_PATH)

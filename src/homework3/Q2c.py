from pyspark.sql import SparkSession
import os
import shutil

CITY_TEMPERATURE_PATH = "/src/io/hw3/city_temperature.csv"
OUTPUT_DIR_PATH = "/src/io/hw3/Q2c-output"
OUTPUT_PATH = "/src/io/hw3/Q2c-output.txt"

spark = SparkSession.builder \
                    .appName("Q2c") \
                    .getOrCreate()

# Access SparkContext through SparkSession
sc = spark.sparkContext
sc.setLogLevel("ERROR")


def clearPrint(*args):
    print("\n", *args, "\n")


clearPrint("=== === === My Program === === ===")

# === Input ===

rdd = sc.textFile(CITY_TEMPERATURE_PATH)
clearPrint("INPUT", rdd)

# === Find the average of AvgTemperature by City only located in the Country “Senegal” ===

# Split each line by comma (adjust the delimiter if necessary)
rdd_split = rdd.map(lambda line: line.split(','))

# Optionally, remove the header
header = rdd_split.first()  # Get the first line (header)
rdd_no_header = rdd_split.filter(lambda row: row != header)

avgTemp = rdd_no_header \
    .filter(lambda line: line[1] == "Senegal") \
    .map(lambda line: (line[3], (float(line[7]), 1))) \
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

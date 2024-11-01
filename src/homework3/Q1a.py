from pyspark.sql import SparkSession
import os
import shutil

INPUT_PATH = "/src/io/hw3/input_hw1.txt"
OUTPUT_DIR_PATH = "/src/io/hw3/Q1a-output"
OUTPUT_PATH = "/src/io/hw3/Q1a-output.txt"

spark = SparkSession.builder \
                    .appName("Q1a") \
                    .getOrCreate()

# Access SparkContext through SparkSession
sc = spark.sparkContext
sc.setLogLevel("ERROR")


def clearPrint(*args):
    print("\n", *args, "\n")


clearPrint("=== === === My Program === === ===")

# === Input ===
rdd = sc.textFile(INPUT_PATH)
clearPrint("INPUT", rdd)

# === Count the number of occurrences for each word (word count) ===

counts = rdd \
    .flatMap(lambda line: line.split()) \
    .map(lambda word: (word, 1)) \
    .reduceByKey(lambda x, y: x + y) \
    .sortBy(lambda x: x[0])

counts.collect()

# === Output ===

# Remove an empty directory

if os.path.exists(OUTPUT_DIR_PATH):
    # If it exists, remove the directory
    shutil.rmtree(OUTPUT_DIR_PATH)

if os.path.exists(OUTPUT_PATH):
    os.remove(OUTPUT_PATH)

# Coalesce into a single partition (one file)
counts.coalesce(1).saveAsTextFile(OUTPUT_DIR_PATH)

shutil.move(os.path.join(OUTPUT_DIR_PATH, "part-00000"), OUTPUT_PATH)
shutil.rmtree(OUTPUT_DIR_PATH)

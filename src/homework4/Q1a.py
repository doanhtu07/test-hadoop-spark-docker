import os
import shutil

from pyspark.sql import SparkSession

INPUT_PATH = "/src/io/hw4/city_temperature.csv"
OUTPUT_DIR_PATH = "/src/io/hw4/Q1a-output"
OUTPUT_PATH = "/src/io/hw4/Q1a-output.csv"

spark = SparkSession.builder.appName("Q1a").getOrCreate()

# Access SparkContext through SparkSession
sc = spark.sparkContext
sc.setLogLevel("ERROR")


def clearPrint(*args):
    print("\n", *args, "\n")


clearPrint("=== === === My Program === === ===")

# === Input ===

df = spark.read.csv(INPUT_PATH, header=True, inferSchema=True)

# === Find the average of AvgTemperature for each Region ===

df = (
    df.filter(df["AvgTemperature"] != -99)
    .groupBy("Region")
    .avg("AvgTemperature")
    .orderBy("Region")
)

# === Output ===

# Remove an empty directory

if os.path.exists(OUTPUT_DIR_PATH):
    # If it exists, remove the directory
    shutil.rmtree(OUTPUT_DIR_PATH)

if os.path.exists(OUTPUT_PATH):
    os.remove(OUTPUT_PATH)

# Coalesce into a single partition (one file)
# counts.coalesce(1).saveAsTextFile(OUTPUT_DIR_PATH)

df.coalesce(1).write.csv(OUTPUT_DIR_PATH, header=True)

for filename in os.listdir(OUTPUT_DIR_PATH):
    if filename.startswith("part-") and filename.endswith(".csv"):
        part_file_path = os.path.join(OUTPUT_DIR_PATH, filename)
        final_file_path = os.path.join(OUTPUT_DIR_PATH, OUTPUT_PATH)

        # Rename the file to the desired name
        shutil.move(part_file_path, final_file_path)
        shutil.rmtree(OUTPUT_DIR_PATH)
        break

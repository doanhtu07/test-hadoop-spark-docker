import os
import shutil

from pyspark.sql import SparkSession
from pyspark.sql.functions import broadcast

CITY_TEMPERATURE_PATH = "/src/io/hw3/city_temperature.csv"
COUNTRY_LIST_PATH = "/src/io/hw3/country-list.csv"
OUTPUT_DIR_PATH = "/src/io/hw4/Q1e-output"
OUTPUT_PATH = "/src/io/hw4/Q1e-output.csv"

spark = SparkSession.builder.appName("Q1e").getOrCreate()

# Access SparkContext through SparkSession
sc = spark.sparkContext
sc.setLogLevel("ERROR")


def clearPrint(*args):
    print("\n", *args, "\n")


clearPrint("=== === === My Program === === ===")

# === Input ===

countryDf = spark.read.csv(COUNTRY_LIST_PATH, header=True, inferSchema=True)
tempDf = spark.read.csv(CITY_TEMPERATURE_PATH, header=True, inferSchema=True).alias(
    "tempDf"
)

broadcast_countryDf = broadcast(countryDf).alias("countryDf")

# === For each country, find the capital and average of AvgTemperature of that capital city. (Use broadcast variable) ===

df = tempDf.join(
    broadcast_countryDf,
    on=(
        (tempDf["Country"] == broadcast_countryDf["country"])
        & (tempDf["City"] == broadcast_countryDf["capital"])
    ),
    how="inner",
)

df.show()

df = (
    df.filter(df["AvgTemperature"] != -99)
    .groupBy(["tempDf.Country", "tempDf.City"])
    .avg("AvgTemperature")
    .orderBy("tempDf.Country")
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

import os
import shutil

from pyspark.sql import SparkSession
from pyspark.sql import functions as F
from pyspark.sql.functions import broadcast
from pyspark.sql.types import BooleanType, StringType

CITY_TEMPERATURE_PATH = "/src/io/hw3/city_temperature.csv"
COUNTRY_LIST_PATH = "/src/io/hw3/country-list.csv"
OUTPUT_DIR_PATH = "/src/io/hw4/Q1f-output"
OUTPUT_PATH = "/src/io/hw4/Q1f-output.csv"

spark = SparkSession.builder.appName("Q1f").getOrCreate()

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

# === For each country, find the capital and average of AvgTemperature of that capital city. ===
# === (Use UDF to filter year >= 2010 and change final output format) ===

df = tempDf.join(
    broadcast_countryDf,
    on=(
        (tempDf["Country"] == broadcast_countryDf["country"])
        & (tempDf["City"] == broadcast_countryDf["capital"])
    ),
    how="inner",
)

df.show()


# Define the UDF to check if the year is equal to the specified year
def is_lte_year(year, targetYear):
    return year >= targetYear


# Register the UDF
year_filter_udf = F.udf(lambda year: is_lte_year(year, 2010), BooleanType())


# Define the UDF to reformat the output
def reformat_output(capital: str, country: str, temperature: float):
    return (
        capital
        + " is the capital of "
        + country
        + " and its average temperature is "
        + str(temperature)
    )


# Register the UDF
reformat_output_udf = F.udf(reformat_output, StringType())


df = (
    df.filter((df["AvgTemperature"] != -99) & year_filter_udf(df["Year"]))
    .groupBy(["tempDf.Country", "tempDf.City"])
    .avg("AvgTemperature")
    .orderBy("tempDf.Country")
    .withColumn(
        "my-column",
        reformat_output_udf("tempDf.City", "tempDf.Country", "avg(AvgTemperature)"),
    )
    .select("my-column")
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

df.coalesce(1).write.csv(OUTPUT_DIR_PATH, header=False)

for filename in os.listdir(OUTPUT_DIR_PATH):
    if filename.startswith("part-") and filename.endswith(".csv"):
        part_file_path = os.path.join(OUTPUT_DIR_PATH, filename)
        final_file_path = os.path.join(OUTPUT_DIR_PATH, OUTPUT_PATH)

        # Rename the file to the desired name
        shutil.move(part_file_path, final_file_path)
        shutil.rmtree(OUTPUT_DIR_PATH)
        break

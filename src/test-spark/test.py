import pyspark
from pyspark.sql import SparkSession

# Examples link: https://spark.apache.org/examples.html

print(pyspark.__version__)

spark = SparkSession.builder.appName("demo").getOrCreate()

df = spark.createDataFrame(
    [
        ("sue", 32),
        ("li", 3),
        ("bob", 75),
        ("heo", 13),
    ],
    ["first_name", "age"],
)

df.show()

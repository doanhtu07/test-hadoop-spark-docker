package com.homework2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
// import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q1 {

  public static class CountMap extends Mapper<LongWritable, Text, Text, FloatWritable> {
    public static final Log log = LogFactory.getLog(CountMap.class);
    private static int mapCounter = 0;

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    private static HashMap<String, String> countryList = new HashMap<>(); // country -> capital

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
      mapCounter++;

      // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
      log.info("DEBUG CountMap setup --- " + mapCounter);

      // --- Way 1: Read the file from the Hadoop file system (HDFS)

      // Get configuration and the file path from context
      // Configuration conf = context.getConfiguration();

      // Path path = new Path("hdfs://hadoop:9000/" + conf.get("country_list_path"));
      // FileSystem fs = FileSystem.get(conf);
      // BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));

      // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
      // log.info("DEBUG CountMap setup --- " + fs.exists(path));

      // --- Way 2: Read the file from the distributed cache
      URI[] cacheFiles = context.getCacheFiles();
      Path path = new Path(cacheFiles[0]);
      BufferedReader br = new BufferedReader(new FileReader(path.getName()));

      try {
        String line;
        while ((line = br.readLine()) != null) {
          String[] parts = line.split(",");

          // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
          log.info("DEBUG CountMap setup --- " + parts[0] + " " + parts[1]);

          String country = parts[0].substring(1, parts[0].length() - 1);
          String capital = parts[1].substring(1, parts[1].length() - 1);

          // Ignore header line in csv
          if (country.compareTo("\"country\"") == 0) {
            continue;
          }

          if (parts.length >= 2) {
            countryList.put(country, capital);
          }
        }

      } finally {
        br.close();
      }

      // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
      log.info("DEBUG CountMap setup --- " + countryList.toString());
    }

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      String[] lineData = value.toString().split(",");

      // Ignore header line in csv
      if (lineData[0].compareTo("Region") == 0) {
        return;
      }

      String country = lineData[1];
      String city = lineData[3];
      Float avgTemp = Float.parseFloat(lineData[7]);
      String matchCity = countryList.get(country);

      // Filter not capital
      if (matchCity == null || matchCity.compareTo(city) != 0) {
        return;
      }

      outputKey.set(country + "," + city); // country,capital
      outputValue.set(avgTemp); // avgTemp
      context.write(outputKey, outputValue);
    }
  }

  public static class CountReduce extends Reducer<Text, FloatWritable, Text, FloatWritable> {
    public static final Log log = LogFactory.getLog(CountReduce.class);

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterable<FloatWritable> values, Context context)
        throws IOException, InterruptedException {

      Float sumAvgTemp = Float.valueOf(0); // initialize the sum for each keyword
      int occurrences = 0;

      for (FloatWritable val : values) {
        sumAvgTemp += val.get();
        occurrences += 1;
      }

      outputKey.set(key.toString());
      outputValue.set(sumAvgTemp / occurrences);
      context.write(outputKey, outputValue);
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 3) {
      System.err.println("Usage: Q1 <city_temperature> <country_list> <out>");
      System.exit(2);
    }

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", ",");
    conf.set("country_list_path", otherArgs[1]);

    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q1 - average temperature for capital city (mapper side join)");

    // -- Way 2: Read the file from the distributed cache
    job1.addCacheFile(new Path(otherArgs[1]).toUri());

    job1.setJarByClass(Q1.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    job1.setMapOutputKeyClass(Text.class);
    job1.setMapOutputValueClass(FloatWritable.class);

    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(FloatWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job1, new Path(otherArgs[2]));

    // Wait till job completion
    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }
  }
}

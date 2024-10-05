package com.homework1;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
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

public class Q2d {
  public static final Log log = LogFactory.getLog(CountMap.class);

  public static class CountMap extends Mapper<LongWritable, Text, Text, Text> {
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    // private static HashMap<String, String> countryList = new HashMap<>();
    // private static int counter = 0;

    // @Override
    // public void setup(Context context) throws IOException, InterruptedException {
    // // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
    // counter++;
    // log.info("DEBUG CountMap setup --- " + counter);

    // // Get configuration and the file path from context
    // Configuration conf = context.getConfiguration();

    // // Read the file from the Hadoop file system (HDFS)
    // Path path = new Path("hdfs://hadoop:9000/input/hw1/country-list.csv");
    // FileSystem fs = FileSystem.get(conf);
    // BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));

    // // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
    // log.info("DEBUG CountMap setup --- " + fs.exists(path));

    // try {
    // String line;
    // while ((line = br.readLine()) != null) {
    // String[] parts = line.split(",");

    // // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
    // log.info("DEBUG CountMap setup --- " + parts[0] + " " + parts[1]);

    // if (parts.length >= 2) {
    // countryList.put(parts[0], parts[1]);
    // }
    // }

    // } finally {
    // br.close();
    // }

    // // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
    // log.info("DEBUG CountMap setup --- " + countryList.toString());
    // }

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      String[] lineData = value.toString().split(",");

      // Ignore header line in csv
      if (lineData[0].compareTo("Region") == 0) {
        return;
      }

      // Ignore header line in csv
      if (lineData[0].compareTo("\"country\"") == 0) {
        return;
      }

      if (lineData.length == 8) {
        String city = lineData[3];
        Float avgTemp = Float.parseFloat(lineData[7]);

        // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
        // System.out.println("DEBUG CountMap --- " + city + " " + avgTemp);

        outputKey.set(city.toString());
        outputValue.set(avgTemp.toString());

        context.write(outputKey, outputValue);

        return;
      }

      if (lineData.length == 3) {
        String country = lineData[0];
        String city = lineData[1];

        // DEBUG: View at localhost:9870 in userlogs - syslog of the mapper
        // System.out.println("DEBUG CountMap --- " + country + " " + city);

        outputKey.set(city.substring(1, city.length() - 1));
        outputValue.set(country.substring(1, country.length() - 1));

        context.write(outputKey, outputValue);

        return;
      }
    }
  }

  public static class CountReduce extends Reducer<Text, Text, Text, FloatWritable> {

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {

      Float sumAvgTemp = Float.valueOf(0); // initialize the sum for each keyword
      int occurrences = 0;
      String country = null;

      for (Text val : values) {
        try {
          Float temp = Float.parseFloat(val.toString());
          sumAvgTemp += temp;
          occurrences += 1;
        } catch (NumberFormatException e) {
          country = val.toString();
        }
      }

      if (country != null && occurrences > 0) {
        outputKey.set(country + "," + key.toString());
        outputValue.set(sumAvgTemp / occurrences);
        context.write(outputKey, outputValue);
      }
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 3) {
      System.err.println("Usage: Q2d <city_temperature> <country_list> <out>");
      System.exit(2);
    }

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", ",");

    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q2d - average temperature for capital city");

    job1.setJarByClass(Q2d.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    job1.setMapOutputKeyClass(Text.class);
    job1.setMapOutputValueClass(Text.class);

    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(FloatWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
    FileInputFormat.addInputPath(job1, new Path(otherArgs[1]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job1, new Path(otherArgs[2]));

    // Wait till job completion
    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }
  }
}

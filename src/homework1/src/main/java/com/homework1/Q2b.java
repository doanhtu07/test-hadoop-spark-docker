package com.homework1;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q2b {

  public static class CountMap extends Mapper<LongWritable, Text, Text, FloatWritable> {

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      String[] lineData = value.toString().split(",");

      // DEBUG: View at localhost:9870 in userlogs
      System.out.println("DEBUG CountMap --- " + lineData);

      // Ignore header line in csv
      if (lineData[0].compareTo("Region") == 0) {
        return;
      }

      String region = lineData[0];
      String year = lineData[6];
      Float avgTemp = Float.parseFloat(lineData[7]);

      // Filter by Asia region
      if (region.compareTo("Asia") != 0) {
        return;
      }

      // DEBUG: View at localhost:9870 in userlogs
      System.out.println("DEBUG CountMap --- " + region + " " + year + " " + avgTemp);

      outputKey.set(year.toString());
      outputValue.set(avgTemp.floatValue());
      context.write(outputKey, outputValue);
    }
  }

  public static class CountReduce extends Reducer<Text, FloatWritable, Text, Text> {

    private Text result = new Text();

    public void reduce(Text key, Iterable<FloatWritable> values, Context context)
        throws IOException, InterruptedException {

      Float sumAvgTemp = Float.valueOf(0); // initialize the sum for each keyword
      int occurrences = 0;

      for (FloatWritable val : values) {
        sumAvgTemp += val.get();
        occurrences += 1;
      }

      result.set(sumAvgTemp.toString() + "/" + Integer.toString(occurrences));
      context.write(key, result);
    }
  }

  public static class AverageMap extends Mapper<LongWritable, Text, Text, FloatWritable> {

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      String[] lineData = value.toString().split(",");

      // DEBUG: View at localhost:9870 in userlogs
      for (String data : lineData) {
        System.out.println("DEBUG lineData ---|" + data + "|---");
      }

      String year = lineData[0];

      String[] tokens = lineData[1].split("/");
      Float avgTemp = Float.parseFloat(tokens[0]);
      int occurrences = Integer.parseInt(tokens[1]);

      outputKey.set(year);
      outputValue.set(avgTemp / occurrences);

      context.write(outputKey, outputValue);
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 3) {
      System.err.println("Usage: Q2b <in> <intermediate> <out>");
      System.exit(2);
    }

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", ",");

    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q2b - phase 1 - sum up temperature and occurrences");
    job1.setJarByClass(Q2b.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    job1.setMapOutputKeyClass(Text.class);
    job1.setMapOutputValueClass(FloatWritable.class);

    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(Text.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));

    // Wait till job completion
    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }

    // === Second job ===

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", "\t");

    @SuppressWarnings("deprecation")
    Job job2 = new Job(conf, "Q2b - phase 2 - make average temperature");
    job2.setJarByClass(Q2b.class);
    job2.setMapperClass(AverageMap.class);
    // Use default identity reducer => No declaration

    job2.setMapOutputKeyClass(Text.class);
    job2.setMapOutputValueClass(FloatWritable.class);

    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(FloatWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job2, new Path(otherArgs[1]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job2, new Path(otherArgs[2]));

    // Wait till job completion
    if (!job2.waitForCompletion(true)) {
      System.exit(1);
    }
  }
}

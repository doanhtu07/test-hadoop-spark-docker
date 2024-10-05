package com.homework1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q2a {

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
      Float avgTemp = Float.parseFloat(lineData[7]);

      // DEBUG: View at localhost:9870 in userlogs
      System.out.println("DEBUG CountMap --- " + region + " " + avgTemp);

      outputKey.set(region);
      outputValue.set(avgTemp.floatValue());
      context.write(outputKey, outputValue);
    }
  }

  public static class CountReduce extends Reducer<Text, FloatWritable, Text, FloatWritable> {

    private FloatWritable result = new FloatWritable();

    public void reduce(Text key, Iterable<FloatWritable> values, Context context)
        throws IOException, InterruptedException {

      Float sumAvgTemp = Float.valueOf(0); // initialize the sum for each keyword
      int occurrences = 0;

      for (FloatWritable val : values) {
        sumAvgTemp += val.get();
        occurrences += 1;
      }

      result.set(sumAvgTemp / occurrences);
      context.write(key, result);
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 2) {
      System.err.println("Usage: Q2a <in> <out>");
      System.exit(2);
    }

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", ",");

    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q2a - average temperature");
    job1.setJarByClass(Q2a.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    job1.setMapOutputKeyClass(Text.class);
    job1.setMapOutputValueClass(FloatWritable.class);

    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(FloatWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));

    // Wait till job completion
    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }
  }
}

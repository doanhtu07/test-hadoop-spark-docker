package com.homework2;

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

public class Q3 {

  public static class CountMap extends Mapper<LongWritable, Text, Text, FloatWritable> {
    public static final Log log = LogFactory.getLog(CountMap.class);

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      String[] lineData = value.toString().split(",");

      // Ignore header line in csv
      if (lineData[0].compareTo("Region") == 0) {
        return;
      }

      String country = lineData[1];
      Float avgTemp = Float.parseFloat(lineData[7]);

      outputKey.set(country); // country,capital
      outputValue.set(avgTemp); // avgTemp
      context.write(outputKey, outputValue);
    }
  }

  public static class CountCombiner extends Reducer<Text, FloatWritable, Text, FloatWritable> {
    public static final Log log = LogFactory.getLog(CountReduce.class);

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterable<FloatWritable> values, Context context)
        throws IOException, InterruptedException {

      Float maxAvgTemp = Float.MIN_VALUE;

      for (FloatWritable val : values) {
        if (val.get() > maxAvgTemp) {
          maxAvgTemp = val.get();
        }
      }

      outputKey.set(key.toString());
      outputValue.set(maxAvgTemp);
      context.write(outputKey, outputValue);
    }
  }

  public static class CountReduce extends Reducer<Text, FloatWritable, Text, FloatWritable> {
    public static final Log log = LogFactory.getLog(CountReduce.class);

    private Text outputKey = new Text();
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterable<FloatWritable> values, Context context)
        throws IOException, InterruptedException {

      Float maxAvgTemp = Float.MIN_VALUE;

      for (FloatWritable val : values) {
        if (val.get() > maxAvgTemp) {
          maxAvgTemp = val.get();
        }
      }

      outputKey.set(key.toString());
      outputValue.set(maxAvgTemp);
      context.write(outputKey, outputValue);
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 2) {
      System.err.println("Usage: Q3 <city_temperature> <out>");
      System.exit(2);
    }

    // Set the custom output delimiter
    conf.set("mapreduce.output.textoutputformat.separator", ",");

    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q3 - highest average temperature in the country (with Combiner)");

    job1.setJarByClass(Q3.class);
    job1.setMapperClass(CountMap.class);
    job1.setCombinerClass(CountCombiner.class);
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

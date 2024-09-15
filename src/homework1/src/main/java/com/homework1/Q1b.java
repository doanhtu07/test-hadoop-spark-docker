package com.homework1;

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileReader;
import java.io.IOException;
// import java.net.URI;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
// import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
// import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
// import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q1b {

  public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable one = new IntWritable(1);
    private Text outputKey = new Text(); // type of output key

    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
      // Flow:
      //
      // - RecordReader splits input into multiple <key, value> pairs
      // - TextInputFormat typically splits files line by line
      // - key is the line's offset and the value is the actual line of text
      //
      // - Each mapper processes one <key, value> pair

      String[] mydata = value.toString().split("\\s+");

      for (String data : mydata) {
        String lower = data.toLowerCase();

        if (
        //
        lower.compareTo("america") == 0
            || lower.compareTo("president") == 0
            || lower.compareTo("washington") == 0
        //
        ) {
          outputKey.set(lower); // set word as each input keyword
          context.write(outputKey, one); // create a pair <keyword, 1>
        }
      }
    }
  }

  public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {

      // - Each reducer processes one <key, value> pair returned from mappers

      int sum = 0; // initialize the sum for each keyword
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result); // create a pair <keyword, number of occurences>
    }
  }

  // Driver program
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // get all args
    if (otherArgs.length != 2) {
      System.err.println("Usage: Q1b <in> <out>");
      System.exit(2);
    }

    // create a job with name "wordcount"
    @SuppressWarnings("deprecation")
    Job job = new Job(conf, "Q1b - specific wordcount");
    job.setJarByClass(Q1b.class);
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);

    // uncomment the following line to add the Combiner
    // job.setCombinerClass(Reduce.class);

    // set output key type
    job.setOutputKeyClass(Text.class);
    // set output value type
    job.setOutputValueClass(IntWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    // Wait till job completion
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

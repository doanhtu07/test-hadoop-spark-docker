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
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
// import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
// import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
// import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Q1c {

  public static class CountMap extends Mapper<LongWritable, Text, Text, IntWritable> {

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
        outputKey.set(data.toLowerCase()); // set lowercase word as each input keyword
        context.write(outputKey, one); // create a pair <keyword, 1>
      }
    }
  }

  public static class UniqueMap extends Mapper<LongWritable, Text, Text, IntWritable> {

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
      String uniqueString = mydata[0];

      // length + " " + first character
      String mapKey = uniqueString.length() == 0 ? "0|" : uniqueString.length() + "|" + uniqueString.charAt(0);

      outputKey.set(mapKey);
      context.write(outputKey, one);
    }
  }

  public static class CountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {

      // Flow:
      // - Each reducer processes one <key, value> pair returned from mappers

      // Mapper: multiple pairs (key -> 1)

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
    if (otherArgs.length != 3) {
      System.err.println("Usage: Q1c <in> <tmp> <out>");
      System.exit(2);
    }

    // create a job with name "wordcount"
    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q1c - unique wordcount phase 1: count words");
    job1.setJarByClass(Q1c.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    // set output key type
    job1.setOutputKeyClass(Text.class);
    // set output value type
    job1.setOutputValueClass(IntWritable.class);

    // set the HDFS path of the input data
    FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
    // set the HDFS path for the output
    FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));

    // Wait till job completion
    if (!job1.waitForCompletion(true)) {
      System.exit(1);
    }

    // === Second job ===

    // create a job with name "wordcount"
    @SuppressWarnings("deprecation")
    Job job2 = new Job(conf, "Q1c - unique wordcount phase 2: count unique criteria");
    job2.setJarByClass(Q1c.class);
    job2.setMapperClass(UniqueMap.class);
    job2.setReducerClass(CountReduce.class);

    // set output key type
    job2.setOutputKeyClass(Text.class);
    // set output value type
    job2.setOutputValueClass(IntWritable.class);

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

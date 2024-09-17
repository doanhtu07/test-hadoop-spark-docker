package com.homework1;

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

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

  public static class CountMap extends Mapper<LongWritable, Text, Text, Text> {

    private Text outputKey = new Text(); // type of output key
    private Text outputValue = new Text();

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
        String mapKey = data.length() == 0 ? "0|" : data.length() + "|" + String.valueOf(data.charAt(0)).toLowerCase();
        outputKey.set(mapKey);
        outputValue.set(data);
        context.write(outputKey, outputValue);
      }
    }
  }

  public static class CountReduce extends Reducer<Text, Text, Text, IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {

      // Flow:
      // - Each reducer processes one <key, value> pair returned from mappers

      // Mapper: multiple pairs (key -> 1)

      HashSet<String> set = new HashSet<>();

      int sum = 0; // initialize the sum for each keyword
      for (Text val : values) {
        if (set.contains(val.toString())) {
          continue;
        }
        sum += 1;
        set.add(val.toString());
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
      System.err.println("Usage: Q1c <in> <out>");
      System.exit(2);
    }

    // create a job with name "wordcount"
    @SuppressWarnings("deprecation")
    Job job1 = new Job(conf, "Q1c - unique wordcount");
    job1.setJarByClass(Q1c.class);
    job1.setMapperClass(CountMap.class);
    job1.setReducerClass(CountReduce.class);

    job1.setMapOutputKeyClass(Text.class);
    job1.setMapOutputValueClass(Text.class);

    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(IntWritable.class);

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

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

public class Q2 {

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

            // Ignore header line in csv
            if (lineData[0].compareTo("\"country\"") == 0) {
                return;
            }

            if (lineData.length == 3) {
                String country = lineData[0].substring(1, lineData[0].length() - 1);
                String city = lineData[1].substring(1, lineData[1].length() - 1);

                outputKey.set(country + "," + city);
                outputValue.set(Float.NaN);

                context.write(outputKey, outputValue);

                return;
            }

            if (lineData.length == 8) {
                String country = lineData[1];
                String city = lineData[3];
                Float avgTemp = Float.parseFloat(lineData[7]);

                outputKey.set(country + "," + city);
                outputValue.set(avgTemp);

                context.write(outputKey, outputValue);

                return;
            }
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
            boolean isCapital = false;

            for (FloatWritable val : values) {
                if (Float.isNaN(val.get())) {
                    isCapital = true;
                    continue;
                }
                sumAvgTemp += val.get();
                occurrences += 1;
            }

            if (!isCapital || occurrences == 0) {
                return;
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
            System.err.println("Usage: Q2 <city_temperature> <country_list> <out>");
            System.exit(2);
        }

        // Set the custom output delimiter
        conf.set("mapreduce.output.textoutputformat.separator", ",");

        @SuppressWarnings("deprecation")
        Job job1 = new Job(conf, "Q2 - average temperature for capital city (reducer side join)");

        job1.setJarByClass(Q2.class);
        job1.setMapperClass(CountMap.class);
        job1.setReducerClass(CountReduce.class);

        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(FloatWritable.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(FloatWritable.class);

        // set the HDFS path of the input data
        FileInputFormat.addInputPath(job1, new Path(otherArgs[1])); // Let country-list comes first in key order
        FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
        // set the HDFS path for the output
        FileOutputFormat.setOutputPath(job1, new Path(otherArgs[2]));

        // Wait till job completion
        if (!job1.waitForCompletion(true)) {
            System.exit(1);
        }
    }
}

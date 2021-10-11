/**
 * author: Dr. Rim Moussa
 * Goal:
 * illustrative example of MapReduce #1 
 */

package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;

public class MaxTemp extends Configured implements Tool {
 
  public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final int MISSING = 9999;
    public void map(LongWritable key, Text value,
                    Context context) throws IOException,InterruptedException {
      
      //print key ;) and tell what is it
      String line = value.toString();
      String year = line.substring(15, 19);
      int airTemperature=0;
      if (line.length()>92 && line.charAt(87) == '+') { // parseInt doesn't like leading plus signs
        airTemperature = Integer.parseInt(line.substring(88, 92));
      } else {
        if (line.length()>92)airTemperature = Integer.parseInt(line.substring(87, 92));
      }
      if (line.length()>93){
          String quality = line.substring(92, 93);
          if (airTemperature != MISSING && quality.matches("[01459]")) {
            context.write(new Text(year), new IntWritable(airTemperature));
          }
      }
    }
  }
  /**
   * reducer class 
   */
  public static class Reduce1 extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context) throws IOException,InterruptedException {
      int maxValue = Integer.MIN_VALUE;
      Iterator<IntWritable> it = values.iterator(); 
      while (it.hasNext()) {
        maxValue = Math.max(maxValue, it.next().get()); 
      }
      context.write(key, new IntWritable(maxValue));
    }
  }

  public int run(String[] args) throws Exception {
    // Make sure there are exactly 2 parameters
    if (args.length!= 2) {
      System.out.println("Wrong number of parameters");
      return -1;
    }

    Configuration conf = new Configuration();

    Job job = new Job(conf);
    job.setJarByClass(MaxTemp.class);

    job.setJobName("Max Temperature");

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    job.setMapperClass(Map1.class);
    //job.setCombinerClass(Combiner1.class);//local max
    job.setReducerClass(Reduce1.class);

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    //FileInputFormat.setMaxInputSplitSize(job, Long.parseLong(args[3])); 
    //16777216 = 16MB
    //33554432 = 32MB
    //FileInputFormat.setMinInputSplitSize(job, 1);

    boolean result = job.waitForCompletion(true);
   
    FileSystem fs = FileSystem.get(conf);   
    return 0;
  }
 
 
  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new MaxTemp(), args);
    System.exit(res);
  }

}
// to do
// 1. add a combiner
// 2. change the number of reducers

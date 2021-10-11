/**
 * author: Dr. Rim Moussa
 * Goal:
 * illustrative example of MapReduce #3
 */

package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

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
import java.util.StringTokenizer;

public class TermFreq extends Configured implements Tool {
 
  public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable> {
   
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value,
                    Context context) throws IOException,InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }

  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class Reduce1 extends Reducer<Text, IntWritable, Text, IntWritable> {
   
    //private static int support_threshold;
    //private IntWritable total = new IntWritable();
      
    public void setup(Context context) {
      // support_threshold = Integer.parseInt(context.getConfiguration().get("min_support_value")); 
	    //System.out.println("st: " + support_threshold);
    }

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context) throws IOException,InterruptedException {

      int sum = 0;
      for (IntWritable value : values) {
        sum += value.get();
      }
      context.write(key, new IntWritable(sum));
    }
    	
  }

  public int run(String[] args) throws Exception {
    // Make sure there are exactly 2 parameters
    if (args.length!= 2) {
      System.out.println("Wrong number of parameters");
      return -1;
    }

    Configuration conf = new Configuration();
    //global variables
    //conf.set("min_support_value", args[0]);

    Job job = new Job(conf);
    job.setJarByClass(TermFreq.class);

    job.setJobName("Term Frequency");

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    job.setMapperClass(Map1.class);
    job.setCombinerClass(Reduce1.class);
    job.setReducerClass(Reduce1.class);

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    //FileInputFormat.setMaxInputSplitSize(job, Long.parseLong(args[3])); 
    //16777216 = 16MB
    //33554432 = 32MB
    //FileInputFormat.setMinInputSplitSize(job, 1);

    boolean result = job.waitForCompletion(true);
  
    return 0;
  }
 
 
  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new TermFreq(), args);
    System.exit(res);
  }

}s

// to do
// reduce: select only frequent terms, those which frequency is greater than v


/**
 * author: Dr. Rim Moussa
 * Goal:
 * illustrative example of MapReduce #2
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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.util.StringTokenizer;

public class InvIdx extends Configured implements Tool {
 
  public static class Map1 extends Mapper<LongWritable, Text, Text, Text> {
   
    public void map(LongWritable key, Text value,
                    Context context) throws IOException,InterruptedException {
      	StringTokenizer tokenizer = new StringTokenizer(value.toString());
		FileSplit fileSplit = (FileSplit)context.getInputSplit();
		Text filePath = new Text(fileSplit.getPath().getName());
		
		while (tokenizer.hasMoreTokens()){
			context.write(new Text(tokenizer.nextToken()), filePath);
		}
    }
  }
  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class Reduce1 extends Reducer<Text, Text, Text, Text> {
   
    //private static int support_threshold;
    //private IntWritable total = new IntWritable();
      
    public void setup(Context context) {
      // support_threshold = Integer.parseInt(context.getConfiguration().get("min_support_value")); 
	    //System.out.println("st: " + support_threshold);
    }

    public void reduce(Text key, Iterable<Text> values,
                       Context context) throws IOException,InterruptedException {
		HashMap hm = new HashMap();
		int count=0;
		for(Text t: values){
			String str = t.toString();
			//Check if file name is present in the HashMap ,if File name is not present then add the Filename to the HashMap 
		    //and increment the counter by one , This condition will be satisfied on first occurrence of that word
			if(hm != null && hm.get(str) != null){
				count = (int)hm.get(str);
				hm.put(str, ++count);
			}
			else{
				//Else part will execute if file name is already added then just increase the count for that file name which is stored as 
				//key in the hash map
				hm.put(str, 1);
			}
		}
		//Emit word and [file1→count of the word1 in file1 , file2→count of the word1 in file2 ………] as output
		context.write(key, new Text(hm.toString()));

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
    job.setJarByClass(InvIdx.class);

    job.setJobName("Inverted Index");

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

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
  
    return 0;
  }
 
 
  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new InvIdx(), args);
    System.exit(res);
  }

}
//to do
//add a combiner



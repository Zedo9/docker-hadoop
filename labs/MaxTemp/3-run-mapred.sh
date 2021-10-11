#!/bin/sh

hadoop jar /tmp/labs/MaxTemp/MaxTemp.jar src.MaxTemp  /ncdc_data/ /results_maxtemp/

# rm -rf /tmp/labs/MaxTemp/results_maxtemp
hdfs dfs -get /results_maxtemp/ /tmp/labs/MaxTemp/
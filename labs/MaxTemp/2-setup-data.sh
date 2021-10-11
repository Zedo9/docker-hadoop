#!/bin/sh

hdfs dfs -mkdir /ncdc_data
hdfs dfs -copyFromLocal /tmp/labs/MaxTemp/NCDC_data/1901 /ncdc_data/
hdfs dfs -copyFromLocal /tmp/labs/MaxTemp/NCDC_data/1902 /ncdc_data/

hdfs dfs -ls /ncdc_data
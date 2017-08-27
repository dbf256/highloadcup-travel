#!/bin/bash

mkdir /opt/data
unzip /tmp/data/data.zip -d /travel/data
cp /tmp/data/options.txt /travel/data
export DATA_PATH=/travel/data
cd /travel/app
java -Xms3500m -Xmx3500m -XX:MaxNewSize=1700m -XX:+PrintGCTimeStamps -XX:+PrintGC -cp travel-1.0.jar travel.Main

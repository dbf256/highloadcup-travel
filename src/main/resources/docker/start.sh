#!/bin/bash

mkdir /opt/data
unzip /tmp/data/data.zip -d /travel/data
cp /tmp/data/options.txt /travel/data
export DATA_PATH=/travel/data
cd /travel/app
java -XX:MaxNewSize=2300m -XX:NewSize=2300m -Xms3500m -Xmx3500m -XX:+PrintGCTimeStamps -XX:+PrintGC -cp travel-1.0.jar travel.Main

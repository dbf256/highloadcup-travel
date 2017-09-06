#!/bin/bash

mkdir /opt/data
unzip /tmp/data/data.zip -d /travel/data
cp /tmp/data/options.txt /travel/data
export DATA_PATH=/travel/data
cd /travel/app
java -XX:MaxNewSize=2400m -XX:NewSize=2400m -XX:SurvivorRatio=6 -XX:-UseAdaptiveSizePolicy -Xms3600m -Xmx3600m -XX:+PrintGCTimeStamps -XX:+PrintGC -cp travel-1.0.jar travel.Main

#!/bin/sh

docker build -t main .
docker tag main stor.highloadcup.ru/travels/neat_yak
docker push stor.highloadcup.ru/travels/neat_yak
#!/bin/bash

echo "-----배포 시작-----"
cd /home/ubuntu/shopch-server
sudo fuser -k -n tcp 8080 || true
nohup java -jar -Dspring.profiles.active=prod project.jar > ./output.log 2>&1 &
echo "-----배포 완료-----"
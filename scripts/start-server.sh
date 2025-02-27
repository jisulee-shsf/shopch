#!/bin/bash

echo "-----배포 시작-----"

cd /home/ubuntu/shopch-server
for port in 8080 8081; do
  sudo fuser -k -n tcp $port || true
  nohup java -jar -Dspring.profiles.active=prod project.jar --server.port=$port >> output.log 2>&1 &
done

echo "-----배포 완료-----"
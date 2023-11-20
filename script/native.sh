#!/bin/bash

startTime=`date +%Y%m%d-%H:%M:%S`
startTime_s=`date +%s`

mvn clean install -DskipTests -P tencent -pl common -am --no-transfer-progress

cd app-runner || exit

mvn clean native:compile -DskipTests -P tencent,native --no-transfer-progress

endTime=`date +%Y%m%d-%H:%M:%S`
endTime_s=`date +%s`
sumTime=$[ $endTime_s - $startTime_s ]

echo "开始时间：$startTime"
echo "结束时间：$endTime"
echo "Total: $sumTime seconds"
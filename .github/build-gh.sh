#!/bin/bash

startTime=`date +%Y%m%d-%H:%M:%S`
startTime_s=`date +%s`
mvn clean install -DskipTests -pl common -am --no-transfer-progress

cd app || exit

mvn clean native:compile -P native --no-transfer-progress -Dfile.encoding=UTF-8

endTime=`date +%Y%m%d-%H:%M:%S`
endTime_s=`date +%s`
sumTime=$[ $endTime_s - $startTime_s ]

echo "开始时间：$startTime"
echo "结束时间：$endTime"
echo "Total: $sumTime seconds"
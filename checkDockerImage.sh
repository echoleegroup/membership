#!/bin/sh

pid=`docker ps -a | grep aiovpoint | awk '{print$1}'`
echo $pid
if [ -z $pid ]
then
 echo "aiovpoint not exist"
else
 docker rm -f aiovpoint
fi
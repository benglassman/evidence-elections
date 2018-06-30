#!/bin/bash
# develop with Docker env mounted on local filesystem

# to develop run:
# cd /home/src/
# sbt run
# app will be avilable on port 9000
COMMAND="/bin/bash"
docker run -it -v $(pwd):/home/src/  -p :9000:9000 quay.io/zachglassman/scala-sbt $COMMAND

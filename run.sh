#!/bin/bash

# Start the Spring Boot application in the background
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" &

# Monitor the ./src/ directory for changes and recompile on changes
while true; do
  inotifywait -e modify,create,delete,move -r ./src/ && ./mvnw compile
done

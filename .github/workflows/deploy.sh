#!/usr/bin/env bash

# Stop script on first error
set -ex -o pipefail

echo "Deploying via remote SSH"

sshpass -p "$USER_PW" ssh -o StrictHostKeyChecking=no "$USER_PLUS_DROPLET" \
  "docker pull fahrican/get-things-done:latest \
  && docker stop get-things-done-be \
  && docker rm get-things-done-be \
  && docker run -d -e SERVER_PORT=$SERVER_PORT \
                   -e DATASOURCE_URL=$DATASOURCE_URL \
                   -e DATASOURCE_USERNAME=$DATASOURCE_USERNAME \
                   -e DATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
  --name get-things-done-be -p 9091:9091 fahrican/get-things-done:latest \
  && docker system prune -af" # remove unused images to free up space

echo "Successfully deployed, hoooooray!"
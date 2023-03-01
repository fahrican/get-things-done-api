#!/usr/bin/env bash

# Stop script on first error
set -ex -o pipefail

echo "Deploying via remote SSH"

sshpass -p "$DROPLET_PORT" "$USER_PW" ssh -o StrictHostKeyChecking=no "$USER_PLUS_DROPLET" \
  "docker pull fahrican/get-things-done:latest \
  && docker stop get-things-done-be \
  && docker rm get-things-done-be \
  && docker run -d -e SERVER_PORT=$SERVER_PORT \
                   -e JPA_DATABASE=$JPA_DATABASE \
                   -e DATASOURCE_URL=$DATASOURCE_URL \
                   -e DATASOURCE_DRIVER_CLASS_NAME=$DATASOURCE_DRIVER_CLASS_NAME \
                   -e DATASOURCE_USERNAME=$DATASOURCE_USERNAME \
                   -e DATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
  --name get-things-done-be -p $SERVER_PORT:$SERVER_PORT fahrican/get-things-done:latest \
  && docker system prune -af" # remove unused images to free up space

echo "Successfully deployed, hoooooray!"
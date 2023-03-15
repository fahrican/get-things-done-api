#!/usr/bin/env bash

# Stop script on first error
set -ex -o pipefail

echo "Deploying via remote SSH"

sshpass -p "$USER_PW" ssh -o StrictHostKeyChecking=no -p "$DROPLET_PORT" "$USER_PLUS_DROPLET" \
  "docker pull fahrican/get-things-done-api:latest \
  && docker stop get-things-done-api \
  && docker rm get-things-done-api \
  && docker run -d -e SERVER_PORT=$SERVER_PORT \
                   -e JPA_DATABASE=$JPA_DATABASE \
                   -e DATASOURCE_URL=$DATASOURCE_URL \
                   -e DATASOURCE_DRIVER_CLASS_NAME=$DATASOURCE_DRIVER_CLASS_NAME \
                   -e DATASOURCE_USERNAME=$DATASOURCE_USERNAME \
                   -e DATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
  --name get-things-done-api -p $SERVER_PORT:$SERVER_PORT fahrican/get-things-done-api:latest \
  && docker system prune -af" # remove unused images to free up space

echo "Successfully deployed, hoooooray!"

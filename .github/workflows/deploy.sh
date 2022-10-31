#!/bin/sh

# Stop script on first error
set -e

IMAGE_NAME=fahrican/get-things-done
IMAGE_TAG=latest

echo "${SSH_KEY}" | base64 -d > ssh_key
chmod 600 ssh_key # private keys need to have strict permission to be accepted by SSH agent

# Add production server to known hosts
echo "${SERVER_PUBLIC_KEY}" | base64 -d >> ~/.ssh/known_hosts

echo "Deploying via remote SSH"
ssh -i ssh_key "fahri@134.209.251.159" \
  "docker pull ${IMAGE_NAME}:${IMAGE_TAG} \
  && docker stop live-container \
  && docker rm live-container \
  && docker run --init -d --name live-container -p 9091:9091 ${IMAGE_NAME}:${IMAGE_TAG} \
  && docker system prune -af" # remove unused images to free up space

echo "Successfully deployed, hooray!"
#!/usr/bin/env bash

# Stop script on first error
set -ex -o pipefail

echo "Deploying via remote SSH"

sshpass -p "$USER_PW" ssh -o StrictHostKeyChecking=no -p "$DROPLET_PORT" "$USER_PLUS_DROPLET" \
  "docker-compose pull && docker-compose up -d && docker system prune -af"

echo "Successfully deployed, hooray!"

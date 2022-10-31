#!/usr/bin/env bash

# Stop script on first error
set -ex -o pipefail

IMAGE_NAME=fahrican/get-things-done
IMAGE_TAG=latest
SERVER_PUBLIC_KEY=ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDkS4uqIEcjoHNnI+7x/xVk6ftwF0VLqSEO0PqQ71b31EYxkdAFDo6Fb5pH2EvK2iZ8ADTcFKK8YV4BWHkinaq62MfMGVC7lLMjEaZYCpNdK0vj+ZP22aXhEY8w9uqQwy4i1N+f+azWg+HuaWIE9wC7eFs/CZ+fFj892NVKak8t2hfMROJLRLVIEiMB2OohaCYdR2XJHN5kOLRzZEcJLPgONTLG6U1wKZF5x3UzumjbB6B3HfR14z1MqIU+BwMVnTsbTtjf7OG+oEIMyPFOXH6ymTohSPavNuLx6iFePEDP+kfXc3DCTlUux5cmBtZC2UMxpBv97mE+WXeUr4bxFQb3ZDwrX3p9HwDY5LVwoPVreyRvdtR6n3lPHS9zmE0cKyDL8iVVm5hUCCY/lL2bQHUga/uPd8ErJUvcm5ImyJKaC1597pdeUob6rB7p5AdYBV1Nk/2f3VQz4QYxPmqc3QivWV8t8qd2YY/lPbzTK3DdV/GKwpQj9H+B66IDXcXpFL8= fahri@s-1vcpu-1gb-fra1-01

echo "${SSH_KEY}" | base64 -d > ssh_key
chmod 600 ssh_key # private keys need to have strict permission to be accepted by SSH agent

# .ssh folder
mkdir .ssh

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
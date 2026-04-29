#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 || $# -gt 4 ]]; then
  echo "Usage: $0 <ec2-public-ip> <ssh-private-key-path> [jar-path] [app-port]"
  exit 1
fi

EC2_PUBLIC_IP="$1"
SSH_KEY_PATH="$2"
JAR_PATH="${3:-target/personal-book-catalog-0.0.1-SNAPSHOT.jar}"
APP_PORT="${4:-8080}"
REMOTE="ec2-user@${EC2_PUBLIC_IP}"

if [[ ! -f "${JAR_PATH}" ]]; then
  echo "JAR not found at ${JAR_PATH}. Building with Maven..."
  mvn clean package
fi

if [[ ! -f "${JAR_PATH}" ]]; then
  echo "JAR still not found at ${JAR_PATH}."
  exit 1
fi

if [[ ! -f "data/BookList.csv" ]]; then
  echo "data/BookList.csv is missing."
  exit 1
fi

scp -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "${JAR_PATH}" "${REMOTE}:/tmp/app.jar"
scp -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "data/BookList.csv" "${REMOTE}:/tmp/BookList.csv"

ssh -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "${REMOTE}" \
  "sudo mv /tmp/app.jar /opt/personal-book-catalog/app.jar && \
   sudo mv /tmp/BookList.csv /opt/personal-book-catalog/data/BookList.csv && \
   sudo chown -R bookcatalog:bookcatalog /opt/personal-book-catalog && \
   sudo systemctl restart personal-book-catalog.service && \
   sudo systemctl status personal-book-catalog.service --no-pager"

echo "Deployment complete. Open: http://${EC2_PUBLIC_IP}:${APP_PORT}/books"

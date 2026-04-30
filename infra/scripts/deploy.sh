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

if [[ -z "${APP_AUTH_ADMIN_USERNAME:-}" || -z "${APP_AUTH_ADMIN_PASSWORD_HASH:-}" ]]; then
  echo "APP_AUTH_ADMIN_USERNAME and APP_AUTH_ADMIN_PASSWORD_HASH must be set."
  exit 1
fi

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

APP_ENV_FILE="$(mktemp)"
trap 'rm -f "${APP_ENV_FILE}"' EXIT
cat > "${APP_ENV_FILE}" <<EOF
APP_AUTH_ADMIN_USERNAME=${APP_AUTH_ADMIN_USERNAME}
APP_AUTH_ADMIN_PASSWORD_HASH=${APP_AUTH_ADMIN_PASSWORD_HASH}
EOF

scp -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "${JAR_PATH}" "${REMOTE}:/tmp/app.jar"
scp -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "data/BookList.csv" "${REMOTE}:/tmp/BookList.csv"
scp -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "${APP_ENV_FILE}" "${REMOTE}:/tmp/app.env"

ssh -i "${SSH_KEY_PATH}" -o StrictHostKeyChecking=accept-new "${REMOTE}" \
  "if ! sudo grep -q '^EnvironmentFile=-/opt/personal-book-catalog/app.env$' /etc/systemd/system/personal-book-catalog.service; then \
     sudo sed -i '/^WorkingDirectory=/a EnvironmentFile=-/opt/personal-book-catalog/app.env' /etc/systemd/system/personal-book-catalog.service; \
   fi && \
   sudo mv /tmp/app.jar /opt/personal-book-catalog/app.jar && \
   sudo mv /tmp/BookList.csv /opt/personal-book-catalog/data/BookList.csv && \
   sudo mv /tmp/app.env /opt/personal-book-catalog/app.env && \
   sudo chmod 600 /opt/personal-book-catalog/app.env && \
   sudo chown -R bookcatalog:bookcatalog /opt/personal-book-catalog && \
   sudo systemctl daemon-reload && \
   sudo systemctl restart personal-book-catalog.service && \
   sudo systemctl status personal-book-catalog.service --no-pager"

echo "Deployment complete. Open: http://${EC2_PUBLIC_IP}:${APP_PORT}/books"

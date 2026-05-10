#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 <instance-name> <zone> [jar-path] [--reset-data]"
}

validate_env_value() {
  local name="$1"
  local value="$2"

  if [[ "${value}" == *$'\n'* || "${value}" == *$'\r'* ]]; then
    echo "${name} must not contain newline characters."
    exit 1
  fi

  if [[ ! "${value}" =~ ^[A-Za-z0-9_./:@%+=,\{\}\$-]+$ ]]; then
    echo "${name} contains characters that are unsafe for the systemd env file."
    exit 1
  fi
}

if [[ $# -lt 2 || $# -gt 4 ]]; then
  usage
  exit 1
fi

INSTANCE_NAME="$1"
ZONE="$2"
JAR_PATH="target/personal-book-catalog-0.0.1-SNAPSHOT.jar"
RESET_DATA="false"
APP_PORT="${APP_PORT:-8080}"

shift 2
while [[ $# -gt 0 ]]; do
  case "$1" in
    --reset-data)
      RESET_DATA="true"
      ;;
    *)
      JAR_PATH="$1"
      ;;
  esac
  shift
done

if [[ -z "${APP_AUTH_ADMIN_USERNAME:-}" || -z "${APP_AUTH_ADMIN_PASSWORD_HASH:-}" ]]; then
  echo "APP_AUTH_ADMIN_USERNAME and APP_AUTH_ADMIN_PASSWORD_HASH must be set."
  exit 1
fi
validate_env_value "APP_AUTH_ADMIN_USERNAME" "${APP_AUTH_ADMIN_USERNAME}"
validate_env_value "APP_AUTH_ADMIN_PASSWORD_HASH" "${APP_AUTH_ADMIN_PASSWORD_HASH}"

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

SSH_TARGET="${INSTANCE_NAME}"
if [[ -n "${GCP_SSH_USERNAME:-}" ]]; then
  SSH_TARGET="${GCP_SSH_USERNAME}@${INSTANCE_NAME}"
fi

APP_ENV_FILE="$(mktemp)"
trap 'rm -f "${APP_ENV_FILE}"' EXIT
chmod 600 "${APP_ENV_FILE}"
cat > "${APP_ENV_FILE}" <<EOF
APP_AUTH_ADMIN_USERNAME=${APP_AUTH_ADMIN_USERNAME}
APP_AUTH_ADMIN_PASSWORD_HASH=${APP_AUTH_ADMIN_PASSWORD_HASH}
SPRING_DATASOURCE_URL=jdbc:h2:file:/opt/personal-book-catalog/data/booksdb;AUTO_SERVER=TRUE
APP_BOOTSTRAP_BOOK_CSV_PATH=/opt/personal-book-catalog/data/BookList.csv
APP_BOOTSTRAP_MARKER_PATH=/opt/personal-book-catalog/data/.book-bootstrap.done
EOF

gcloud compute scp "${JAR_PATH}" "${SSH_TARGET}:/tmp/app.jar" --zone "${ZONE}"
gcloud compute scp "data/BookList.csv" "${SSH_TARGET}:/tmp/BookList.csv" --zone "${ZONE}"
gcloud compute scp "${APP_ENV_FILE}" "${SSH_TARGET}:/tmp/app.env" --zone "${ZONE}"

REMOTE_SCRIPT="$(mktemp)"
trap 'rm -f "${APP_ENV_FILE}" "${REMOTE_SCRIPT}"' EXIT
cat > "${REMOTE_SCRIPT}" <<EOF
set -euo pipefail

sudo systemctl stop personal-book-catalog.service || true

sudo tee /etc/systemd/system/personal-book-catalog.service >/dev/null <<'SERVICE'
[Unit]
Description=Personal Book Catalog Spring Boot service
After=network-online.target
Wants=network-online.target
ConditionPathExists=/opt/personal-book-catalog/app.jar

[Service]
Type=simple
User=bookcatalog
WorkingDirectory=/opt/personal-book-catalog
EnvironmentFile=-/opt/personal-book-catalog/app.env
ExecStart=/usr/bin/java -jar /opt/personal-book-catalog/app.jar --server.port=${APP_PORT}
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
SERVICE

sudo mv /tmp/app.jar /opt/personal-book-catalog/app.jar
sudo mv /tmp/BookList.csv /opt/personal-book-catalog/data/BookList.csv
sudo mv /tmp/app.env /opt/personal-book-catalog/app.env
sudo chmod 600 /opt/personal-book-catalog/app.env
sudo chown -R bookcatalog:bookcatalog /opt/personal-book-catalog
EOF

if [[ "${RESET_DATA}" == "true" ]]; then
  cat >> "${REMOTE_SCRIPT}" <<'EOF'
sudo rm -f /opt/personal-book-catalog/data/booksdb*
sudo rm -f /opt/personal-book-catalog/data/.book-bootstrap.done
EOF
fi

cat >> "${REMOTE_SCRIPT}" <<'EOF'
sudo systemctl daemon-reload
sudo systemctl restart personal-book-catalog.service
sudo systemctl status personal-book-catalog.service --no-pager
EOF

gcloud compute ssh "${SSH_TARGET}" --zone "${ZONE}" --command "bash -s" < "${REMOTE_SCRIPT}"

PUBLIC_IP="$(gcloud compute instances describe "${INSTANCE_NAME}" --zone "${ZONE}" --format='get(networkInterfaces[0].accessConfigs[0].natIP)')"
echo "Deployment complete. Open: http://${PUBLIC_IP}:${APP_PORT}/books"

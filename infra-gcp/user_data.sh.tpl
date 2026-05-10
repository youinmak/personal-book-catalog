#!/bin/bash
set -euxo pipefail

APP_USER="${app_user}"
APP_DIR="${app_dir}"

export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y openjdk-21-jre-headless

id -u "$${APP_USER}" >/dev/null 2>&1 || useradd --system --create-home --shell /usr/sbin/nologin "$${APP_USER}"

mkdir -p "$${APP_DIR}/data"
chown -R "$${APP_USER}:$${APP_USER}" "$${APP_DIR}"

cat > "$${APP_DIR}/app.env" <<ENVFILE
APP_AUTH_ADMIN_USERNAME=
APP_AUTH_ADMIN_PASSWORD_HASH=
SPRING_DATASOURCE_URL=jdbc:h2:file:$${APP_DIR}/data/booksdb;AUTO_SERVER=TRUE
APP_BOOTSTRAP_BOOK_CSV_PATH=$${APP_DIR}/data/BookList.csv
APP_BOOTSTRAP_MARKER_PATH=$${APP_DIR}/data/.book-bootstrap.done
ENVFILE
chmod 600 "$${APP_DIR}/app.env"
chown "$${APP_USER}:$${APP_USER}" "$${APP_DIR}/app.env"

cat > /etc/systemd/system/personal-book-catalog.service <<SERVICE
[Unit]
Description=Personal Book Catalog Spring Boot service
After=network-online.target
Wants=network-online.target
ConditionPathExists=$${APP_DIR}/app.jar

[Service]
Type=simple
User=$${APP_USER}
WorkingDirectory=$${APP_DIR}
EnvironmentFile=-$${APP_DIR}/app.env
ExecStart=/usr/bin/java -jar $${APP_DIR}/app.jar --server.port=${app_port}
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
SERVICE

systemctl daemon-reload
systemctl enable personal-book-catalog.service

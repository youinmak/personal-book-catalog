#!/bin/bash
set -euxo pipefail

APP_USER="${app_user}"
APP_DIR="${app_dir}"

dnf update -y
dnf install -y java-21-amazon-corretto-headless

id -u "$${APP_USER}" >/dev/null 2>&1 || useradd --system --create-home --shell /sbin/nologin "$${APP_USER}"

mkdir -p "$${APP_DIR}/data"
chown -R "$${APP_USER}:$${APP_USER}" "$${APP_DIR}"

cat > /etc/systemd/system/personal-book-catalog.service <<SERVICE
[Unit]
Description=Personal Book Catalog Spring Boot service
After=network.target
ConditionPathExists=$${APP_DIR}/app.jar

[Service]
Type=simple
User=$${APP_USER}
WorkingDirectory=$${APP_DIR}
ExecStart=/usr/bin/java -jar $${APP_DIR}/app.jar --server.port=${app_port} --spring.datasource.url=jdbc:h2:file:$${APP_DIR}/data/booksdb\;AUTO_SERVER=TRUE --app.bootstrap.book-csv-path=$${APP_DIR}/data/BookList.csv --app.bootstrap.marker-path=$${APP_DIR}/data/.book-bootstrap.done
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
SERVICE

systemctl daemon-reload
systemctl enable personal-book-catalog.service

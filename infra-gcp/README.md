# GCP Deployment (Free Tier-Oriented)

This folder contains Terraform infrastructure code for deploying the app on a single Google Compute Engine VM.

## What this creates

- 1 Compute Engine VM (`e2-micro` by default)
- 1 standard persistent boot disk (`pd-standard`, 10 GB by default)
- 1 small custom VPC and subnet
- 2 firewall rules: HTTP `8080` from anywhere and SSH `22` from your CIDR
- 1 systemd service for the Spring Boot app
- Compute Engine API enablement for the configured project

This intentionally avoids managed databases and load balancers to keep the deployment aligned with GCP Free Tier limits.

## Free Tier notes

Google Cloud Free Tier currently includes limited Compute Engine usage for:

- 1 non-preemptible `e2-micro` VM per month in `us-west1`, `us-central1`, or `us-east1`
- 30 GB-months standard persistent disk
- Limited outbound transfer

Verify your current eligibility and limits before long-running usage:

https://cloud.google.com/free/docs/gcp-free-tier

## Prerequisites

- GCP project with billing enabled
- Terraform 1.6+
- Google Cloud CLI (`gcloud`)
- Application Default Credentials or service account credentials
- SSH public key for VM login

Terraform enables the Compute Engine API for the configured project. If the API was just enabled, GCP can take a few minutes to propagate the change; rerun `plan` or `apply` if you see a temporary API activation error.

## 1) Authenticate

```bash
gcloud auth application-default login
```

If you prefer a service account, set:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account.json"
```

Set the project id used by the Terraform wrapper:

```bash
export PERSONAL_GCP_PROJECT_ID="your-gcp-project-id"
```

## 2) Configure Terraform variables

```bash
cd infra-gcp
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars`:

```hcl
ssh_username        = "your-linux-user"
ssh_public_key_path = "~/.ssh/id_rsa.pub"
ssh_allowed_cidr    = "203.0.113.5/32"
```

How to choose the values:

- `ssh_username`: Linux username for SSH on the VM. Your local username from `whoami` is usually fine.
- `ssh_public_key_path`: Public SSH key to inject into the VM, commonly `~/.ssh/id_rsa.pub` or `~/.ssh/id_ed25519.pub`.
- `ssh_allowed_cidr`: Your public IP with `/32`. Example: if `curl ifconfig.me` returns `198.51.100.10`, use `198.51.100.10/32`.

Optional values can stay at their defaults:

```hcl
# gcp_region        = "us-central1"
# gcp_zone          = "us-central1-a"
# project_name      = "personal-book-catalog"
# machine_type      = "e2-micro"
# boot_disk_size_gb = 10
# subnet_cidr       = "10.10.0.0/24"
# app_port          = 8080
```

## 3) Provision infrastructure

```bash
chmod +x scripts/tf.sh
./scripts/tf.sh init
./scripts/tf.sh plan
./scripts/tf.sh apply
```

If you run `terraform` directly, this configuration intentionally fails and asks you to use the wrapper.

Get output values:

```bash
./scripts/tf.sh output
```

Important outputs:

- `instance_name`
- `zone`
- `public_ip`
- `app_url`

If `plan` fails with `SERVICE_DISABLED` for Compute Engine, wait a few minutes and retry:

```bash
./scripts/tf.sh plan
```

## 4) Build and deploy app JAR

From repository root:

```bash
export APP_AUTH_ADMIN_USERNAME=admin
export APP_AUTH_ADMIN_PASSWORD_HASH='$2y$10$...'
chmod +x infra-gcp/scripts/deploy.sh
infra-gcp/scripts/deploy.sh <instance-name> <zone> [jar-path]
```

The deploy script writes credentials to a temporary local env file with `0600` permissions, uploads it to the VM, and installs it as `/opt/personal-book-catalog/app.env` with `0600` permissions.

Credential values are validated before writing the env file. `APP_AUTH_ADMIN_USERNAME` and `APP_AUTH_ADMIN_PASSWORD_HASH` must not contain newline characters and must only use characters safe for a systemd env file:

```text
A-Z a-z 0-9 _ . / : @ % + = , { } $ -
```

This supports standard BCrypt hashes such as `$2y$10$...`. The value must be a BCrypt hash, not a plaintext password.

If you changed `app_port` in Terraform, set the same port for the final deploy URL output:

```bash
export APP_PORT=8080
```

If your SSH username differs from the local user used by `gcloud`, set:

```bash
export GCP_SSH_USERNAME="your-linux-user"
```

The script will:

- Build the JAR if missing
- Upload the JAR, `data/BookList.csv`, and runtime auth env
- Restart `personal-book-catalog.service`

By default, deploys preserve the remote H2 database and bootstrap marker. To force a fresh CSV bootstrap and reset remote data:

```bash
infra-gcp/scripts/deploy.sh <instance-name> <zone> --reset-data
```

The reset option removes `/opt/personal-book-catalog/data/booksdb*` and `/opt/personal-book-catalog/data/.book-bootstrap.done` before restarting the service.

## 5) Access app

- Application: `http://<public-ip>:8080/books`

## Useful operations

Check VM service status:

```bash
gcloud compute ssh <instance-name> --zone <zone> --command "sudo systemctl status personal-book-catalog.service --no-pager"
```

View app logs:

```bash
gcloud compute ssh <instance-name> --zone <zone> --command "sudo journalctl -u personal-book-catalog.service -n 100 --no-pager"
```

Destroy resources:

```bash
cd infra-gcp
./scripts/tf.sh destroy
```

## Notes

- `terraform.tfvars` is ignored by git because it contains local project and SSH values.
- The remote H2 database lives under `/opt/personal-book-catalog/data`.
- The initial CSV import remains marker-based through `/opt/personal-book-catalog/data/.book-bootstrap.done`.
- Free Tier eligibility depends on account, region, and current Google Cloud terms.

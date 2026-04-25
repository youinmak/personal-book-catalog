# AWS Deployment (Free Tier-Oriented)

This folder contains Terraform infrastructure code for deploying the app on a single EC2 instance.

## What this creates

- 1 EC2 instance (`t2.micro` by default)
- 1 Security Group (HTTP `8080`, SSH `22` from your CIDR)
- 1 IAM role + instance profile for SSM access
- Root EBS volume (10 GB by default)

This is intentionally minimal and avoids managed services that can increase monthly cost.

## Prerequisites

- AWS account
- Terraform 1.6+
- Existing EC2 key pair in your target region
- Your public IP CIDR for SSH (for example `x.x.x.x/32`)

## AWS authentication options

`PERSONAL_AWS_ACCESS_KEY` and `PERSONAL_AWS_SECRET_KEY` are mandatory for this infra setup.
Run Terraform only via `infra/scripts/tf.sh`.

### Using your custom env vars

```bash
export PERSONAL_AWS_ACCESS_KEY="AKIA..."
export PERSONAL_AWS_SECRET_KEY="..."
# export PERSONAL_AWS_SESSION_TOKEN="..."   # optional
```

Then run Terraform through the wrapper:

```bash
cd infra
./scripts/tf.sh init
./scripts/tf.sh plan
./scripts/tf.sh apply
```
If you run `terraform` directly, this configuration intentionally fails and asks you to use the wrapper.

## 1) Provision infrastructure

```bash
cd infra
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your values
./scripts/tf.sh init
./scripts/tf.sh plan
./scripts/tf.sh apply
```

Get output values:

```bash
./scripts/tf.sh output
```

Important output:

- `public_ip`
- `app_url`

## 2) Build and deploy app JAR

From repository root:

```bash
chmod +x infra/scripts/deploy.sh
infra/scripts/deploy.sh <public-ip> <path-to-private-key> [jar-path] [app-port]
```

Example:

```bash
infra/scripts/deploy.sh 3.91.10.20 ~/.ssh/my-key.pem
```

The script will:

- Build JAR if missing
- Upload JAR and `data/BookList.csv`
- Restart `personal-book-catalog` systemd service

## 3) Access app

- Application: `http://<public-ip>:8080/books`

## Free tier notes

- Keep `instance_type = "t2.micro"` (or another free-tier-eligible micro in your account/region).
- Keep total EBS usage low (default is 10 GB).
- Destroy resources when not needed:

```bash
cd infra
./scripts/tf.sh destroy
```

AWS Free Tier terms can vary by account age, region, and plan. Verify your current AWS billing page before long-running usage.

#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${PERSONAL_AWS_ACCESS_KEY:-}" || -z "${PERSONAL_AWS_SECRET_KEY:-}" ]]; then
  echo "PERSONAL_AWS_ACCESS_KEY and PERSONAL_AWS_SECRET_KEY must be set."
  exit 1
fi

export TF_VAR_aws_access_key_id="${PERSONAL_AWS_ACCESS_KEY}"
export TF_VAR_aws_secret_access_key="${PERSONAL_AWS_SECRET_KEY}"
export TF_VAR_wrapper_guard="tf-wrapper"

if [[ -n "${PERSONAL_AWS_SESSION_TOKEN:-}" ]]; then
  export TF_VAR_aws_session_token="${PERSONAL_AWS_SESSION_TOKEN}"
fi

exec terraform "$@"

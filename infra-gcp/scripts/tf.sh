#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${PERSONAL_GCP_PROJECT_ID:-}" ]]; then
  echo "PERSONAL_GCP_PROJECT_ID must be set."
  exit 1
fi

export TF_VAR_gcp_project_id="${PERSONAL_GCP_PROJECT_ID}"
export TF_VAR_wrapper_guard="tf-wrapper"

exec terraform "$@"

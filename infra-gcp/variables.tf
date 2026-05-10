variable "gcp_project_id" {
  description = "GCP project id to deploy into"
  type        = string
  nullable    = false
}

variable "wrapper_guard" {
  description = "Internal guard set by infra-gcp/scripts/tf.sh to enforce wrapper usage"
  type        = string
  nullable    = false
  sensitive   = true

  validation {
    condition     = var.wrapper_guard == "tf-wrapper"
    error_message = "Run Terraform via ./scripts/tf.sh only."
  }
}

variable "gcp_region" {
  description = "GCP region to deploy into. Keep this in a Compute Engine Free Tier eligible region."
  type        = string
  default     = "us-central1"
}

variable "gcp_zone" {
  description = "GCP zone to deploy into. Keep this in the selected Free Tier eligible region."
  type        = string
  default     = "us-central1-a"
}

variable "project_name" {
  description = "Prefix used in GCP resource names"
  type        = string
  default     = "personal-book-catalog"
}

variable "machine_type" {
  description = "Compute Engine machine type. Keep e2-micro for Free Tier sizing."
  type        = string
  default     = "e2-micro"
}

variable "ssh_username" {
  description = "Linux username to inject into instance SSH metadata"
  type        = string
}

variable "ssh_public_key_path" {
  description = "Path to the public SSH key to inject into instance metadata"
  type        = string
}

variable "ssh_allowed_cidr" {
  description = "CIDR block allowed to SSH to the VM (for example: 203.0.113.5/32)"
  type        = string
}

variable "boot_disk_size_gb" {
  description = "Boot disk size in GB. Keep total standard persistent disk usage within the Free Tier allowance."
  type        = number
  default     = 10
}

variable "subnet_cidr" {
  description = "CIDR range for the app subnet"
  type        = string
  default     = "10.10.0.0/24"
}

variable "app_port" {
  description = "HTTP port used by the Spring Boot application"
  type        = number
  default     = 8080
}

variable "aws_region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "ap-south-1"
}

variable "aws_access_key_id" {
  description = "Mandatory AWS access key id for Terraform provider authentication"
  type        = string
  nullable    = false
  sensitive   = true
}

variable "aws_secret_access_key" {
  description = "Mandatory AWS secret access key for Terraform provider authentication"
  type        = string
  nullable    = false
  sensitive   = true
}

variable "wrapper_guard" {
  description = "Internal guard set by infra/scripts/tf.sh to enforce wrapper usage"
  type        = string
  nullable    = false
  sensitive   = true

  validation {
    condition     = var.wrapper_guard == "tf-wrapper"
    error_message = "Run Terraform via ./scripts/tf.sh only."
  }
}

variable "aws_session_token" {
  description = "Optional AWS session token when using temporary credentials"
  type        = string
  default     = null
  sensitive   = true
}

variable "project_name" {
  description = "Prefix used in AWS resource names"
  type        = string
  default     = "personal-book-catalog"
}

variable "instance_type" {
  description = "EC2 instance type. Keep t2.micro for Free Tier sizing."
  type        = string
  default     = "t2.micro"
}

variable "key_pair_name" {
  description = "Existing EC2 key pair name for SSH access"
  type        = string
}

variable "ssh_allowed_cidr" {
  description = "CIDR block allowed to SSH to EC2 (for example: 203.0.113.5/32)"
  type        = string
}

variable "root_volume_size_gb" {
  description = "Root EBS volume size in GB. Keep total EBS usage within your Free Tier allowance."
  type        = number
  default     = 10
}

variable "app_port" {
  description = "HTTP port used by the Spring Boot application"
  type        = number
  default     = 8080
}

output "instance_id" {
  description = "EC2 instance id"
  value       = aws_instance.app.id
}

output "public_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.app.public_ip
}

output "app_url" {
  description = "Public URL for the Spring Boot app"
  value       = "http://${aws_instance.app.public_ip}:${var.app_port}/books"
}

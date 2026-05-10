output "instance_name" {
  description = "Compute Engine instance name"
  value       = google_compute_instance.app.name
}

output "zone" {
  description = "Compute Engine zone"
  value       = google_compute_instance.app.zone
}

output "public_ip" {
  description = "Public IP of the Compute Engine instance"
  value       = google_compute_instance.app.network_interface[0].access_config[0].nat_ip
}

output "app_url" {
  description = "Public URL for the Spring Boot app"
  value       = "http://${google_compute_instance.app.network_interface[0].access_config[0].nat_ip}:${var.app_port}/books"
}

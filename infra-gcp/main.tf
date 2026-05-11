resource "google_project_service" "compute" {
  project            = var.gcp_project_id
  service            = "compute.googleapis.com"
  disable_on_destroy = false
}

data "google_compute_image" "ubuntu_2404" {
  family  = "ubuntu-2404-lts-amd64"
  project = "ubuntu-os-cloud"

  depends_on = [google_project_service.compute]
}

locals {
  app_tag = "${var.project_name}-app"
}

resource "google_compute_network" "app" {
  name                    = "${var.project_name}-network"
  auto_create_subnetworks = false

  depends_on = [google_project_service.compute]
}

resource "google_compute_subnetwork" "app" {
  name          = "${var.project_name}-subnet"
  ip_cidr_range = var.subnet_cidr
  region        = var.gcp_region
  network       = google_compute_network.app.id
}

resource "google_compute_firewall" "app" {
  name    = "${var.project_name}-app"
  network = google_compute_network.app.name

  allow {
    protocol = "tcp"
    ports    = [tostring(var.app_port)]
  }

  source_ranges = ["0.0.0.0/0"]
  target_tags   = [local.app_tag]
}

resource "google_compute_firewall" "ssh" {
  name    = "${var.project_name}-ssh"
  network = google_compute_network.app.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = [var.ssh_allowed_cidr]
  target_tags   = [local.app_tag]
}

resource "google_compute_instance" "app" {
  name         = var.project_name
  machine_type = var.machine_type
  zone         = var.gcp_zone
  tags         = [local.app_tag]

  boot_disk {
    initialize_params {
      image = data.google_compute_image.ubuntu_2404.self_link
      size  = var.boot_disk_size_gb
      type  = "pd-standard"
    }
  }

  network_interface {
    network    = google_compute_network.app.self_link
    subnetwork = google_compute_subnetwork.app.self_link

    access_config {
    }
  }

  metadata = {
    ssh-keys = "${var.ssh_username}:${file(pathexpand(var.ssh_public_key_path))}"
  }

  metadata_startup_script = templatefile("${path.module}/user_data.sh.tpl", {
    app_user = "bookcatalog"
    app_dir  = "/opt/personal-book-catalog"
    app_port = var.app_port
  })
}

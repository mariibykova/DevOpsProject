terraform {
  required_providers {
    twc = {
      source  = "tf.timeweb.cloud/timeweb-cloud/timeweb-cloud"
      version = "~> 1.6"
    }
  }
  required_version = ">= 1.6, < 2.0"

  backend "s3" {
    endpoints = {
      s3 = "https://s3.timeweb.cloud"
    }
    bucket = "lab03"
    key    = "terraform_k8s.tfstate"
    region = "ru-1"

    skip_credentials_validation = true
    skip_metadata_api_check     = true
    skip_region_validation      = true
    skip_requesting_account_id  = true
    use_path_style              = true
  }
}

provider "twc" {
  token = var.twc_token
}

resource "twc_k8s_cluster" "main" {
  name        = var.cluster_name
  description = var.cluster_description

  high_availability = var.high_availability
  version           = var.kubernetes_version
  network_driver    = var.network_driver
  ingress           = var.ingress_enabled

  preset_id = var.master_preset_id
}

resource "twc_k8s_node_group" "main" {
  cluster_id = twc_k8s_cluster.main.id
  name       = var.node_group_name
  node_count = var.node_count

  is_autohealing   = var.node_autohealing
  is_autoscaling   = var.node_autoscaling
  public_ip_enabled = var.node_public_ip_enabled

  min_size = var.node_autoscaling ? var.node_min_size : null
  max_size = var.node_autoscaling ? var.node_max_size : null

  preset_id = var.worker_preset_id
}
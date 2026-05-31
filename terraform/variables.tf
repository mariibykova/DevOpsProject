variable "twc_token" {
  description = "API token Timeweb Cloud (передавайте через переменную окружения TF_VAR_twc_token)."
  type        = string
  sensitive   = true
}

variable "s3_access_key" {
  description = "S3 access key for Terraform backend."
  type        = string
  sensitive   = true
}

variable "s3_secret_key" {
  description = "S3 secret key for Terraform backend."
  type        = string
  sensitive   = true
}

variable "cluster_name" {
  description = "Имя Kubernetes кластера Timeweb Cloud."
  type        = string
  default     = "k8s-lab03"
}

variable "cluster_description" {
  description = "Описание Kubernetes кластера."
  type        = string
  default     = "Managed Kubernetes cluster for the lab03"
}

variable "location" {
  description = "Локация kubernetes"
  type        = string
  default     = "ru-1"

  validation {
    condition     = contains(["ru-1", "ru-3", "nl-1"], var.location)
    error_message = "Допустимые значения location: ru-1, ru-3, nl-1."
  }
}

variable "kubernetes_version" {
  description = "Версия Kubernetes Timeweb Cloud"
  type        = string
  default     = "v1.35.3+k0s.0"
}

variable "network_driver" {
  description = "CNI network driver для кластера"
  type        = string
  default     = "calico"
}

variable "high_availability" {
  description = "Включить High Availability для control plane."
  type        = bool
  default     = false
}

variable "ingress_enabled" {
  description = "Создавать встроенный ingress для кластера."
  type        = bool
  default     = false
}

variable "master_preset_id" {
  description = "Preset ID для master node"
  type        = number
  default     = 1999
}

variable "worker_preset_id" {
  description = "Preset ID для worker node group"
  default     = 1993
}

variable "node_group_name" {
  description = "Имя основной worker node group."
  type        = string
  default     = "lab03-workers"
}

variable "node_count" {
  description = "Количество worker-нод в основной группе."
  type        = number
  default     = 1
}

variable "node_autohealing" {
  description = "Автоматически пересоздавать worker-ноды."
  type        = bool
  default     = true
}

variable "node_autoscaling" {
  description = "Включить автоскейлинг для основной группы нод."
  type        = bool
  default     = false
}

variable "node_min_size" {
  description = "Минимальный размер node group при включенном автоскейлинге."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Максимальный размер node group при включенном автоскейлинге."
  type        = number
  default     = 3
}

variable "node_public_ip_enabled" {
  description = "Выдавать worker-нодам публичные IP"
  type        = bool
  default     = true
}
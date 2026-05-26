output "cluster_id" {
  description = "ID Kubernetes кластера в Timeweb Cloud."
  value       = twc_k8s_cluster.main.id
}

output "kubeconfig" {
  description = "Kubeconfig для доступа к кластеру."
  value       = twc_k8s_cluster.main.kubeconfig
  sensitive   = true
}

output "master_preset_id" {
  description = "Preset ID для master-ноды кластера."
  value       = var.master_preset_id
}

output "worker_preset_id" {
  description = "Preset ID для worker-нод."
  value       = var.worker_preset_id
}

output "node_group_id" {
  description = "ID основной worker node group."
  value       = twc_k8s_node_group.main.id
}

output "cluster_status" {
  description = "Текущий статус managed Kubernetes кластера."
  value = twc_k8s_cluster.main.status

}
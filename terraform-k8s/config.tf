//TODO: cleanup & fromatting of terraform K8s files
//TODO: network policy most likely not necessary. check and verify. If not remove related lables from deployment.
//TODO: apply poddisruptionbudget

resource "kubernetes_namespace" "pipeline-namespace" {
    metadata {
        name = "${var.namespace}"
    }
}

resource "kubernetes_config_map" "pipeline-config" {
    metadata {
        name = "pipeline-config"
        namespace = "${var.namespace}"
    }

    depends_on = [
        "kubernetes_namespace.pipeline-namespace"
    ]

    data = {
        FINNHUB_STOCKS_TICKERS = jsonencode(var.finnhub_stocks_tickers)
        FINNHUB_VALIDATE_TICKERS = "1"

        KAFKA_SERVER + "kafka-service.${var.namespace}.svc.cluster.local"
        KAFKA_PORT = "9092"
        KAFKA_TOPIC_NAME = "market"
        KAFKA_MIN_PARTITIONS = "1"

        SPARK_MASTER = "spark://spark-master:7077"
        SPARK_MAX_OFFSET_PER_TRIGGER = "100"
        SPARK_SHUFFLE_PARTITIONS = "2"
        SPARK_DEPRECATED_OFFSETS = "False"
    }
}

resource "kubernetes_network_policy" "pipeline_network"{
    metadata {
        name = "pipeline-network"
        namespace = "${var.namespace}"
    }

    depends_on = [
        "kubernetes_namespace.pipeline-namespace"
    ]

    spec {
        pod_selector {}
        match_labels = {
            "k8s.network/pipeline-network" = "true"
        }
    }

    policy_types = ["Ingress"]

    ingress {
        from {
            pod_select {
                match_labesl = {
                    "k8s.network/pipeline-network" = "true"
                }
            }
        }
    }
}

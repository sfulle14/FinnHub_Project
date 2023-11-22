# FinnHub Project
This will be a project to work on data ingestion and streaming from the Finnhub.io API/websocket. It is also being used as a way to learn how Kafka, Cassandra, Spark, Grafana, and Terraform work and link together.


## Architecture
![finnhub_data_pipeline_diagram](https://user-images.githubusercontent.com/75480707/218998119-12d514ef-8e10-40e7-a638-afaa728e6b4f.png)

The diagram above provides a detailed insight into pipelin;s architecture.

All applications are containerized into **Docker** containters, which are orchestrated by **Kubernetes** - and its infrastructure is managed by **Terraform**

**Data ingestion layer** - a containerized **Python** application called **FinnhubProducer** connects to Finnhub.io websocket. It encodes retrieved messages into Avro format as specified in schemas/trades.avsc file and ingests messages into Kafka broker.

**Message broker layer** - messages from FinnhubProducer are consumed by **Kafka** broker, which is located in kafka-service pod and has **Kafdrop** service as a sidecar ambassador container for Kafka. On a container startup, **kafka-setup-k8s.sh** script runs to create topics. The **Zookeeper** pod is launched before Kafka as it is required for its metadata management.

**Stream processing layer** - a **Spark** Kubernetes cluster based on spark-k8s-operator is deployed using Helm. A **Scala** application called **StreamProcessor** is submitted into Spark cluster manager, that delegates a worker for it. This application connects to Kafka broker to retrieve messages, transform them using Spark Structured Streaming, and loads into Cassandra tables. The first query - that transforms trades into feasible format - runs continuously, whereas the second - with aggregations - has a 5 seconds trigger.

**Serving database layer** - a **Cassandra** database stores & persists data from Spark jobs. Upon launching, the **cassandra-setup.cql** script runs to create keyspace & tables.

**Visualization layer** - **Grafana** connects to Cassandra database using HadesArchitect-Cassandra-Plugin and serves visualized data to users as in example of Finnhub Sample BTC Dashboard. The dashboard is refreshed each 500ms.


## Dashboard

You can access Grafana with a dashboard on localhost:3000 by running the following command:
```
kubectl port-forward -n pipeline service/grafana 3000:3000
```

You can also modify it for your liking from UI - but if you want to save anything, you will need to export json and load it into Docker image.
Remember that if you change namespace name in Terraform variables you need to apply it into that command as well.

## Setup & Deployment

The application is designed to be deployed on a local Minikube cluster. However, the deployment into EKS/GKE/AKS should be quite straight-forward, with tweaking deployment settings for providers, volumes etc.

Running the application requires you to have a Finnhub API token. You can retrieve it once you have created a Finnhub account. To include it in final deployment, insert it into proper fields in terraform-k8s/config.tf, along with Cassandra database username & password of choice. While setting Cassandra credentials remember to verify them with Grafana dashboard settings (the issue is referenced in config.tf file).

There is also an old setup that relies solely on docker-compose. To reach that, navigate to the docker-compose-old branch.

I was running this cluster on Windows with Minikube, Helm, Docker Desktop and Terraform pre-installed. I have utilized local Docker registry to apply custom images into deployment. I was launching it with no vtx enabled, using VirtualBox as VM engine. Below attached are scripts that I was running in Powershell in order to run the cluster as intended:

```
set HTTP_PROXY=http://<proxy hostname:port>
set HTTPS_PROXY=https://<proxy hostname:port>
set NO_PROXY=localhost,127.0.0.1,10.96.0.0/12,192.168.59.0/24,192.168.49.0/24,192.168.39.0/24

minikube start --no-vtx-check --memory 10240 --cpus 6

minikube docker-env
set DOCKER_TLS_VERIFY=”1"
set DOCKER_HOST=”tcp://172.17.0.2:2376"
set DOCKER_CERT_PATH=”/home/user/.minikube/certs”
set MINIKUBE_ACTIVE_DOCKERD=”minikube”

minikube docker-env | Invoke-Expression

docker-compose -f docker-compose-ci.yaml build --no-cache

cd terraform-k8s
terraform apply
```


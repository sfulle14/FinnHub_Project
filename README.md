# FinnHub Project
This will be a project to work on data ingestion and streaming from the Finnhub.io API/websocket.


## Architecture
![finnhub_data_pipeline_diagram](https://user-images.githubusercontent.com/75480707/218998119-12d514ef-8e10-40e7-a638-afaa728e6b4f.png)

The diagram above provides a detailed insight into pipelin;s architecture.

All applications are containerized into **Docker** containters, which are orchestrated by **Kubernetes** - and its infrastructure is managed by **Terraform**

**Data ingestion layer** 

**Message broker layer**

**Stream processing layer**

**Serving database layer**

**Visualization layer**

## Dashboard

You can access Grafana with a dashboard on localhost:3000 by running the following command:
```
kubectl port-forward -n pipeline service/grafana 3000:3000
```


## Setup & Deployment
This is part of everest project to act as istio enabled sample apps is divided into:     
- 'file-server': service for files (audio, video and files) and cpu intensive calculation (fibonacci number)
- 'guide': service for something that consumes memory and storage

## Requirements
- python 3.x     
- grpc     
- python grpc     
```
% pip install grpcio
```
- numpy
```
% pip install numpy
```


## Setup & Run

### Install grpcio
In Subproject 'guide/src':    
- Install grpcio and grpcio-tools
```
% pip install grpcio
```
- Install numpy
```
% pip install numpy
```
### Run Grpc Guide Service
In Subproject 'guide/src':    
- Start the Guide service
```
% python3 ./route_guide_server.py
```

## Deployment on Kubernetes
- The deployment on kubernetes is supported.      
- Build the package using build.sh (if you use your own repo, adjust the script)    
- ```
% ./build.sh
```
- Instantiate the kubernetes resource from the top of the repo directory   
  ```
% kubectl create -f deployment/kubernetes/vm/app/file-server/file-server.yaml
```

wait a little bit and make sure that the pod and the service is running.
```
% kubectl get pods --all-namespaces |grep guide

```

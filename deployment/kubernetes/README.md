# Deploying/Installing Everest Software package on kubernetes

## Assumptions/Requirements
- It is assumed the Kubernetes (docker etc.) is installed.    
- It is assumed that clover project and clovisor are installed.   
- It is assumed you know how kubectl works and how to set the kube config file to point to your kubernetes cluster.    
  

## Setting up
There are currently 2 ways to deploy everest software packages into kubernetes.
### Using Helm (Recommended)
- Make sure that you installed helm client (and server in the kubernetes cluster/target).    
- Make sure that you have kube config file pointing to the kubernetes cluster/target.   
- Use the script everest.sh under subdirectory helm-chart to manage the installation of everest chart.     
- Invoke the script everest.sh with '-h' to get full description how to use it.    
```
./helm-chart/everest.sh -h
```

### Using kubectl
- Make sure that you installed kubectl.   
- Make sure that you have kube config file pointing to the kubernetes cluster/target.   
- Use the script kube_everest.sh.     
- Invoke the script kube_everest.sh with '-h' to get full description how to use it.    
```
./kube_everest.sh -h
```

## Troubleshootings
TBD

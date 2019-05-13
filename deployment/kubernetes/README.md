# Deploying/Installing Everest Software package on kubernetes

## Assumptions/Requirements
- It is assumed the Kubernetes (docker etc.) is installed.    
- It is assumed that clover project and clovisor are installed.   
- It is assumed you know how kubectl works and how to set the kube config file to point to your kubernetes cluster.    
- Check out the repo https://github.com/iharijono/everest.git    
- You must know where your kubernetes cluster is running and use the script 'kube_everest.sh' with the correct '-p' parameter.     
For example if you k8s runs on VM(s):
```
./kube_everest.sh -p vm
```
For example if you k8s runs on GKE:
```
./kube_everest.sh -p gke
```
Run kube_everest.sh with '-h' to see the full description.     
- By default the script 'kube_everest.sh' will install everest software in the namespace 'everest'. To override it, use the parameter '--with-ns <mynamespace>'.
- 
For example if you k8s runs on VM(s) and would like to install everest in the namespace 'default':     
```
./kube_everest.sh --with-ns default
```
For example if you k8s runs on GKE and would like to install everest in the namespace 'myeverest':     
```
./kube_everest.sh -p gke --with-ns myeverest
```

## Setting up Everest
There are currently 2 ways to deploy everest software packages into kubernetes.
### Using Helm (Recommended)
- Make sure that you installed helm client (and server in the kubernetes cluster/target).    
- Make sure that you have kube config file pointing to the kubernetes cluster/target.   
- Use the script kube-everest.sh with the argument '--with-helm'. Otherwise invoke 'kube-everest.sh -h' to see all supported engine '-p' option.    
```
WARNING: You must use the same argument --with-helm to delete or to list your installation.
```     
- To set up everest on k8s running on VM(s):   
```
./kube_everest.sh --with-helm
```    
- To set up everest on k8s running on GKE:     
```
./kube_everest.sh --with-helm -p gke
```    
- To check/get/list the components of installed everest:    
```
./kube_everest.sh --with-helm list
```    
- To remove/delete the installed everest:    
```
./kube_everest.sh --with-helm delete
```    

### Using kubectl
- Make sure that you installed kubectl.   
- Make sure that you have kube config file pointing to the kubernetes cluster/target.   
- Use the script kube_everest.sh without argument (implicitly with the argument '--with-kubectl').    
```
WARNING: You must use the same argument --with-kubectl (or no arguments) to delete or to list your installation.
```  
- To set up everest on k8s running on VM(s):   
```
./kube_everest.sh
```     
- To set up everest on k8s running on GKE:     
```
./kube_everest.sh -p gke
```    
- To check/get/list the components of installed everest:    
```
./kube_everest.sh list
```    
- To remove/delete the installed everest:    
```
./kube_everest.sh delete
```    
- Invoke the script kube_everest.sh with '-h' to get full description how to use it.    
```
./kube_everest.sh -h
```    

## Troubleshootings
TBD

# Project Everest Software package
This contains all software package and documentation for Everest project

## Contents
- Deployment: How to deploy and the requirements to deploy all everest software packages.       
- Development: How to add/modify the source code for all everest software packages.

## Quickstart:       
- Go to tools/create_k8s and refer to README there.      
 
## Requirements for deployment
- Kubernetes Cluster(s): if you have already kubernetes cluster set up and running, you are good to go and proceed to read the README for development or deployment below. if you need to set up kubernetes cluster on your own, there are 2 ways depending on your environment, but in general the first is preferable (see Quickstart above):     
  + on Virtualbox VMs: go to tools/vmcreate_k8s 'directory' and read README there to set up the kubernetes cluster first.   
  On both directory there is a shell script called 'kube.sh', execute it with '-h' to see the usage but basically you need to just execute it without arguments to start creating the cluster of kubernetes.    
  + on baremetal or existing VMs: go to tools/create_k8s and read README there to set up the kubernetes cluster first.   
- kubectl
- Helm (optional)
- Clovisor, Istio

## Requirements for development
- read README.md under components/collectors/collector_ui subdirectory.    

## Setting up for deployment
See README.md under 'deployment' subdirectory      

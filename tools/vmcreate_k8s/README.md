# How to create kubernetes cluster on Virtualbox VMs
This contains explanation how to set up a kubernetes cluster on virtualbox VMs

## Contents
- Requirements
- How to start/create
- How to clean up
- Troubleshooting

## Requirements
- Virtualbox
- Vagrant

## How to start/create
1. Clone the project everest (URL is temp!!!) to your local machine which has vagrant and virtualbox installed.   
`% git clone https://github.com/iharijono/everest.git`
2. go to the directory 'everest/tools/vmcreate_k8s'.   
`% cd everest/tools/vmcreate_k8s`
3. execute 'kube.sh'. Optional you can configure your kubernetes cluster before executing it by setting environment variables:
   - NODES: setting how many nodes of kubernetes cluster you will instantiate, default is 3.
   For example:   
   `% ./kube.sh`
   will create kubernetes cluster with 3 nodes.
   `% NODES=5 ./kube.sh`
   will create kubernetes cluster with 5 nodes.


## How to clean up
1. execute 'kube.sh -d'. WARNING: if you create kubernetes with NODES environment variabler set (see above), you MUST use the same environment variable again!!!, for example (corresponding to above):   
   `% ./kube.sh -d`
   will delete kubernetes cluster with 3 nodes.
   `% NODES=5 ./kube.sh -d`
   will delete kubernetes cluster with 5 nodes.

## Troubleshooting
TBD

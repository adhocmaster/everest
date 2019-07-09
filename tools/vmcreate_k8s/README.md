# How to create kubernetes cluster (optionally with everest related project) on Virtualbox VMs
This contains explanation how to set up a kubernetes cluster on virtualbox VMs

## Contents
- Requirements
- How to start/create
- How to clean up
- Troubleshooting

## Requirements
- Virtualbox
- Vagrant
- add some ip and host mapping into your machine. In linux, it is /etc/hosts. the hostname 'master' is mandatory, the rest is optional.
```
192.168.26.10 master    
192.168.26.11 node-0      
192.168.26.12 node-1       
```      
- Upgrade your bash version to 4.x.x if you are on MacOS. It is needed for the script tstdi.sh to check actions on K8s.   
```
% brew install bash
```


## How to start/create
1. Clone the project everest (URL is temp!!!) to your local machine which has vagrant and virtualbox installed.     
```% git clone https://github.com/iharijono/everest.git```     
2. go to the directory 'everest/tools/vmcreate_k8s'.      
```% cd everest/tools/vmcreate_k8s```          
3. execute 'kube.sh'. Optionally you can configure your kubernetes cluster before executing it by setting environment variables:    
   - NODES: setting how many nodes of kubernetes cluster you will instantiate, default is 3.     
   - NUM_CPU: setting how many cpu for the nodes of kubernetes cluster you will instantiate, default is 2.     
   - NUM_MEM: setting how many multiple of 2048 MB of the memory for the nodes of kubernetes cluster you will instantiate, default is 1 (meaning 2GB). setting to 2 means 4G.     
     NOTES:      
     - Due to different capacity of the host (PC, VM, laptop etc), there is differences in setting these number, please experiment with these number and report back.     
     - On laptop, usually the default parameter will suffice.    
     - On big machines, the number of memory of the slaves is set 2x than the master.     

   For example:       
   ```% ./kube.sh```         
   will create kubernetes cluster with 3 nodes. There is always 1 Master and the rest are slaves. In addition to that, it will jump start all everest related artifacts.    
   ```% NODES=5 ./kube.sh```           
   will create kubernetes cluster with 5 nodes. In addition to that, it will jump start all everest related artifacts.   
   ```% NUM_CPU=5 ./kube.sh```            
   will create kubernetes cluster with 3 nodes. The master and slaves will have 5 CPUs and 2 GB memory. In addition to that, it will jump start all everest related artifacts.   
   ```% NUM_MEM=4 ./kube.sh```              
   will create kubernetes cluster with 3 nodes. The master and slaves will have 2 CPUs and Master with 8 GB and slave with 16 GB memory each. In addition to that, it will jump start all everest related artifacts.       
   ```% NUM_CPU=5 NUM_MEM=4 NODES=5 ./kube.sh```            
   will create kubernetes cluster with 5 nodes. The master and slaves will have 5 CPUs and Master with 8 GB and slave with 16 GB memory each. In addition to that, it will jump start all everest related artifacts.       
   if you want to delete or initialize ONLY everest project, use '-i':     
   ```% ./kube.sh -d -i ```  
   it will uninstall everest and everest related artifacts, leave the k8s cluster intact     
    ```% ./kube.sh -i ```  
   it will install everest and everest related artifacts, without installing kubernetes
    ```% ./kube.sh -k ```  
   it will install kubernetes cluster + istio only      

4. After ./kube.sh is finished:     
   - Connect your terminal to use the newly created kubernetes cluster by:
    ```% source ~/kubeconfig.sh```

5. Check everything is up:
    -  % kubectl get pods -n istio-system      
   ```      NAME                                      READY   STATUS             RESTARTS   AGE
            grafana-67c69bb567-46zhj                  1/1     Running            0          38h
            istio-citadel-67697b6697-wmg2s            1/1     Running            0          38h
            istio-cleanup-secrets-1.1.6-f27rf         0/1     Completed          0          38h
            istio-egressgateway-7dbbb87698-hxpxr      1/1     Running            0          38h
            istio-galley-7474d97954-qxx2x             1/1     Running            0          21h
            istio-grafana-post-install-1.1.6-565b2    0/1     Completed          1          38h
            istio-ingressgateway-565b894b5f-h8c58     1/1     Running            0          38h
            istio-pilot-6dd5b8f74c-rxc4h              1/2     Running   274        38h
            istio-policy-7f8bb87857-clqbk             2/2     Running            4          38h
            istio-security-post-install-1.1.6-4s9p7   0/1     Completed          1          38h
            istio-sidecar-injector-fd5875568-h4v62    1/1     Running            6          38h
            istio-telemetry-8759dc6b7-85pjl           2/2     Running            3          38h
            istio-tracing-5d8f57c8ff-8tpcm            1/1     Running            0          38h
            kiali-d4d886dd7-5gvrm                     1/1     Running            0          38h
            prometheus-d8d46c5b5-trdnp                1/1     Running            0          38h    
    ```       
    -   % kubectl get pods -n everest-app     
    ```
            NAME                                READY   STATUS    RESTARTS   AGE
            fibo-v1-7fb5c5cd4c-7h9gp            2/2     Running   1          28h
            fibo-v1-7fb5c5cd4c-cc8nh            2/2     Running   1          28h
            fs-standalone-v1-74d7477459-4s9vd   2/2     Running   1          28h
            fs-v1-6bd5ccf774-8h9zz              2/2     Running   2          28h
            fs-v1-6bd5ccf774-wmrx9              2/2     Running   2          28h
            guide-v1-f689f77b7-nbbw5            2/2     Running   2          28h
            guide-v1-f689f77b7-npwjt            2/2     Running   1          28h   
    ```      
    -   % kubectl get pods -n everest    
    ```
            NAME                                      READY   STATUS    RESTARTS   AGE
            collector-ui-deployment-bbb56cfc7-9zcq2   1/1     Running   0          28h
            grafana-deployment-766b89f9d-rn9qh        1/1     Running   1          28h
            mongo-controller-76hkk                    1/1     Running   6          28h
    ```   
    -   % kubectl get pods -n kafka      
    ```
            NAME      READY   STATUS    RESTARTS   AGE
            kafka-0   1/1     Running   2          26h
            kafka-1   1/1     Running   2          26h
            kafka-2   1/1     Running   2          26h
            zoo-0     1/1     Running   0          26h
            zoo-1     1/1     Running   0          26h
            zoo-2     1/1     Running   0          26h      
    ```           
    everything must show 'Running'
6. If you change your terminal, you need to resource kubeconfig:
    - ```% source ~/kubeconfig.sh```         
7. To test if all is running correctly, use smoke.sh under everest/components/clients:
    - ```% cd everest/components/clients```            
    - ```% ./smoke.sh```           
    everything must return 'SUCCESS', example:     
    ``` 
        % ./smoke.sh
        Testing: FS, Fibo REST API
        'http://master:31380/fibo?n=3' --> SUCCESS
        'http://master:31380/fibo_remote?n=3' --> SUCCESS
        'http://master:31380/file?name=everest.ppt' --> SUCCESS
        'http://master:31380/video?name=small.mp4' --> SUCCESS


        Testing: Remote Guide Grpc via REST
        'http://master:31380/guide_remote?goal=1' --> SUCCESS
        'http://master:31380/guide_fibo?goal=1' --> SUCCESS
        'http://master:31380/guide_remote?goal=2' --> SUCCESS
    ```

## How to clean up
1. execute 'kube.sh -d'. WARNING: if you create kubernetes with NODES environment variabler set (see above), you MUST use the same environment variable again!!!, for example (corresponding to above):   
   `% ./kube.sh -d`    
   will delete kubernetes cluster with 3 nodes.    
   `% NODES=5 ./kube.sh -d`    
   will delete kubernetes cluster with 5 nodes.    

## Troubleshooting
TBD

This is part of everest project to act as istio enabled sample apps is divided into:     
- 'file-server': service for files (audio, video and files)
- TBD

## Requirements
Nodejs version 8.+

## Available Scripts

In each sub project directory, you can run:

### `npm install` and then `npm start`
Subproject 'file-server':
Runs the app in the development mode.<br>
Open [http://localhost:9000](http://localhost:9000) to view it in the browser.


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
% kubectl get pods --all-namespaces |grep file

```

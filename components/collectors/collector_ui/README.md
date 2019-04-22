This project is divided into 2 subprojects: 'backend' and 'ui'

The 'ui' subproject  was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).
and the 'backend' subproject was bootstrapped with 'yarn init'

## Requirements
Nodejs version 8.+
react
React-bootstrap
etc...
See 'package.json' in each subproject directories.

## Available Scripts

In each sub project directory, you can run:

### `npm start`
Subproject 'ui':
Runs the app in the development mode.<br>
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.<br>
You will also see any lint errors in the console.

Subproject 'backend':
Runs the server waiting for REST connection at port number 8888

### `npm test`

Launches the test runner in the interactive watch mode.<br>
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder of the subproject 'ui' only.<br>
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br>
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (Webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).


## Deployment
There are 2 artifacts to deploy 'backend' and 'ui' into a kubernetes cluster. The difference is only whether you are using a 'LoadBalancer' or a 'NodePort' type of kubernetes services.

Copy one of the artifacts below according to where you run kubernetes cluster and store it to a file (let's say 'collector-ui.yaml')

Create a kubernetes resource with the file:
```
% kubectl create -f collector-ui.yaml
```
wait a little bit and make sure that the pod and the service is running.
```
% kubectl get pods --all-namespaces |grep ui
clover-system          collector-ui-deployment-b9c7499cf-f7trg                          1/1       Running   0          3h
```
```
% kubectl get svc --all-namespaces |grep web
clover-system          collector-web              LoadBalancer   10.47.252.66    35.238.226.128   8888:31266/TCP                                                                                              3h

```
If you use kubernetes on VM, your output will look slightly different.

### Kubernetes on GKE

```
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: clover-system
  name: collector-ui-deployment
  labels:
    app: collector-ui
spec:
  replicas: 1
  selector:
    matchLabels:
      app: collector-ui
  template:
    metadata:
      labels:
        app: collector-ui
    spec:
      containers:
      - name: collector-ui
        # This is using a public image which I made
        image: iharijono/collector-ui:latest
        imagePullPolicy: Always
        resources:
        # keep request = limit to keep this container in guaranteed class
          limits:
            cpu: 250m
            memory: 250Mi
          requests:
            cpu: 250m
            memory: 250Mi
        ports:
          - containerPort: 8888
            name: http
        env:
          # This is going to refetch the data every 1 minute
          - name: CCOLLECTOR_POLL_INTERVAL
            value: "60000"
          - name: CCOLLECTOR_CLOVER_JAEGER_HOST
            value: "jaeger-query.istio-system"
          - name: CCOLLECTOR_CLOVER_JAEGER_PORT
            value: "16686"
          - name: CCOLLECTOR_CLOVISOR_JAEGER_HOST
            value: "jaeger-query.clover-system"
          - name: CCOLLECTOR_CLOVISOR_JAEGER_PORT
            value: "80"
          - name: CCOLLECTOR_CLOVER_PROM_ID
            value: "clover-prom"
          - name: CCOLLECTOR_CLOVER_PROM_HOST
            value: "prometheus.istio-system"
          - name: CCOLLECTOR_CLOVER_PROM_PORT
            value: "9090"
---
apiVersion: v1
kind: Service
metadata:
  namespace: clover-system
  name: collector-web
spec:
  ports:
  - port: 8888
  type: LoadBalancer
  selector:
    app: collector-ui

```

### Kubernetes on VM

```

apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: clover-system
  name: collector-ui-deployment
  labels:
    app: collector-ui
spec:
  replicas: 1
  selector:
    matchLabels:
      app: collector-ui
  template:
    metadata:
      labels:
        app: collector-ui
    spec:
      containers:
      - name: collector-ui
        # This is using a public image which I made
        image: iharijono/collector-ui:latest
        imagePullPolicy: Always
        resources:
        # keep request = limit to keep this container in guaranteed class
          limits:
            cpu: 150m
            memory: 150Mi
          requests:
            cpu: 150m
            memory: 150Mi
        ports:
          - containerPort: 8888
            name: http
        env:
          # This is going to refetch the data every 1 minute
          - name: CCOLLECTOR_POLL_INTERVAL
            value: "60000"
          - name: CCOLLECTOR_CLOVER_JAEGER_ID
            value: "clover-jaeger"
          - name: CCOLLECTOR_CLOVER_JAEGER_HOST
            value: "jaeger-query.istio-system"
          - name: CCOLLECTOR_CLOVER_JAEGER_PORT
            value: "16686"
          - name: CCOLLECTOR_CLOVISOR_JAEGER_ID
            value: "clovisor-jaeger"
          - name: CCOLLECTOR_CLOVISOR_JAEGER_HOST
            value: "jaeger-query.clover-system"
          - name: CCOLLECTOR_CLOVISOR_JAEGER_PORT
            value: "80"
          - name: CCOLLECTOR_CLOVER_PROM_ID
            value: "clover-prom"
          - name: CCOLLECTOR_CLOVER_PROM_HOST
            value: "prometheus.istio-system"
          - name: CCOLLECTOR_CLOVER_PROM_PORT
            value: "9090"
---
apiVersion: v1
kind: Service
metadata:
  namespace: clover-system
  name: collector-web
spec:
  ports:
  - port: 8888
  type: NodePort 
  selector:
    app: collector-ui
```

## Running (Finding out the URL for the WEB UI)
After you successfully deploy the artifact, you need to find out the IP address that has been assigned to the service (for Kubernetes on GKE) or the port number (for Kubernetes on VMs), in order to see the Web UI.
If you are using Kubernetes on GKE:
```
% kubectl get svc --all-namespaces |grep web
clover-system          collector-web              LoadBalancer   10.47.252.66    35.238.226.128   8888:31266/TCP                                                                                              3h

```
The IP address is 35.238.226.128, so the Web UI URL is http://35.238.226.128:8888
If you are using Kubernetes on VMs:
```
% kubectl get svc --all-namespaces |grep web                                      
NAME             CLUSTER_IP       EXTERNAL_IP   PORT(S)                      AGE
collector-web    172.30.89.219    <nodes>       8888:30036/TCP               2m

```
The port address is 30036, so the Web UI URL is http://<NODE>:30036

Open a web browser and place the URL in the address field.

## Testing
The backend for Web UI will continously gather traces data. But if there is no traffic coming into the (micro)-services, then there won't be any meaningful trace data. To generate traffic, find the IP address of the istio-ingressgateway.

```
% get svc --all-namespaces |grep 35.193.129.37
istio-system           istio-ingressgateway       LoadBalancer   10.47.244.92    35.193.129.37    80:31380/TCP,443:31390/TCP,31400:31400/TCP,15011:31856/TCP,8060:31882/TCP,15030:32509/TCP,15031:32374/TCP   79d

```
So the IP address is '35.193.129.37'
Generating traffic:
```
curl http://35.193.129.37
```
Wait about a minute and refresh the browser.

## Debugging
To see a healthy 'backend' and 'web ui' containers:
- Find the namespace and the name of pod where the UI is running:
  
```
% kubectl get pods --all-namespaces |grep ui
clover-system          collector-ui-deployment-b9c7499cf-f7trg                          1/1       Running   0          3h

```
- Trace the log using the namespace and the name of the pod
    
```
% kubectl logs -f -n clover-system collector-ui-deployment-b9c7499cf-f7trg
  
```
You will see following output every seconds:

```
*************** Capture Tracing/Monitoring Data, Date: Tue Mar 26 2019 04:42:18 GMT+0000 (Coordinated Universal Time)
*************** Capture Tracing/Monitoring Data, Date: Tue Mar 26 2019 04:43:18 GMT+0000 (Coordinated Universal Time)
......
......
......
```
Any other output indicates that the pod is NOT functioning well.


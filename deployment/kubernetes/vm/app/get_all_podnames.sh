kubectl get pods -n everest-app -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}'

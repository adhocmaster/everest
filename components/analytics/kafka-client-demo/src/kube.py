# Copyright 2016 The Kubernetes Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# WARNING:
#    - the pod running this program must have the correct service account with the right to list the resources
#
#
from __future__ import print_function
from kubernetes import client, config
from kubernetes.client.rest import ApiException
from pprint import pprint


class EverestK8s():
    def __init__(self, incluster=True):
        print("Starting Kubernetes Client with incluster={}".format(incluster))
        self.k8s_v1 = None
        self.k8s_betav1 = None
        try:
            if incluster is True:
                config.load_incluster_config()
            else:
                config.load_kube_config()
            self.k8s_v1 = client.CoreV1Api()
            self.k8s_betav1 = client.ApiextensionsV1beta1Api()
            self.k8s_custom_obj = client.CustomObjectsApi()

        except config.config_exception.ConfigException as cex:
            print("*** ERROR *** can not connect to Kubernetes Cluster msg = {}".format(cex))
 
    def list_pod(self):
        if self.k8s_v1 is None:
            print("*** ERROR *** Did you already connect to Kubernetes Cluster???")
            return
        try:
            ret = self.k8s_v1.list_pod_for_all_namespaces(watch=False)
            pprint(ret)
            # for i in ret.items:
            #     print("%s\t%s\t%s" %
            #         (i.status.pod_ip, i.metadata.namespace, i.metadata.name))
        except ApiException as e:
            print("Exception when calling CoreV1Api->list_pod_for_all_namespaces: %s\n" % e)

    def get_resources(self):
        if self.k8s_v1 is None:
            print("*** ERROR *** Did you already connect to Kubernetes Cluster???")
            return
        try:
            ret = self.k8s_v1.get_api_resources()
            #pprint(ret)
            for i in ret.resources:
                print("%s\t%s" %
                    (i.kind, i.name))

        except ApiException as e:
            print("Exception when calling CoreV1Api->get_api_resources: %s\n" % e)

    def my_test(self):
        if self.k8s_betav1 is None:
            print("*** ERROR *** Did you already connect to Kubernetes Cluster Beta???")
            return
        try:       
            # current_crds = [x['spec']['names']['kind'].lower() for x in self.k8s_betav1.list_custom_resource_definition().to_dict()['items']]
            # for crd in current_crds:
            #     print(crd)
            # api_response = self.k8s_betav1.list_custom_resource_definition(pretty=pretty, _continue=_continue, field_selector=field_selector, label_selector=label_selector, limit=limit, resource_version=resource_version, timeout_seconds=timeout_seconds, watch=watch)
            api_response = self.k8s_betav1.list_custom_resource_definition(watch=False)
            #pprint(api_response)
            for i in api_response.items:
                if i.spec.names.kind.lower() in ['virtualservice', 'destinationrule']:
                    print("------------------{}------------------".format(i.spec.names.kind.upper()))
                    #pprint(i.spec)
                    watch = False
                    group = i.spec.group
                    version = i.spec.version
                    plural = i.spec.names.plural
                    pretty = 'true'
                    obj_response = self.k8s_custom_obj.list_cluster_custom_object(
                            group, version, plural, pretty=pretty, watch=watch)
                    #pprint(obj_response['items'])
                    for c_obj in obj_response['items']:
                        # print("Name: {}, Labels: {}, Namespace: {}".format(
                        #     c_obj['metadata']['name'], c_obj['metadata']['labels'], 
                        #     c_obj['metadata']['namespace']))
                        pprint(c_obj['metadata'])
                    print("----------------------------------------")
 
        except ApiException as e:
            print("Exception when calling ApiextensionsV1beta1Api->list_custom_resource_definition: %s\n" % e)
    
    def my_test1(self):
        if self.k8s_betav1 is None:
            print("*** ERROR *** Did you already connect to Kubernetes Cluster Beta???")
            return
        try:       
            # current_crds = [x['spec']['names']['kind'].lower() for x in self.k8s_betav1.list_custom_resource_definition().to_dict()['items']]
            # for crd in current_crds:
            #     print(crd)
            # api_response = self.k8s_betav1.list_custom_resource_definition(pretty=pretty, _continue=_continue, field_selector=field_selector, label_selector=label_selector, limit=limit, resource_version=resource_version, timeout_seconds=timeout_seconds, watch=watch)
            watch = False

            api_response = self.k8s_custom_obj.list_cluster_custom_object(
                            group, version, plural, pretty=pretty, field_selector=field_selector, 
                            label_selector=label_selector, resource_version=resource_version, 
                            timeout_seconds=timeout_seconds, watch=watch)
            pprint(api_response)
            # for i in api_response.items:
            #     if i.spec.names.kind.lower() in ['virtualservice', 'destinationrule']:
            #         print("----------------------------------------")
            #         pprint(i.spec)
            #         print("----------------------------------------")
 
        except ApiException as e:
            print("Exception when calling ApiextensionsV1beta1Api->get_api_resources: %s\n" % e)


if __name__ == '__main__':
    def main(outside):
        # Configs can be set in Configuration class directly or using helper
        # utility. If no argument provided, the config will be loaded from
        # default location.
        # or using kubectl proxy --port=8080
        # config.host = "127.0.0.1:8080"
        # only outside pods
        # config.load_kube_config()

        if outside is True:
            everest = EverestK8s(incluster=False)
        else:
            everest = EverestK8s()
        #everest.list_pod()
        #everest.get_resources()
        everest.my_test()

    VERBOSE=False
    OUTSIDE=False

    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("-v", "--verbose", help="print out the logging info, default is {0}".format(VERBOSE), default=VERBOSE, action="store_true")
    parser.add_argument("-o", "--outside", help="run the program outside k8s cluster {0}".format(OUTSIDE), default=OUTSIDE, action="store_true")

    args = parser.parse_args()
    if args.outside is True:
        OUTSIDE = args.outside

    main(OUTSIDE)
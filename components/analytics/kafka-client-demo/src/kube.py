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
import json
import copy
import subprocess
import time

MINUTES=60
CLEANING_ELAPSED_THRESHOLD = 0.5 * MINUTES # seconds
RESET_ELAPSED_THRESHOLD = 10 * MINUTES

from threading import Timer
class RepeatedTimer():
    def __init__(self, interval, function, *args, **kwargs):
        self._timer     = None
        self.function   = function
        self.interval   = interval
        self.args       = args
        self.kwargs     = kwargs
        self.is_running = False
        self.start()

    def _run(self):
        #print("Run Cleaning Task every {} seconds".format(self.interval))
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self._timer = Timer(self.interval, self._run)
            self._timer.start()
            self.is_running = True
            # print("Start Cleaning Task every {} seconds".format(self.interval))

    def stop(self):
        self._timer.cancel()
        self.is_running = False


class EverestK8s():
    def __init__(self, incluster=True, id=''):
        print("Starting Kubernetes Client with incluster={}".format(incluster))
        self.k8s_v1 = None
        self.k8s_betav1 = None
        self.k8s_custom_obj = None
        self._containers = {}
        self.id = id
        self._clean_args = {}
        self._clean_args['id'] = id
        self._clean_args['containers'] = self._containers

        try:
            self.rt = RepeatedTimer(CLEANING_ELAPSED_THRESHOLD/5, self.cleaning, self._clean_args)

            if incluster is True:
                config.load_incluster_config()
            else:
                config.load_kube_config()
            self.k8s_v1 = client.CoreV1Api()
            self.k8s_betav1 = client.ApiextensionsV1beta1Api()
            self.k8s_custom_obj = client.CustomObjectsApi()

        except config.config_exception.ConfigException as cex:
            print("*** ERROR *** can not connect to Kubernetes Cluster msg = {}".format(cex))
        finally:
            #self.rt.stop()
            pass
 
    def cleaning(self, args):
        # print("K8S Start Cleaning self._containers = {} \n containers = {}".format(self._containers, args))
        id = args['id']
        containers = args['containers']
        for key,c_data in containers.items():
            last_ts = c_data[0]
            elapsed_time = time.time() - last_ts
            if elapsed_time > RESET_ELAPSED_THRESHOLD:
                print("{}: Elapsed {} bigger than {}, so reset to normal now".format(key, elapsed_time, RESET_ELAPSED_THRESHOLD))
                containers.pop(key, None)
        
        # print("K8S Done Cleaning ")

    def _check_K8s(self):
        if self.k8s_v1 is None or self.k8s_betav1 is None or self.k8s_custom_obj is None:
            return True
        else:
            return False

    def action(self, **kwargs):
        if self._check_K8s():
            print("*** ERROR *** Did you already connect to Kubernetes Cluster Beta???")
            return
        try:
            # for key, value in kwargs.items(): 
            #     print ("%s == %s" % (key, pprint(value)))
            # pprint(kwargs)
            # pod_name = 'istio-galley-6c466bf5b6-4jvsn'
            # namespace = 'istio-system'
            pod_name = kwargs['data']['podName']
            namespace = kwargs['data']['namespace']
            key = "{}@{}".format(pod_name, namespace)

            action = 'add'
            print("---> Action is executed on {}".format(key))
            if 'action' in kwargs:
                action = kwargs['action']
            if not key in self._containers:
                print("---> Action is executed on {}".format(key))
                self._containers[key] = [time.time()]
            else:
                print("---> Action ALREADY is executed on {}, DO NOTHING".format(key))
                pass


            #
            # This is later TBD TBD TBD
            # 
            # services = self.k8s_v1.list_service_for_all_namespaces(watch=False)
            # for svc in services.items:
            #     if svc.spec.selector:
            #         # convert the selector dict into a  string selector
            #         # for example: {"app": "redis"} => "app=redis"
            #         selector = ''
            #         for k,v in svc.spec.selector.items():
            #             selector += k + '=' + v + ','
            #         selector = selector[:-1]
            #         # print("SELECTOR: " + selector)
            #         # get the pods that match the selector
            #         pods = self.k8s_v1.list_pod_for_all_namespaces(label_selector=selector)
            #         for pod in pods.items:
            #             if pod.metadata.name == pod_name and pod.metadata.namespace == namespace:
            #                 print("ACTION ON ---> Service {} {}@{}".format(svc.metadata.name, pod.metadata.name, pod.metadata.namespace))

        except ApiException as e:
            print("Exception when calling xxx: %s\n" % e)

    def list_pod(self):
        if self._check_K8s():
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
        if self._check_K8s():
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

    def my_test(self, namespace=None, vservice=None, label='normal'):
        if self._check_K8s():
            print("*** ERROR *** Did you already connect to Kubernetes Cluster Beta???")
            return
        if vservice is None:
            print("*** ERROR *** virtual service can not be None")
            return
        
        try:       
            api_response = self.k8s_betav1.list_custom_resource_definition(watch=False)
            # name = 'VirtualService'
            # api_response = self.k8s_betav1.read_custom_resource_definition(
            #     name, pretty='pretty', exact=True, export=True
            # )
            # pprint(api_response)
            for i in api_response.items:
                if i.spec.names.kind.lower() in ['virtualservice']:
                    print("------------------{}------------------".format(i.spec.names.kind.upper()))
                    # pprint(i.spec)
                    watch = False
                    group = i.spec.group
                    version = i.spec.version
                    plural = i.spec.names.plural
                    pretty = 'true'
                    if namespace is None:
                        # obj_response = self.k8s_custom_obj.list_cluster_custom_object(
                        #     group, version, plural, pretty=pretty, watch=watch)
                        obj_response = self.k8s_custom_obj.get_cluster_custom_object(
                            group, version, plural, vservice)
                    else:
                        namespace = namespace
                        # obj_response = self.k8s_custom_obj.list_namespaced_custom_object(
                        #     group, version, namespace, plural, pretty=pretty, watch=watch)
                        obj_response = self.k8s_custom_obj.get_namespaced_custom_object(
                            group, version, namespace, plural, vservice)
                    # pprint(obj_response)
                    # body = {items: [{spec: {http: [{route:}, {route:}, {route:}]}}]}
                    # for c_obj in obj_response['items']:
                    #     print("Name: {}, Route: {}, Namespace: {}".format(
                    #         c_obj['metadata']['name'], c_obj['spec'], 
                    #         c_obj['metadata']['namespace'])
                    #          )
                    _obj_response = copy.deepcopy(obj_response)
                    http = _obj_response['spec']['http']
                    for routes in http:
                        for route in routes['route']:
                            # now set the weight if they need to set busy
                            # otherwise reset
                            # pprint(route)
                            if route['destination']['subset'] == 'v1':
                                if label == 'normal':
                                    print("N Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                elif label == 'busy':
                                    print("B Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                else:
                                    print("I Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                            elif route['destination']['subset'] == 'v1-idle':
                                if label == 'normal':
                                    print("IN Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                elif label == 'busy':
                                    print("IB Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                else:
                                    print("II Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                            elif route['destination']['subset'] == 'v1-busy':
                                if label == 'normal':
                                    print("BN Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                elif label == 'busy':
                                    print("BB Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                                else:
                                    print("BI Subset: {} Weight {}".format(route['destination']['subset'], route['weight']))
                        print("\n")
                    print("----------------------------------------")
 
        except ApiException as e:
            print("Exception when calling ApiextensionsV1beta1Api->list_custom_resource_definition: %s\n" % e)
    
    def my_patch(self, namespace=''):
        if self._check_K8s():
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
                if i.spec.names.kind.lower() in ['virtualservice']:
                    print("------------------{}------------------".format(i.spec.names.kind.upper()))
                    #pprint(i.spec)
                    watch = False
                    group = i.spec.group
                    version = i.spec.version
                    plural = i.spec.names.plural
                    pretty = 'true'
                    c_obj['metadata']['labels']
                    body = {"metadata" : {
                            "spec" : ""
                        }
                    }
                    if namespace is None:
                        obj_response = self.k8s_custom_obj.patch_cluster_custom_object(
                            group, version, plural, name, body)
                    else:
                        namespace = namespace
                        obj_response = self.k8s_custom_obj.patch_namespaced_custom_object(
                            group, version, namespace, plural, name, body)
                    print("PATCH RESULT ===========>")
                    pprint(obj_response['items'])
                    print("PATCH RESULT <===========")
 
        except ApiException as e:
            print("Exception when calling ApiextensionsV1beta1Api->patch_cluster_custom_object: %s\n" % e)


    #
    # DON'T USE !!! just place holder 
    #
    def my_test1(self):
        if self._check_K8s():
            print("*** ERROR *** Did you already connect to Kubernetes Cluster Beta???")
            return
        try:       
            # current_crds = [x['spec']['names']['kind'].lower() for x in self.k8s_betav1.list_custom_resource_definition().to_dict()['items']]
            # for crd in current_crds:
            #     print(crd)
            # api_response = self.k8s_betav1.list_custom_resource_definition(pretty=pretty, _continue=_continue, field_selector=field_selector, label_selector=label_selector, limit=limit, resource_version=resource_version, timeout_seconds=timeout_seconds, watch=watch)
            watch = False
 
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
        everest.my_test(namespace='everest-app', vservice='fs')

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
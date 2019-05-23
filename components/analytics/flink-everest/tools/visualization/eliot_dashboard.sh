#!/bin/sh
#
# Description   : A script to automatically create dashboard for Eliot data on Grafana
#
# Since         : 11/09/2018
#
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Grafana from outside Kube
#GRAFANA_URL="http://master:30003"
# Grafana from inside Kube
GRAFANA_URL="http://grafana-service.default.svc.cluster.local:3000"
# Grafana on mac
#GRAFANA_URL="http://master:30003"
GRAFANA_DASHBOARD=$GRAFANA_URL/api/dashboards/db
DASHBOARD_JSON=eliot_dashboard.json
OP="create"
ID=""

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "eliot_dashboard.sh [-h] [-l|-d <UID>] [-url grafana_url] [-json eliot_dashboard_json]"
	    echo "create, delete or list dashboard for Eliot data on grafana, default is to create"
	    echo "-url grafana_url, default is $GRAFANA_URL"
	    echo "-json eliot_dashboard_json, default is $DASHBOARD_JSON"

	    exit
	    ;;
	-l)
	    OP="list"
	    shift
	    ;;
	-d)
	    shift
	    ID="$1"
	    OP="delete"
	    shift
	    ;;
	-url)
	    shift
	    GRAFANA_URL="$1"
	    GRAFANA_DASHBOARD=$GRAFANA_URL/api/dashboards/db
	    shift
	    ;;
	-json)
	    shift
	    DASHBOARD_JSON="$1"
	    shift
	    ;;
    esac
done

case "$OP" in
    list)
        echo "curl -i -u admin:admin $GRAFANA_URL/api/search?folderIds=0&query=&starred=false"
        curl -i -u admin:admin $GRAFANA_URL/api/search?folderIds=0&query=&starred=false
        echo
        ;;
    delete)
        echo "curl -i -X DELETE -u admin:admin $GRAFANA_URL/api/dashboards/uid/$ID --header \"Content-Type: application/json\""
        curl -i -X DELETE -u admin:admin $GRAFANA_URL/api/dashboards/uid/$ID --header "Content-Type: application/json"
        ;;
    *)
        echo "curl -i -u -X POST admin:admin $GRAFANA_DASHBOARD -d @$DASHBOARD_JSON --header \"Content-Type: application/json\""
        curl -i -u admin:admin $GRAFANA_DASHBOARD -d @$DASHBOARD_JSON --header "Content-Type: application/json"
        ;;
esac
echo

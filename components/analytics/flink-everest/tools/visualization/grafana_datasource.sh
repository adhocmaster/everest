#!/bin/sh
#
# Description   : A script to automatically create datasource for InfluxDB on Grafana
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
GRAFANA_DATA_SOURCE=$GRAFANA_URL/api/datasources
DATA_SOURCE_JSON=influxdb_datasource.json
OP="create"
ID=""

for arg in "$@"
do
    case "$1" in
	-h)
	    echo "db_influxdb.sh [-h] [-l|-d <ID>] [-url grafana_url] [-json datasource_json]"
	    echo "create, delete or list data source on grafana, default is to create"
	    echo "-url grafana_url, default is $GRAFANA_URL"
	    echo "-json datasource_json, default is $DATA_SOURCE_JSON"
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
	    GRAFANA_DATA_SOURCE=$GRAFANA_URL/api/datasources
	    shift
	    ;;
	-json)
	    shift
	    DATA_SOURCE_JSON="$1"
	    shift
	    ;;
    esac
done

#DROP SERIES FROM /.*/

case "$OP" in
    list)
        curl -i -u admin:admin $GRAFANA_DATA_SOURCE --header "Content-Type: application/json"
        ;;
    delete)
        curl -i -X DELETE -u admin:admin $GRAFANA_DATA_SOURCE/$ID --header "Content-Type: application/json"
        ;;
    *)
        curl -i -u admin:admin $GRAFANA_DATA_SOURCE -d @$DATA_SOURCE_JSON --header "Content-Type: application/json"
        ;;
esac

echo

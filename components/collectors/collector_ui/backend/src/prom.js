/**
 * File Name:           prom.js
 * Description:         contains class to manage and get prometheus information
 * 
 * Last Modified:       03/28/2019
 * Requirement:         Prometheus URL access
 *                      axios
 *                        % npm install -S axios
 *
 *
 **/

"use strict"

const axios = require('axios')

class Prom {
    // all class level constants
    static get PROM_HOST() {
        return "35.224.142.142"
    }
    static get PROM_PORT() {
        return 9090
    }
    static get PROM_ID() {
        return "prometheus-default"
    }
    static get PROMETHEUS_ISTIO_TARGETS() {
        return ["envoy",
        "galley",
        "istio-mesh",
        "istio-policy",
        "istio-telemetry",
        "kubernetes-apiservers",
        "kubernetes-cadvisor",
        "kubernetes-nodes",
        "kubernetes-pods",
        "kubernetes-service-endpoints",
        "mixer",
        "pilot"]
    }
    static get prom_services() {
        return ["proxy_access_control", "clover_server1", "clover_server2", "clover_server3"]
    }
    static get prom_prefixes() {
        return ["envoy_", "mixer_", "pilot_"]
    }
    static get prom_suffixes() {
        return ["_default_svc_cluster_local_upstream_rq_2xx", "_default_svc_cluster_local_upstream_cx_active"]
    }

    constructor(host=Prom.PROM_HOST, port=Prom.PROM_PORT, id=Prom.PROM_ID) {
      this._verbose = false
      this._id = id
      this._host = host
      this._port = port
      this._url_all_metrics = 'http://' + this._host + ':' + this._port + '/api/v1/label/__name__/values'
      this._url0 = 'http://' + this._host + ':' + this._port + '/api/v1/targets'
      this._url1 = 'http://' + this._host + ':' + this._port + '/api/v1/query?query='
      this._metrics = []
    }
    get id() {
      return this._id
    }
    get host() {
        return this._host
    }
    get port() {
        return this._port
    }
    get url0() {
        return this._url0
    }
    get url1() {
        return this._url1
    }
    get metrics() {
        return this._metrics
    }
    set verbose(on_or_off) {
        this._verbose = on_or_off
    }

    async _collect0() {
        let title = "Prom ID '" + this._id + "' _collect0"
        let res = false
		if(this._verbose)
            console.log(title + " URL -> " + this._url_all_metrics)
        try {
            const response = await axios.get(this._url_all_metrics)
            const data = response.data
            if(this._verbose)
                console.log(data)
            if(data.status == "success") {
                let metric_names = data.data
                let all_jobs = []
                for(let metric_name of metric_names) {
                    if(metric_name.indexOf(Prom.prom_prefixes[0]) != 0)
                        continue
                    //console.log("Metric Name: " + metric_name)
                    //all_jobs.push(this._collect1(metric_name))
                    await this._collect1(metric_name)
                }
                //await Promise.all(all_jobs)
                res = true
            } else {
                console.log(`WARNING: http request to ${this._url_all_metrics} status errors`)
                this._services = []
                res = false
            }
            return data
        } catch (error) {
            console.log(`ERROR: http request to ${this._url_all_metrics} return errors: ${error}`)
            this._services = []
            res = false
        }
        return res
    }
    
    async _collect1(metric_name) {
        let res = false
        let title = "Prom ID '" + this._id + "' __collect1"
        let url = this._url1 + metric_name
        if(this._verbose == true)
            console.log(title + " URL -> " + url)

        try {
            const response = await axios.get(url)
            const data = response.data
            if(data.status == "success") {
                if(this._verbose)
                    console.log(JSON.stringify(data.data, null, 2))
 
                //console.log(`Metric=${data.data.result[0].metric.__name__} Job=${data.data.result[0].metric.job}
                //Value=${data.data.result[0].value}`)
                this._metrics.push(data)
                res = true
            } else {
                console.log(`WARNING: http request to ${url} return errors`)
                this._traces = {}
                res = false
            }
        } catch (error) {
            console.log(`ERROR: http request to ${url} return errors`)
            console.log("msg: " + error)
            this._traces = {}
            res = false
        }
        return res
    }
    
    async collect() {
        await this._collect0()
    }
}

module.exports = Prom
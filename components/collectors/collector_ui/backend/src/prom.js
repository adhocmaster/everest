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
        return "prometheus.istio-system"
    }
    static get PROM_PORT() {
        return 9090
    }
    static get PROM_ID() {
        return "prometheus-default"
    }
    static get PROM_CPU() {
        return ["container_cpu_usage_seconds_total", "container_cpu_load_average_10s", ]
    }
    static get PROM_MEM() {
        return ["container_memory_working_set_bytes"]
    }
    static get PROM_NET() {
        return ["container_network_transmit_bytes_total", "container_network_receive_bytes_total"]
    }
    static get PROM_CLUSTER() {
        return [""]
    }
    static get EXCLUDED_NS() {
        return ["kube-system", "istio-system", "everest", "kafka"]
    }
    static get PROM_JSON_KEY() {
        return 'collector-data'
    }
 
    constructor(host=Prom.PROM_HOST, port=Prom.PROM_PORT, id=Prom.PROM_ID) {
      this._verbose = false
      this._id = id
      this._host = host
      this._port = port
      this._url_query = 'http://' + this._host + ':' + this._port + '/api/v1/query?query='
      this._kafka = ''
      this._metrics = {'cluster_id': 'mycluster_id', 'cpuData': [], 'memData': [], 'ts': 0}
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
    get url_query() {
        return this._url_query
    }
    get metrics() {
        return this._metrics
    }
    set verbose(on_or_off) {
        this._verbose = on_or_off
    }
    set set_kafka(kafka) {
        this._kafka = kafka
    }

    async _collect_mem() {
        let title = "Prom ID '" + this._id + "' _collect_mem"
        let json_data = {} 
        let res = true

        for(let element of Prom.PROM_MEM) {
            const p_query = this._url_query + element
            if(this._verbose)
                console.log(title + " URL -> " + p_query)
            const response = await axios.get(p_query)
    
            const status = response.status
            if(status == 200) {
                const results = response.data.data.result
                let ts_milliseconds = (new Date).getTime()
                this._metrics.cluster_id = 'mycluster_id_memX'
                this._metrics.ts = ts_milliseconds
                for(let result of results) {
                    let metric = result.metric
                    let values = result.value
                    if(!Prom.EXCLUDED_NS.includes(metric.namespace) && 'namespace' in metric) {
                        //if(this._verbose) {
                            // console.log('\n')
                            // console.log(JSON.stringify(response.data.data, null, 2))
                            // console.log(`POD ---> ${metric.pod}`)
                            // console.log(`POD NAME ---> ${metric.pod_name}`)
                            // console.log(`NAMESPACE ---> ${metric.namespace}`)
                            // console.log(`VALUE MEM ---> ${values}`)
                            //console.log(`PERCENT ---> ${values[1]}`)
                        //}
                        let memData = {'containerName': metric.pod, 'value': values[0], 'percentage': values[1],
                        'podName': metric.pod_name, 'namespace': metric.namespace}
                        this._metrics.memData.push(memData)
                    }
                }
            } else {
                console.log(`WARNING: http rest API request to ${p_query} status NOT 200 but ${status}`)
                this._services = []
                res = false
            }
        }
        return res
    }

    async _collect_net() {
        let title = "Prom ID '" + this._id + "' _collect_net"
        let json_data = {} 
        let res = true
        for(let element of Prom.PROM_NET) {
            const p_query = this._url_query + element
            if(this._verbose)
                console.log(title + " URL -> " + p_query)
            const response = await axios.get(p_query)
    
            const status = response.status
            if(status == 200) {
                const results = response.data.data.result
                let ts_milliseconds = (new Date).getTime()
                this._metrics.cluster_id = 'mycluster_id_memX'
                this._metrics.ts = ts_milliseconds
                for(let result of results) {
                    let metric = result.metric
                    let values = result.value
                    if(!Prom.EXCLUDED_NS.includes(metric.namespace) && 'namespace' in metric) {
                        if(this._verbose) {
                            // console.log('\n')
                            // console.log(JSON.stringify(response.data.data, null, 2))
                            console.log(`POD ---> ${metric.pod}`)
                            // console.log(`POD NAME ---> ${metric.pod_name}`)
                            // console.log(`NAMESPACE ---> ${metric.namespace}`)
                            console.log(`VALUE NET ---> ${values}`)
                            //console.log(`PERCENT ---> ${values[1]}`)
                        }
                        // let memData = {'containerName': metric.pod, 'value': values[0], 'percentage': values[1],
                        // 'podName': metric.pod_name, 'namespace': metric.namespace}
                        // this._metrics.memData.push(memData)
                    }
                }
            } else {
                console.log(`WARNING: http rest API request to ${p_query} status NOT 200 but ${status}`)
                this._services = []
                res = false
            }
        }
        return res
 
    }

    async _collect_cpu() {
        let title = "Prom ID '" + this._id + "' _collect_cpu"
        let json_data = {} 

        for(let element of Prom.PROM_CPU) {
            const p_query = this._url_query + element
            if(this._verbose)
                console.log(title + " URL -> " + p_query)
            const response = await axios.get(p_query)
    
            const status = response.status
            if(status == 200) {
                const results = response.data.data.result
                let ts_milliseconds = (new Date).getTime()
                this._metrics.cluster_id = 'mycluster_id_X'
                this._metrics.ts = ts_milliseconds
                for(let result of results) {
                    let metric = result.metric
                    let values = result.value
                    if(!Prom.EXCLUDED_NS.includes(metric.namespace) && 'namespace' in metric) {
                        //if(this._verbose) {
                            // console.log('\n')
                            // console.log(JSON.stringify(response.data.data, null, 2))
                            // console.log(`POD ---> ${metric.pod}`)
                            // console.log(`POD NAME ---> ${metric.pod_name}`)
                            // console.log(`NAMESPACE ---> ${metric.namespace}`)
                            //console.log(`VALUE CPU ---> ${values}`)
                            //console.log(`PERCENT ---> ${values[1]}`)
                        //}
                        let cpuData = {'containerName': metric.pod, 'value': values[0], 'percentage': values[1],
                        'podName': metric.pod_name, 'namespace': metric.namespace}
                        this._metrics.cpuData.push(cpuData)
                    }
                }
            } else {
                console.log(`WARNING: http rest API request to ${p_query} status NOT 200 but ${status}`)
                this._services = []
                res = false
            }
        }
    }
    async _collect0() {

        let res = false
        try {
            await this._collect_cpu()
            await this._collect_mem()
            await this._collect_net()
        } catch (error) {
            console.log(`ERROR: http rest API request to ${this._url_query} return errors: ${error}`)
            res = false
        }
        return res
    }
    
    async collect() {
        await this._collect0()
    }
}

module.exports = Prom
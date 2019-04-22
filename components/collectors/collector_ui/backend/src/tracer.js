/**
 * File Name:           tracer.js
 * Description:         contains class to manage and get jaeger information
 * 
 * Last Modified:       03/28/2019
 * Requirement:         jaeger URL access
 *                      axios
 *                        % npm install -S axios
 *
 *
 **/

"use strict"

const axios = require('axios')

class Tracer {
    // all class level constants
    static get TRACE_HOST() {
        return "35.238.70.37"
    }
    static get TRACE_PORT() {
        return 80
    }
    static get TRACE_ID() {
        return "jaeger-default"
    }
    static get TRACE_TYPE() {
        return "clover"
    }

    constructor(host=Tracer.TRACE_HOST, port=Tracer.TRACE_PORT, id=Tracer.TRACE_ID, type=Tracer.TRACE_TYPE) {
      this._verbose = false
      this._id = id
      this._host = host
      this._port = port
      this._type = type
      this._services = []
      this._traces = {}
      this._url0 = 'http://' + this._host + ':' + this._port + '/api/services'
      this._url1 = 'http://' + this._host + ':' + this._port + '/api/traces?service='
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
    get traces() {
        return this._traces
    }

    get startCaptureInMsec() {
        return this._capturedInMsec
    }
    set verbose(on_or_off) {
        this._verbose = on_or_off
    }
    set startCaptureInMsec(msec) {
        this._capturedInMsec = msec
    }

    async _collect0() {
        let title = "Trace ID '" + this._id + "' collect0"
        let res = false
        if(this._verbose)
            console.log(title + " URL -> " + this._url0)
        try {
            const response = await axios.get(this._url0)
            const data = response.data
            if(this._verbose)
                console.log(data)
            if(data.errors === null) {
                this._services = data.data
                let all_jobs = []
                for(let service of this._services) {
                    //console.log(this._id + ": SERVICE: " + service)
                    if(service === 'jaeger-query')
                        continue
                    all_jobs.push(this._collect1(service))
                }
                await Promise.all(all_jobs)
                res = true
            } else {
                console.log("WARNING: http request return errors, empty service list")
                this._services = []
                res = false
            }
            return data
        } catch (error) {
            console.log(`ERROR: http request return errors, (maybe empty service list) or ${error}`)
            this._services = []
            res = false
        }
        return res
    }

    async _collect1(service) {
        let title = "Trace ID '" + this._id + "' collect1 for service " + service 
        let res = false
        let aux = "loopback=1h&maxDuration&minDuration&"    
        let end = Date.now()
        let start = end - this._capturedInMsec
        let trace_url = this._url1 + service + "&" + aux + 'start=' + (start * 1000) + '&end=' + (end * 1000)    

        if(this._verbose)
            console.log(title + " URL -> " + trace_url)
        try {
            const response = await axios.get(trace_url)
            const data = response.data
            if(this._verbose)
                console.log(data)
            if(data.errors === null) {
                if(data.data.length > 0) {                    
                    let c_data = data.data;	    
                    if(c_data.length > 0) {
                        
                        c_data.forEach((trace) => {
                            if(this.type === 'clovisor')
                                trace["spans"].map(a => {return a["startTime"]/1000})
                            trace["spans"].sort((a, b) => {
                                return a["startTime"] - b["startTime"]
                            })
                        })
                        this._traces[service] = c_data
                    }
                }
                // console.log("Traces LEN " + this._traces.length)
                // for(let trace of this._traces) {
                //     console.log("spans len " + trace.spans.length)
                //     console.log("processes len " + Object.keys(trace.processes).length)
                //     for(let span of trace.spans) {
                //         console.log("span tags len " + span.tags.length)
                //         for (let tag of span.tags) {
                //             console.log('KEY ' + tag.key + ' VALUE ' + tag.value)
                //         }
                //     }
                // }
                res = true
            } else {
                console.log("WARNING: http request return errors, empty trace list")
                this._traces = {}
                res = false
            }
        } catch (error) {
            console.log(`ERROR: http request return errors, (maybe empty service list) or ${error}`)
            this._traces = {}
            res = false
        }
        return res
    }
    collect() {
        return this._collect0()
    }
}

module.exports = Tracer
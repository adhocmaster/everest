"use strict"

const mongoose = require('mongoose')

const Schema = mongoose.Schema
// this will be our data base's data structure 
const TraceSchema = new Schema(
    {
      traceID: String,
      service: String,
      origin_id: String,
      origin_type: String,
      origin_url: String,
      spans: [{
        spanID: String,
        opName: String,
        startTime: Number,
        duration: Number,
        processId: String,
        tags: [{
            key: String,
            value: String
        }],
        parent: {
            spanID: String
        }        
      }],
      processes: [{
        name: String,
        tags: [{
            key: String,
            value: String
        }]
      }],
      origin: {
        id: String,
        type: String,
        tracer_url: String
      },
      derived: {
          startTime: Number,
          endTime: Number,
          duration: Number,
          app: String,
          statusOp: Boolean,
          target: String
      }
    },
    {timestamps: true }
)

var Trace = mongoose.model("Trace", TraceSchema, 'traces')

class Mongo {
    constructor(route) {
        this._route = route
        this._mongo = null
        if(this._route !== '') {         
            mongoose.connect(
                this._route,
                { useNewUrlParser: true }
            )
            this._mongo = mongoose.connection
            this._mongo.once("open", () => console.log(`connected to the Mongo database at ${this._route}`))
            this._mongo.on("error", console.error.bind(console, `MongoDB connection error to ${this._route}`))
        
        }
    }
 
    get route() {
        return this._route
    }
  
    async find(trace) {
        if(trace == '')
            trace = {}
        let traces = null
        try {
            let traces = await Trace.find(trace)
            //console.log("traces: " + traces)
            return traces
        } catch (err) {
            console.log('Mongo: Error in find, err=' + err)
        }
 
    }

    async update(id, update) {
        let res = null
        try {
            res = await Trace.findOneAndUpdate(id, update)
            return res
        } catch (err) {
            console.log('Mongo: Error in update, err=' + err)
        }
    }

    async delete(id) {
        if(id == '')
            id = {}
        let res = null
        try {
            res = await Trace.deleteMany(id)
        } catch(err) {
            console.log('Mongo: Error in delete, err=' + err)
        }
        return res
    }

    build_docs(_org, _traces) {
        let traceDocs = []

        for(let key in _traces) {
            let traces = _traces[key]
            let sum_status_ok = 0
            //console.log(`==== SERVICE: ${key} LEN ${traces.length}`)
            for(let trace of traces) {
                let _startTime = -1
                let _endTime = -1
                let _app = 'N/A'
                let _stat = false
                let _target = 'N/A'
                let traceDoc = new Trace()
                traceDoc.traceID = trace.traceID
                traceDoc.service = key
        
                //     tracer_url: _org.tracer_url,
                //     type: _org.type
                // }
                traceDoc.processes = []
                traceDoc.spans = []
                //console.log("spans len " + trace.spans.length)
                //console.log("processes len " + Object.keys(trace.processes).length)
                for(let span of trace.spans) {
                    if(_startTime < 0 || span.startTime < _startTime)
                        _startTime = span.startTime
                    if(_endTime < 0 || (_startTime + span.duration) > _endTime)
                        _endTime = _startTime + span.duration
                        
                    let spanDoc = {
                        spanID: span.spanID,
                        opName: span.operationName,
                        startTime: span.startTime,
                        duration: span.duration,
                        processId: span.processId,
                    }
                    //console.log("PARENT LEN " + span.references.length)
                    if(span.references.length > 0) {
                         spanDoc.parent = {spanID: span.references[0].spanID}
                    }
                    spanDoc.tags = []
                    //console.log("span tags len " + span.tags.length)
                    for(let tag of span.tags) {
                        //if(tag.key === 'http.status_code' && tag.value === '200') {
                        if(tag.key === 'http.status_code') {
                            //_stat = true
                            console.log("Status -> " + tag.value)
                            //sum_status_ok++
                        }
                        if(tag.key === 'http.url') {
                            _target = tag.value
                            console.log("Target -> " + _target)
                        }
                        if(tag.key === 'http.ptotocol') {
                            _app = tag.value
                            //console.log("APP -> " + _app)
                        }
                        let tagDoc = {
                            key: tag.key,
                            value: tag.value
                        }
                        spanDoc.tags.push(tagDoc)
                    }
                    traceDoc.spans.push(spanDoc)
                }
                traceDoc.derived.startTime = _startTime
                traceDoc.derived.endTime = _endTime
                traceDoc.derived.duration = _endTime - _startTime
                traceDoc.derived.app = _app
                traceDoc.derived.statusOp = _stat 
                traceDoc.derived.target = _target

                traceDoc.origin_id = _org.id
                traceDoc.origin_url = _org.tracer_url
                traceDoc.origin_type = _org.type

                //console.log("ORG " + JSON.stringify(traceDoc, null, 2))

                
 
                let tab = '\t\t'
                if(_stat)
                    console.log(`\t\tDERIVED: jaeger_id: ${_org.id} 
                        start: ${traceDoc.derived.startTime} 
                        start: ${_startTime} 
                        end: ${_endTime} 
                        duration ${traceDoc.derived.duration} 
                        app: ${_app} 
                        status ${_stat}
                        service ${key}
                        url ${_target}
                        `)
                else
                    console.log(`DERIVED: jaeger_id: ${_org.id} 
                    start: ${traceDoc.derived.startTime} 
                    start: ${_startTime} 
                    end: ${_endTime} 
                    duration ${traceDoc.derived.duration} 
                    app: ${_app} 
                    status ${_stat}
                    service ${key}
                    url ${_target}
                    `)
                traceDocs.push(traceDoc)
            }
            console.log(`==== SERVICE: ${key} Total ${traces.length} OK ${sum_status_ok} NOK ${traces.length - sum_status_ok}`)
        }
        return traceDocs
    }
    

    async create(id, traces) {

        //console.log(`Mongo Create ${id} ${traces}`)
        let traceDocs = this.build_docs(id, traces)
        try {
            if(traceDocs.length > 0) {
                let newTrace = await Trace.insertMany(traceDocs)
                //let newTrace = await trace.save()
                return newTrace
            }
        } catch (err) {
            console.log('Mongo: Error in create err=' + err)
        }


    }

}
module.exports = Mongo

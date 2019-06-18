/**
 * File Name:           data.js
 * Description:         Data source for clover related stuffs
 * 
 * Last Modified:       02/28/2019
 * Requirement:         Jaeger, Prometheus etc.
 *                      axios
 *                        % npm install -S axios
 *
 *
 **/

const PROM = require('./prom')
const TRACER = require('./tracer')


var restData = {}
var restDataProm = {}
var jaeger_urls = {}
var prom_urls = {}
var log_dir = ""

// now initialize our collecting function which is triggered every INTERVAL seconds
const DEFAULT_POLL_INTERVAL = 6 * 1000 //5 seconds
const DEFAULT_FACTOR_POLL_TO_START = 60 //30 minute to grab the jaeger info
var POLL_INTERVAL
var jaegers = []
var proms = []
var VERBOSE = false
var LOG_IT = false


const DEFAULT_CLOVER_JAEGER_ID="Clover-Istio"
const DEFAULT_CLOVER_JAEGER_HOST="master"
const DEFAULT_CLOVER_JAEGER_PORT="32544" // istio-tracing
const DEFAULT_CLOVISOR_JAEGER_ID="Clovisor"
const DEFAULT_CLOVISOR_JAEGER_HOST="master"
const DEFAULT_CLOVISOR_JAEGER_PORT="32484" // jaeger-deployment
const DEFAULT_CLOVER_PROM_ID="Clover-Istio"
const DEFAULT_CLOVER_PROM_HOST="master"
const DEFAULT_CLOVER_PROM_PORT="31448" //istio-prometheus
const DEFAULT_AUX_JAEGERS = ""
const DEFAULT_AUX_PROMS = ""
const DEFAULT_REST_AUX = ""

var fs = require('fs')

/**
 *
 * PROM
 *
 *
 **/
const PROMETHEUS_ISTIO_TARGETS = ["envoy",
				  "istio-mesh",
				  "kubernetes-apiservers",
				  "kubernetes-cadvisor",
				  "kubernetes-nodes",
				  "kubernetes-service-endpoints",
				  "mixer",
				  "pilot"];

const prom_services = ["proxy_access_control", "clover_server1", "clover_server2", "clover_server3"];
const prom_prefixes = ["envoy_cluster_outbound_9180__", "envoy_cluster_inbound_9180__"];
const prom_suffixes = ["_default_svc_cluster_local_upstream_rq_2xx", "_default_svc_cluster_local_upstream_cx_active"];

/**
 *
 * JAEGER
 *
 **/

function print_jaeger(jaegerObj) {
	for(let key in jaegerObj.traces) {
		let traces = jaegerObj.traces[key]
		console.log(`==== SERVICE: ${key} LEN ${traces.length}`)
		for(let trace of traces) {
			console.log("spans len " + trace.spans.length)
			console.log("processes len " + Object.keys(trace.processes).length)
			for(let span of trace.spans) {
				console.log("span tags len " + span.tags.length)
				for (let tag of span.tags) {
					console.log('KEY ' + tag.key + ' VALUE ' + tag.value)
				}
			}
		}
		console.log(`==== SERVICE: ${key} LEN ${traces.length}`)
	}
}

function analyze_jaeger(responseJson) {
	let counter = 0
	let cloverServices = {}
	let clovisorServices = {}
	for(var jaeger_id in responseJson) {
	    if(counter === 0) {
		cloverServices = responseJson[jaeger_id]
	    } else {
		clovisorServices = responseJson[jaeger_id]		
	    }
	    counter++
	}

	Object.keys(clovisorServices).map(key => {
	    let service = clovisorServices[key]
	    for (let trace of service) {
			let spans = trace['spans']
			for (let span of spans) {
				let tags = span['tags']
				let openTracingReqID = ''
				let operationName = ''
				let traceID = ''
				for (let tag of tags) {
					if(tag['key'] === "requestid") {
						openTracingReqID = tag['value']
						continue
					}
					if(tag['key'] === "envoydecorator") {
						operationName = tag['value']
						continue
					}
					if(tag['key'] === 'traceid') {
						traceID = tag['value']
						continue
					}
				}
				// now process clover spans
				//console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID)
				if(openTracingReqID !== '' && operationName !== '' && traceID !== '') {
					Object.keys(cloverServices).map(key => {
					let serviceC = cloverServices[key]
					//console.log("SERVICEC " + JSON.stringify(serviceC, null, 2))
					
					for (let traceC of serviceC) {
						console.log("Trace ID " + traceC.traceID + " OP NAME " + operationName)
						let spansC = traceC.spans
						//console.log("SPANSC " + JSON.stringify(spansC, null, 2))
						spansC.some(spanC => {
							let foundSpan = false
							//console.log("SPANC " + JSON.stringify(spanC, null, 2))
							//console.log("-> OP Name " + operationName + " === " + spanC['operationName'])
							if(spanC['operationName'] !== operationName) {
								return false
							}
							if(spanC['traceID'] !== traceID) {
								return false
							}
							console.log("XXXXXXXXX=====>")
							let tagsC = spanC['tags']
							let i = tagsC.length
							while (i--) {
							if (tagsC[i]['value'] === openTracingReqID) {
								let references = spanC['references']
								console.log("BINGO ... add to clover span")
								if(references.length > 0) {
									console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: " + spanC['references'][0]['spanID'] + " durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
								} else {
									console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: NULL durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
								}
								spansC.push(span)
								foundSpan = true
								break
							}
						}
						return foundSpan
					}) // some..
			    } //traceC of serviceC
			    return true
			})
		    }
		} //span of spans
	    } //trace of service
	    return true
	})//map	    
}

const combine_jaegers = (cloverServices, clovisorServices) => {
	Object.keys(clovisorServices).map(key => {
	    let service = clovisorServices[key]
	    for (let trace of service) {
			let spans = trace['spans']
			for (let span of spans) {
				let tags = span['tags']
				let openTracingReqID = ''
				let operationName = ''
				let traceID = ''
				for (let tag of tags) {
					if(tag['key'] === "requestid") {
						openTracingReqID = tag['value']
						continue
					}
					if(tag['key'] === "envoydecorator") {
						operationName = tag['value']
						continue
					}
					if(tag['key'] === 'traceid') {
						traceID = tag['value']
						continue
					}
				}
				// now process clover spans
				//console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID)
				if(openTracingReqID !== '' && operationName !== '' && traceID !== '') {
					Object.keys(cloverServices).map(key => {
					let serviceC = cloverServices[key]
					//console.log("SERVICEC " + JSON.stringify(serviceC, null, 2))
					
					for (let traceC of serviceC) {
						console.log("Trace ID " + traceC.traceID + " OP NAME " + operationName)
						let spansC = traceC.spans
						//console.log("SPANSC " + JSON.stringify(spansC, null, 2))
						spansC.some(spanC => {
							let foundSpan = false
							//console.log("SPANC " + JSON.stringify(spanC, null, 2))
							//console.log("-> OP Name " + operationName + " === " + spanC['operationName'])
							if(spanC['operationName'] !== operationName) {
								return false
							}
							if(spanC['traceID'] !== traceID) {
								return false
							}
							console.log("XXXXXXXXX=====>")
							let tagsC = spanC['tags']
							let i = tagsC.length
							while (i--) {
							if (tagsC[i]['value'] === openTracingReqID) {
								let references = spanC['references']
								console.log("BINGO ... add to clover span")
								if(references.length > 0) {
									console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: " + spanC['references'][0]['spanID'] + " durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
								} else {
									console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: NULL durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
								}
								spansC.push(span)
								foundSpan = true
								break
							}
						}
						return foundSpan
					}) // some..
			    } //traceC of serviceC
			    return true
			})
		    }
		} //span of spans
	    } //trace of service
	    return true
	})//map	    
}

async function _trace_jaeger() {
	let clovisor = false
	for(let index = 0; index < jaegers.length; index += 2) {
		let all_jobs = []
		jaegers[index][3].set_end_time()
		jaegers[index+1][3].set_end_time()
		all_jobs.push(jaegers[index][3].collect())
		all_jobs.push(jaegers[index+1][3].collect())
		await Promise.all(all_jobs)
		//combine_jaegers(jaegers[index][3].traces, jaegers[index+1][3].traces)
		
		if(KAFKA != null) {
			ready_to_kafka()
		}

		// console.log("Ready to Insert Traces in Mongo wait ... " + JSON.stringify(jaegers[index][3].traces, null, 2))
		// if(MONGO != null) {
		// 	let origin = {id: jaegers[index][2],
		// 		type: 'clover',
		// 		tracer_url: jaegers[index][0] + ':' + jaegers[index][1] 
		// 	}

		// 	//console.log("ORG " + origin)
		// 	MONGO.create(origin, jaegers[index][3].traces)
		// 	origin = {id: jaegers[index+1][2],
		// 		type: 'clovisor',
		// 		tracer_url: jaegers[index+1][0] + ':' + jaegers[index+1][1] 
		// 	}
		// 	//console.log("ORG " + origin)
		// 	MONGO.create(origin, jaegers[index+1][3].traces)
		// }
	}


	// for(let jaeger of jaegers) {
	// 	let jaeger_id = jaeger[2]
	// 	restData[jaeger_id] = {}
	// 	if(jaeger.length > 3) {
	// 		let jaegerObj = jaeger[3]
	// 		await jaegerObj.collect()
	// 		restData[jaeger_id] = jaegerObj.traces
	// 		if(MONGO != null) {
	// 			console.log("Insert Traces wait ... ")
	// 			MONGO.create(jaeger_id, restData[jaeger_id])
	// 			let traces = await MONGO.find({})
	// 			//console.log("Insert Traces wait ....: \n" + traces)
	// 			//MONGO.delete({})
	// 		}

	// 		if(VERBOSE == true)
	// 			print_jaeger(jaegerObj)
	// 		}

	// }
}

function _trace_it() {
    console.log("*************** Capture Tracing/Monitoring Data, Date: " + new Date());
    _trace_jaeger();
    _trace_prom();
};

function _set_verbose(verbose) {
    VERBOSE=verbose
};


/**
 *
 * Prometheus
 *
 **/

function print_prom(promObj) {
	for(let key in promObj.metrics) {
		let metrics = promObj.metrics[key]
		console.log(`Key ${key}: LEN ${metrics.length}`)
	}
}

var PROM_HOST="master";
var PROM_PORT="";
var PROM_URL='http://' + PROM_HOST + ':' + PROM_PORT;
async function _trace_prom() {
	//console.log("_trace_prom")
    for(let prom of proms) {
		let prom_id = prom[2]
		restDataProm[prom_id] = {}
		if(prom.length > 3) {
			//let promObj = prom[3]
			await prom[3].collect()
			// restDataProm[prom_id] = prom[3].metrics
			// if(VERBOSE == true)
			// 	print_prom(restDataProm)
			if(KAFKA != null) {
				ready_to_kafka()
			}
		
		}

	}
}

function _add_jaeger(h, p, id) {
    jaegers.push([h, p, id]);
}

function _add_prom(h, p, id) {
    proms.push([h, p, id]);
}

var WITHOUT_TRACE=false
var WITHOUT_PROM=false
// main routine for collector
function ccollector() {
	console.log("*************** Capture Tracing/Monitoring Data, Date: " + new Date() + ` without-tracing=${WITHOUT_TRACE} without-prom=${WITHOUT_PROM}`)
	if(!WITHOUT_TRACE)
		_trace_jaeger()
	if(!WITHOUT_PROM)
		_trace_prom()
	

    setTimeout(ccollector, POLL_INTERVAL)
}

const ready_to_kafka = () => {
	let trace_data
	let prom_data
	let json_data = {}


	if(!WITHOUT_TRACE) {
		for(let jaeger of jaegers) {
			if(jaeger.length > 3) {
				//let jaegerObj = jaeger[3]
				let jaegerId = jaeger[2]
				//if(VERBOSE)
					console.log(`Ready to kafka ${jaegerId}: ${jaeger[3].traces}`)

				// 'ts':
				// 'url':
				// 'type':
				//    
				// 'traces': {
				//	'service': [
				//	
				//  ]
				// }
				//
				json_data = jaeger[3].traces
				if (typeof json_data !== 'undefined' && json_data ) {
					//console.log(`LEN Obj Data: -${Object.keys(json_data).length}-`)
					
					if(Object.keys(json_data).length > 0) {
						//console.log(`Trace Data: -${JSON.stringify(json_data, null, 2)}-`)
						let _json_data = {
							'cluster_id': 'my_cluster',
							'ts': jaeger[3].start_time, 
							'url': jaeger[0] + ':' + jaeger[1] ,
							'type': jaeger[2],
							'traces': json_data
							}
						KAFKA.send(KAFKA.topic + '-trace-topic', TRACER.TRACE_JSON_KEY, _json_data)
					} else {
						console.log('kafka.js: WARNING, tracer trying to send emtpy data???')
					}
				} else {
					console.log('kafka.js: WARNING, tracer trying to send undefined data???')
				}				
			}	
		}	
	}
		
	if(!WITHOUT_PROM) {
		for(let prom of proms) {
			if(prom.length > 3) {
				//let promObj = prom[3]
				let promId = prom[2]
				//if(VERBOSE)
					console.log(`PROM Ready to kafka ${promId}: ${prom[3].metrics}`)
				json_data = prom[3].metrics
				if ( typeof json_data !== 'undefined' && json_data ) {
					//console.log(`Data: -${Object.keys(json_data).length}- -${json_data.cpuData.length}- -${json_data.memData.length}- -${json_data.netData.length}-`)
					if(Object.keys(json_data).length >= 0 && (json_data.cpuData.length > 0 || json_data.netData.length > 0 
						|| json_data.memData.length > 0)) {
						KAFKA.send(KAFKA.topic + '-data-topic', PROM.PROM_JSON_KEY, json_data)
					} else {
						console.log('kafka.js: WARNING, prom trying to send emtpy data???')
					}
				} else {
					console.log('kafka.js: WARNING, prom trying to send undefined data???')
				}
			}	
		}
	}

	
}

const _change_poll_interval = (interval) => {
    POLL_INTERVAL = interval;
}

const _without_trace = (on_off) => {
	WITHOUT_TRACE = on_off;
}
const _without_prom = (on_off) => {
    WITHOUT_PROM = on_off;
}

var var_rest_aux = ''
const _start_collector = (rest_aux='') => {
    console.log("Start CCollector, Continously Collecting Tracing and Monitoring from Jaeger and Prometheus")
	console.log("Start At               	: " + Date.now())
	let clovisor = false
	var_rest_aux = rest_aux
	if(MONGO != null) {
		console.log("MongoDB    : " + MONGO.route)
	} else {
		console.log("MongoDB    : NONE")
	}
	if(KAFKA != null) {
		console.log(`KAFKA    		:  ${KAFKA.host}:${KAFKA.port} on prefix topic '${KAFKA.topic}'`)
	} else {
		console.log("KAFKA    		:  NONE")
	}

	if(WITHOUT_TRACE == false) {
		let TRACER_INCLUDED_SERVICES = process.env.CCOLLECTOR_TRACER_INCLUDED_SERVICES || TRACER.INCLUDED_SERVICES
		let tracer_svcs = TRACER_INCLUDED_SERVICES.split(",")
		let t_svcs = []
		if(tracer_svcs[0] != "") {
			for(let svc of tracer_svcs) {
				t_svcs.push(svc)
			}
		}

		for(let jaeger of jaegers) {
			let t = new TRACER(jaeger[0], jaeger[1], jaeger[2], clovisor ? 'clovisor' : 'clover', rest_aux)
			clovisor = !clovisor
			t.verbose = VERBOSE
			t.startCaptureInMsec = POLL_INTERVAL
			for(let svc of t_svcs) {
				t.add_included_svc(svc)
			}

			console.log("Jaeger ID     	: " + t.id)
			console.log("Jaeger URL     	: " + t.url0)
			// console.log("Jaeger Capture Interval (msec)    	: " + t.startCaptureInMsec)
			jaeger.push(t)
		}
		console.log("Jaeger INCLUDED SERVICES : " + TRACER_INCLUDED_SERVICES)
	} else {
		console.log("TRACING : OFF")
	}


	if(WITHOUT_PROM == false) {
		let PROM_INCLUDED_NS = process.env.CCOLLECTOR_PROM_INCLUDED_NS || PROM.INCLUDED_NS
		let prom_nss = PROM_INCLUDED_NS.split(",")
		let nss = []
		if(prom_nss[0] != "") {
			for(let ns of prom_nss) {
				nss.push(ns)
			}
		}
		let PROM_INCLUDED_APP = process.env.CCOLLECTOR_PROM_INCLUDED_APP || PROM.INCLUDED_APP
		let prom_apps = PROM_INCLUDED_APP.split(",")
		let apps = []
		if(prom_apps[0] != "") {
			for(let app of prom_apps) {
				apps.push(app)
			}
		}

		let PROM_CAPTURE_STEP = process.env.CCOLLECTOR_PROM_CAPTURE_STEP || PROM.CAPTURE_STEP

		for(let prom of proms) {
			let p = new PROM(prom[0], prom[1], prom[2])
			p.verbose = VERBOSE
			for(let ns of nss) {
				p.add_included_ns(ns)
			}
			for(let app of apps) {
				p.add_included_app(app)
			}
			p.step_capture = '5m' // 5 minutes rate
			// p.step_capture = `${POLL_INTERVAL / 1000}s`
			console.log("Prometheus ID     	: " + prom[2])
			console.log("Prometheus URL     	: " + p.url_query)
			prom.push(p)
		}
		console.log("Prometheus INCLUDED NS : " + PROM_INCLUDED_NS)
		console.log("Prometheus INCLUDED APP : " + PROM_INCLUDED_APP)
		console.log("Prometheus CAPTURE STEP : " + PROM_CAPTURE_STEP)
	} else {
		console.log("Prometheus: OFF")
	}

    console.log("Capture Poll Interval               	: " + POLL_INTERVAL + " msec");    
    ccollector();
};

const _get_rt_traces = async () => {
	console.log("*************** Capture 'RT' Tracing/Monitoring Data, Date: " + new Date())

	let retData = {}
	let clovisor = false
	for(let jaeger of jaegers) {
		let jaeger_id = jaeger[2]
		retData[jaeger_id] = {}
		if(jaeger.length > 3) {
			let jaegerObj = new TRACER(jaeger[0], jaeger[1], jaeger[2], clovisor ? 'clovisor' : 'clover', var_rest_aux)
			clovisor = !clovisor
			jaegerObj.startCaptureInMsec = (POLL_INTERVAL * DEFAULT_FACTOR_POLL_TO_START)
			await jaegerObj.collect()
			retData[jaeger_id] = jaegerObj.traces
			if(VERBOSE == true)
				print_jaeger(jaegerObj)
		}
	}   
	return(retData)
}

const _get_rt_proms = () => {

}

var MONGO = null
function _set_db(mongo) {
	MONGO = mongo
}
var KAFKA = null
function _with_kafka(kafka) {
	KAFKA = kafka
}

module.exports = {
    restData: restData,
    jaeger_urls: jaeger_urls,
    restDataProm: restDataProm,
    prom_urls: prom_urls,
    DEFAULT_POLL_INTERVAL: DEFAULT_POLL_INTERVAL,
    DEFAULT_CLOVER_JAEGER_ID: DEFAULT_CLOVER_JAEGER_ID,
    DEFAULT_CLOVER_JAEGER_HOST: DEFAULT_CLOVER_JAEGER_HOST,
    DEFAULT_CLOVER_JAEGER_PORT: DEFAULT_CLOVER_JAEGER_PORT,
    DEFAULT_CLOVISOR_JAEGER_ID: DEFAULT_CLOVISOR_JAEGER_ID,
    DEFAULT_CLOVISOR_JAEGER_HOST: DEFAULT_CLOVISOR_JAEGER_HOST,
    DEFAULT_CLOVISOR_JAEGER_PORT: DEFAULT_CLOVISOR_JAEGER_PORT,
    DEFAULT_CLOVER_PROM_ID: DEFAULT_CLOVER_PROM_ID,
    DEFAULT_CLOVER_PROM_HOST: DEFAULT_CLOVER_PROM_HOST,    
    DEFAULT_CLOVER_PROM_PORT: DEFAULT_CLOVER_PROM_PORT,
    DEFAULT_AUX_JAEGERS: DEFAULT_AUX_JAEGERS,
    DEFAULT_AUX_PROMS: DEFAULT_AUX_PROMS,
	DEFAULT_REST_AUX: DEFAULT_REST_AUX,
    trace_it() {
		_trace_it();
    },
    set_verbose(verbose) {
		_set_verbose(verbose);
    },
    add_jaeger(h, p, id) {
		_add_jaeger(h, p, id);
    },
    add_prom(h, p, id) {
		_add_prom(h, p, id);
    },
    change_poll_interval(interval) {
		_change_poll_interval(interval);
    },
    start_collector(rest_aux) {
		_start_collector(rest_aux);
	},
	set_db(mongo) {
		_set_db(mongo)
	},
	get_rt_traces() {
		return(_get_rt_traces())
	},
	get_rt_proms() {
		return(_get_rt_proms())
	},
	without_trace(on_off) {
		return(_without_trace(on_off))
	},
	without_prom(on_off) {
		return(_without_prom(on_off))
	},
	with_kafka(kafka) {
		return(_with_kafka(kafka))
	}
};

//_add_prom(CLOVER_PROM_HOST, CLOVER_PROM_PORT, CLOVER_PROM_ID);
//_add_jaeger(CLOVER_JAEGER_HOST, CLOVER_JAEGER_PORT, CLOVER_JAEGER_ID);
//_add_jaeger(CLOVISOR_JAEGER_HOST, CLOVISOR_JAEGER_PORT, CLOVISOR_JAEGER_ID);
//_start_collector();


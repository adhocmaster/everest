/**
 * File Name:           server.js
 * Description:         Continous Data Collector for Clover, Clovisor, Prometheus
 * 
 * Last Modified:       02/28/2019
 * Requirement:         Jaeger, Prometheus etc.
 *                      express
 *                        % npm install -S express
 *
 *
 **/

const express = require("express");
const bodyParser = require("body-parser");
const logger = require("morgan");

const API_PORT = 8888;
const app = express();
const router = express.Router()
const cors = require('cors')
const DS = require('./data')
const kafka = require('./kafka')


const DEFAULT_VERBOSE = false
const DEFAULT_WITH_MONGO = false
const DEFAULT_WITHOUT_TRACE = false
const DEFAULT_WITHOUT_PROM = false
const DEFAULT_KAFKA_HOST = ''
const DEFAULT_KAFKA_PORT = -1
const DEFAULT_KAFKA_DEBUG = false
const DEFAULT_KAFKA_DATA_TOPIC = 'everest-data-topic'

// (optional) only made for logging and
// bodyParser, parses the request body to be a readable json format
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(cors());
app.use(logger("dev"));

// serve the react app files
app.use(express.static(`${__dirname}/../ui/build`));

const traces_api = require('./traces_api')
const metric_api = require('./metric_api');

POLL_INTERVAL = process.env.CCOLLECTOR_POLL_INTERVAL || DS.DEFAULT_POLL_INTERVAL;
CLOVER_JAEGER_ID = process.env.CCOLLECTOR_CLOVER_JAEGER_ID || DS.DEFAULT_CLOVER_JAEGER_ID;
CLOVER_JAEGER_HOST = process.env.CCOLLECTOR_CLOVER_JAEGER_HOST || DS.DEFAULT_CLOVER_JAEGER_HOST;
CLOVER_JAEGER_PORT = process.env.CCOLLECTOR_CLOVER_JAEGER_PORT || DS.DEFAULT_CLOVER_JAEGER_PORT;
CLOVISOR_JAEGER_ID = process.env.CCOLLECTOR_CLOVISOR_JAEGER_ID || DS.DEFAULT_CLOVISOR_JAEGER_ID;
CLOVISOR_JAEGER_HOST = process.env.CCOLLECTOR_CLOVISOR_JAEGER_HOST || DS.DEFAULT_CLOVISOR_JAEGER_HOST;
CLOVISOR_JAEGER_PORT = process.env.CCOLLECTOR_CLOVISOR_JAEGER_PORT || DS.DEFAULT_CLOVISOR_JAEGER_PORT;
CLOVER_PROM_ID = process.env.CCOLLECTOR_CLOVER_PROM_ID || DS.DEFAULT_CLOVER_PROM_ID;
CLOVER_PROM_HOST = process.env.CCOLLECTOR_CLOVER_PROM_HOST || DS.DEFAULT_CLOVER_PROM_HOST;
CLOVER_PROM_PORT = process.env.CCOLLECTOR_CLOVER_PROM_PORT || DS.DEFAULT_CLOVER_PROM_PORT;
AUX_JAEGERS = process.env.CCOLLECTOR_AUX_JAEGERS || DS.DEFAULT_AUX_JAEGERS;
AUX_PROMS = process.env.CCOLLECTOR_AUX_PROMS || DS.DEFAULT_AUX_PROMS;
VERBOSE = process.env.CCOLLECTOR_VERBOSE || DEFAULT_VERBOSE
WITH_MONGO = process.env.CCOLLECTOR_WITH_MONGO || DEFAULT_WITH_MONGO
WITHOUT_TRACE = process.env.CCOLLECTOR_WITHOUT_TRACE || DEFAULT_WITHOUT_TRACE
WITHOUT_PROM = process.env.CCOLLECTOR_WITHOUT_PROM || DEFAULT_WITHOUT_PROM
REST_AUX = process.env.CCOLLECTOR_REST_AUX || DS.DEFAULT_REST_AUX
KAFKA_HOST = process.env.CCOLLECTOR_KAFKA_HOST || DEFAULT_KAFKA_HOST
KAFKA_PORT = process.env.CCOLLECTOR_KAFKA_PORT || DEFAULT_KAFKA_PORT
KAFKA_DEBUG = process.env.CCOLLECTOR_KAFKA_DEBUG || DEFAULT_KAFKA_DEBUG
KAFKA_DATA_TOPIC = process.env.CCOLLECTOR_KAFKA_DATA_TOPIC || DEFAULT_KAFKA_DATA_TOPIC

aux_jaeger_list = AUX_JAEGERS.split(",")
aux_prom_list = AUX_PROMS.split(",")
if(aux_jaeger_list[0] != "") {
    aux_jaeger_list.forEach(function(jaeger_entry) {
	jaeger_entry_array = jaeger_entry.split(":");
	DS.add_jaeger(jaeger_entry_array[0], jaeger_entry_array[1], jaeger_entry_array[2]);
    });
}
if(aux_prom_list[0] != "") {
    aux_prom_list.forEach(function(prom_entry) {
	prom_entry_array = prom_entry.split(":");
	DS.add_prom(prom_entry_array[0], prom_entry_array[1], prom_entry_array[2]);
    });
}

var _kafka;
if(KAFKA_HOST != '') {
	_kafka = new kafka(KAFKA_HOST, KAFKA_PORT)
	_kafka.verbose = KAFKA_DEBUG
	_kafka.topic = KAFKA_DATA_TOPIC
	// _kafka = new kafka()
	// for(i=0; i < 40; i++) {
	// 	_kafka.send()
	// }
	DS.with_kafka(_kafka)
}

DS.set_verbose(VERBOSE == 'true')
DS.add_jaeger(CLOVER_JAEGER_HOST, CLOVER_JAEGER_PORT, CLOVER_JAEGER_ID)
DS.add_jaeger(CLOVISOR_JAEGER_HOST, CLOVISOR_JAEGER_PORT, CLOVISOR_JAEGER_ID)
DS.add_prom(CLOVER_PROM_HOST, CLOVER_PROM_PORT, CLOVER_PROM_ID)
DS.change_poll_interval(POLL_INTERVAL)
if(WITH_MONGO) {
	const Mongo = require('./mongo')
	MONGO_ROUTE = process.env.CCOLLECTOR_MONGO_ROUTE || 'mongodb://localhost:27017/everest'
	mongo = new Mongo(MONGO_ROUTE)
	DS.set_db(mongo)
}

DS.without_trace(WITHOUT_TRACE)
DS.without_prom(WITHOUT_PROM)
DS.start_collector(REST_AUX)


// append /api for our http requests
app.use("/api", traces_api())
app.use("/api", metric_api())

// launch our backend into a port
app.listen(API_PORT, () => {
	console.log(`LISTENING ON PORT ${API_PORT}`)
})

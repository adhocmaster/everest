/**
 * File Name:           guide.js
 * Description:         simulate memory/networking intensive task
 * 
 * Last Modified:       05/04/2019
 * Requirement:         nodejs
 *                      grpc
 *
 *
 **/

"use strict"

const axios = require('axios')
var PROTO_PATH = __dirname + '/protos/route_guide.proto';

var fs = require('fs');
var _ = require('lodash');
var grpc = require('grpc');
var protoLoader = require('@grpc/proto-loader');
var packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {keepCase: true,
     longs: String,
     enums: String,
     defaults: true,
     oneofs: true
    });
var routeguide = grpc.loadPackageDefinition(packageDefinition).routeguide;

const DEFAULT_VERBOSE = true
const DEFAULT_GUIDE_HOST = "guide"
const DEFAULT_GUIDE_PORT = 9999
var VERBOSE = process.env.EVEREST_GUIDE_VERBOSE || DEFAULT_VERBOSE
var GUIDE_HOST = process.env.EVEREST_GUIDE_HOST || DEFAULT_GUIDE_HOST
var GUIDE_PORT = process.env.EVEREST_GUIDE_PORT || DEFAULT_GUIDE_PORT

var client = new routeguide.RouteGuide(`${GUIDE_HOST}:${GUIDE_PORT}`,
									   grpc.credentials.createInsecure());
									   
if(VERBOSE) {
	console.log(`Guide Grpc Client connecting to ${GUIDE_HOST}:${GUIDE_PORT} ...`)
}
									
var FIBO_REMOTE_HOST="fibo"
var FIBO_REMOTE_PORT=9001
var COORD_FACTOR = 1e7;

function featureCallbackHeavy(error, feature) {
    if (error) {
	   console.error("featureCallbackHeavy ****** ERROR ***** " + error)
       return;
    }
    if (feature.name === '') {
      console.log('Found no feature at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    } else {
      console.log('Found feature called "' + feature.name + '" at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    }
}


const _goal = (g) => {
	// Call direct GRPC here
	var point1 = {
		latitude: g,
		longitude: g
	  };
	
	if(VERBOSE) {
		console.log(`Forward/Call Grpc Server FeatureHeavy ...`)
	}
	client.getFeatureHeavy(point1, featureCallbackHeavy);
    return 0
}

const _goal_remote = (g) => {
    // Call Fibo and GRPC here, obsolete???
	console.log("TBD")
	return 0
}

const _goal_fibo = (g) => {
	console.log("_goal_fibo " + VERBOSE)
	if(VERBOSE) {
        console.log(`_goal_fibo: forwarding to ---> http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/goal?n=${g}`)
    }

    // Call REST here
    axios.get(`http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/goal?n=${g}`)
    .then(response => {
        if(VERBOSE) {
            console.log(`fibo_remote response: -${JSON.stringify(response.data, 0, 2)}- <--- http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/fibo?n=${n}`)
		}
		fibo = response.data
		return(fibo)
    })
    .catch(error => {
      console.log(`Error: ${error}`)
    })	
	return 0
}

module.exports = {
	VERBOSE: VERBOSE,
	FIBO_REMOTE_HOST: FIBO_REMOTE_HOST,
	FIBO_REMOTE_PORT: FIBO_REMOTE_PORT,
	goal(g) {
		return(_goal(g))
	},
	goal_remote(g) {
		return(_goal_remote(g))
	},
	goal_fibo(g) {
		return(_goal_fibo(g))
	},	
}
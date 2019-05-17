/**
 * File Name:           metric_api.js
 * Description:         contains all the routing information and the implementation
 *                      of the REST API 'api/metric' to get prometheus information
 * 
 * Last Modified:       03/28/2019
 * Requirement:         Prometheus
 *                      axios
 *                        % npm install -S axios
 *                        % npm install -S morgan
 *
 *
 **/

const {Router} = require('express')

const DS = require('./data')

const DEFAULT_GRAFANA_URL = 'http://master:30003'
var GRAFANA_URL = process.env.CCOLLECTOR_GRAFANA_URL || DEFAULT_GRAFANA_URL
const DEFAULT_IGRAFANA_URL = 'http://master:30004'
var IGRAFANA_URL = process.env.CCOLLECTOR_IGRAFANA_URL || DEFAULT_IGRAFANA_URL
const DEFAULT_IKIALI_URL = 'http://master:30005'
var IKIALI_URL = process.env.CCOLLECTOR_IKIALI_URL || DEFAULT_IKIALI_URL
const DEFAULT_ITRACING_URL = 'http://master:30006'
var ITRACING_URL = process.env.CCOLLECTOR_ITRACING_URL || DEFAULT_ITRACING_URL

console.log('-------------------------------------------')
console.log('Service from Istio:')
console.log(`IGRAFANA ---> ${IGRAFANA_URL}`)
console.log(`IKIALI ---> ${IKIALI_URL}`)
console.log(`ITRACING ---> ${ITRACING_URL}`)
console.log('-------------------------------------------')

// this is our main retrieval methods 
// it retrieve clover data from Prometheus
// using either GET or POST method

module.exports = (router = new Router()) => {
  router.get('/metrics', async (req, res) => {
    return res.json(
      {
        "data": DS.restDataProm
      }
    )
  }),
  router.get("/proms", async (req, res) => {
    res.json(
	    {
            "data": DS.prom_urls
	    }
    )
  }),
  router.get('/grafana_url', async (req, res) => {
    res.json(
        {
            "data": GRAFANA_URL
	    }
    )
  })
  router.get('/istiografana_url', async (req, res) => {
    res.json(
        {
            "data": IGRAFANA_URL
	    }
    )
  })
  router.get('/kiali_url', async (req, res) => {
    res.json(
        {
            "data": IKIALI_URL
	    }
    )
  })
  router.get('/tracing_url', async (req, res) => {
    res.json(
        {
            "data": ITRACING_URL
	    }
    )
  })    
  return router
}
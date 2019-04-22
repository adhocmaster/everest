/**
 * File Name:           traces_api.js
 * Description:         contains all the routing information and the implementation
 *                      of the REST API 'api/traces' to get jaeger information
 * 
 * Last Modified:       03/28/2019
 * Requirement:         Jaeger.
 *                      express
 *                        % npm install -S express
 *
 *
 **/

const {Router} = require('express')

const DS = require('./data')

// this is our main retrieval methods 
// it retrieve clover data from Jaeger
// using either GET or POST method

module.exports = (router = new Router()) => {
  router.get('/traces', async (req, res) => {
    return res.json(
      {
        "data": await DS.get_rt_traces(),
      }
    )
  }),
  router.get("/jaegers", async (req, res) => {
    res.json(
	    {
	      "data": DS.jaeger_urls
	    }
    )
  })
  return router
}
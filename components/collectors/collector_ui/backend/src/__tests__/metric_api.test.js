/**
 * File Name:           metric_api.test.js
 * Description:         Script to test metric_api.js
 * 
 * Last Modified:       02/28/2019
 * Requirement:         supertest
 *                        % npm install -S supertest
 *
 *
 **/

const request = require('supertest')
const METRIC_API = require('../metric_api')


describe('Router Tests', function() {
   it('testing /metrics, should return data with {}', async () => {
      //bug bug bug
      request(METRIC_API).get('/metrics').
      expect(200)
   })


})




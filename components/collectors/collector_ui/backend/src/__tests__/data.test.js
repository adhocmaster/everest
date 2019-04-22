/**
 * File Name:           data.test.js
 * Description:         Script to test data.js
 * 
 * Last Modified:       02/28/2019
 * Requirement:         Jaeger
 *                        % npm install -S request
 *
 *
 **/

const DS = require('../data')


describe('Smoke Test', function() {
   it('string returning hello there jest', () => {
      expect(DS.sayHello()).toMatch('hello there jest')
   })
})

describe('Jaeger', function() {
   const expected_jaeger_urls = {jaeger_urls: [['myhost', 'myport', 'myjaeger_id']]}
   it('add a jaeger url', () => {
      DS.add_jaeger("myhost", "myport", "myjaeger_id")
      expect(expected_jaeger_urls).toMatchObject(expected_jaeger_urls)
   })
})




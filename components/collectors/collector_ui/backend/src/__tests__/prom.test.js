/**
 * File Name:           prom.test.js
 * Description:         Script to test prom.js
 * 
 * Last Modified:       02/28/2019
 * Requirement:         Jaeger
 *                        % npm install -S request
 *
 *
 **/

const PROM = require('../prom')
const Request = jest.mock('request')



describe('Smoke Test', function() {

   it('prom class default attributes', () => {
      let p = new PROM()
      let expected_url0 = 'http://' + PROM.PROM_HOST  + ':' + PROM.PROM_PORT + '/api/v1/targets'
      let expected_url1 = 'http://' + PROM.PROM_HOST + ':' + PROM.PROM_PORT + '/api/v1/query?query='
      expect(p.id).toMatch(PROM.PROM_ID)
      expect(p.port).toBe(PROM.PROM_PORT)
      expect(p.host).toMatch(PROM.PROM_HOST)
      expect(p.url0).toMatch(expected_url0)
      expect(p.url1).toMatch(expected_url1)
      expect(p._verbose).toBe(false)
   })
   it('prom class set attributes', () => {
      let p = new PROM("HOST", 10000, "ID")
      let expected_url0 = 'http://HOST:10000/api/v1/targets'
      let expected_url1 = 'http://HOST:10000/api/v1/query?query='
      expect(p.id).toMatch("ID")
      expect(p.port).toBe(10000)
      expect(p.host).toMatch("HOST")
      expect(p.url0).toMatch(expected_url0)
      expect(p.url1).toMatch(expected_url1)
      expect(p._verbose).toBe(false)
      p.verbose = true
      expect(p._verbose).toBe(true)
   })
})

const mockResponse = () => {
   const res = {};
   res.status = jest.fn().mockReturnValue(res);
   res.json = jest.fn().mockReturnValue(res);
   return res;
}
 
test('should fetch collect0', () => {
   const users = [{name: 'Bob'}]
   const resp = {data: users}
   let p = new PROM()
   return p._collect0().then(resp => expect(resp.data).toEqual(users));
 })


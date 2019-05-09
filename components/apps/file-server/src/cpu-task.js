/**
 * File Name:           cpu-task.js
 * Description:         simulate cpu intensive task
 * 
 * Last Modified:       05/04/2019
 * Requirement:         nodejs
 *
 *
 **/

"use strict"

const _fibo = (n) => {
    if (n < 2)
        return 1
    else   
        return _fibo(n - 2) + _fibo(n - 1)
}

module.exports = {
	fibo(n) {
		return(_fibo(n))
	}
}
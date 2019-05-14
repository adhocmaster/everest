/**
 * File Name:           server.js
 * Description:         file server with REST API
 * 
 * Last Modified:       05/04/2019
 * Requirement:         nodejs
 *                      express, axios
 *                        % npm install -S express
 *                        % npm install -S axios
 *
 *
 * How to test:
 *  % curl -o flink-eliot.jar http://localhost:9000/file?name=flink-eliot.jar
 *  % curl -o sample.mp4 http://localhost:9000/video?name=sample.mp4
 * How to stress the CPU:
 *  % curl -o fibo.res http://localhost:9000/fibo?n=50
 *  % curl -o fibo.res http://localhost:9000/fibo_remote?n=50
 * 
 *  % curl -o guide.res http://localhost:9000/guide?goal=1
 *  % curl -o guide.res http://localhost:9000/guide_remote?goal=1
 **/

"use strict"


const express = require('express')
const fs = require('fs')
const path = require('path')
const app = express()
const mime = require('mime')
const axios = require('axios')

const cpu_task = require('./cpu-task')
const guide = require('./guide')

const DEFAULT_VERBOSE = true
const FS_PORT_DEFAULT = 9000
const FS_NO_VIDEO_DEFAULT = false
const FS_NO_FILE_DEFAULT = false

app.use(express.static(path.join(__dirname, 'public')))

app.get('/', function(req, res) {
    res.sendFile(path.join(__dirname + '/index.htm'))
})

const FIBO_ARG_DEFAULT = 10
app.get('/fibo', function(req, res) {
    let n = req.query.n || FIBO_ARG_DEFAULT

    if(VERBOSE) {
        console.log(`fibo: ${n}`)
    }

    return res.json(
      {
        "data": cpu_task.fibo(n)
      }
    )
})

const DEFAULT_FIBO_REMOTE_HOST = 'localhost'
const DEFAULT_FIBO_REMOTE_PORT = FS_PORT_DEFAULT

var VERBOSE = process.env.EVEREST_FS_VERBOSE || DEFAULT_VERBOSE
var FS_PORT = process.env.EVEREST_FS_PORT || FS_PORT_DEFAULT
var FS_NO_VIDEO = process.env.EVEREST_FS_NO_VIDEO|| FS_NO_VIDEO_DEFAULT
var FS_NO_FILE = process.env.EVEREST_FS_NO_VIDEO|| FS_NO_FILE_DEFAULT
var FIBO_REMOTE_HOST = process.env.EVEREST_FS_FIBO_REMOTE_HOST || DEFAULT_FIBO_REMOTE_HOST
var FIBO_REMOTE_PORT = process.env.EVEREST_FS_FIBO_REMOTE_PORT || DEFAULT_FIBO_REMOTE_PORT

app.get('/fibo_remote', async (req, res) => {
    let n = req.query.n || FIBO_ARG_DEFAULT
    if(VERBOSE) {
        console.log(`fibo_remote: forwarding to ---> http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/fibo?n=${n}`)
    }
    axios.get(`http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/fibo?n=${n}`)
    .then(response => {
        if(VERBOSE) {
            console.log(`fibo_remote response: -${JSON.stringify(response.data, 0, 2)}- <--- http://${FIBO_REMOTE_HOST}:${FIBO_REMOTE_PORT}/fibo?n=${n}`)
        }
        return res.json(
            {
            "data": response.data
            }
        )
    })
    .catch(error => {
      console.log(error);
    })
})

const GUIDE_ARG_DEFAULT = 0
app.get('/guide', function(req, res) {
    let goal = req.query.goal || GUIDE_ARG_DEFAULT

    if(VERBOSE) {
        console.log(`guide: ${goal}`)
    }

    return res.json(
      {
        "data": guide.goal(goal)
      }
    )
})
app.get('/guide_remote', function(req, res) {
    let goal = req.query.goal || GUIDE_ARG_DEFAULT

    if(VERBOSE) {
        console.log(`guide_remote: ${goal}`)
    }

    return res.json(
      {
        "data": guide.goal_remote(goal)
      }
    )
})

app.get('/guide_fibo', function(req, res) {
    let goal = req.query.goal || GUIDE_ARG_DEFAULT

    if(VERBOSE) {
        console.log(`guide_fibo: ${goal}`)
    }

    return res.json(
      {
        "data": guide.goal_fibo(goal)
      }
    )
})

guide.DEFAULT_FIBO_REMOTE_HOST = FIBO_REMOTE_HOST
guide.DEFAULT_FIBO_REMOTE_PORT = FIBO_REMOTE_PORT
console.log("V " + VERBOSE)
guide.VERBOSE = VERBOSE


var DEFAULT_VIDEO = 'sample.mp4'
var DEFAULT_FILE = 'eliot.jar'
if(!FS_NO_VIDEO) {
    app.get('/video', function(req, res) {
        let filename = req.query.name || DEFAULT_VIDEO
        let path = 'assets/video/' + filename

        if(VERBOSE) {
            console.log(`video ---> File ${path}`)
        }

        if (fs.existsSync(path)) {
            //file exists, NOOP
            if(VERBOSE) {
                console.log(`File ${path} OK`)
            }
        } else {
            console.error(`WARNING:  File ${path} doesn't EXIST, setting default video file`)
            path = 'assets/video/' + DEFAULT_VIDEO
        }
        if(VERBOSE) {
            console.log(`video retrieve File ${path} ...`)
        }

        const stat = fs.statSync(path)
        const fileSize = stat.size
        const range = req.headers.range
        const mimetype = mime.lookup(path)
 

        if (range) {
            const parts = range.replace(/bytes=/, "").split("-")
            const start = parseInt(parts[0], 10)
            const end = parts[1] ? parseInt(parts[1], 10) : fileSize-1

            const chunksize = (end-start)+1
            const file = fs.createReadStream(path, {start, end})
            const head = {
                'Content-Range': `bytes ${start}-${end}/${fileSize}`,
                'Accept-Ranges': 'bytes',
                'Content-Length': chunksize,
                'Content-Type': mimetype,
                //'Content-Type': 'video/mp4',
            }

            res.writeHead(206, head)
            file.pipe(res)
        } else {
            const head = {
                'Content-Length': fileSize,
                'Content-Type': mimetype,
                //'Content-Type': 'video/mp4',
            }
            res.writeHead(200, head)
            fs.createReadStream(path).pipe(res)
        }
    })
}

if(!FS_NO_FILE) {
    app.get('/file', function(req, res) {
        let filename = req.query.name || DEFAULT_FILE
        let path = 'assets/file/' + filename

        if(VERBOSE) {
            console.log(`file ---> File ${path}`)
        }


        if (fs.existsSync(path)) {
            //file exists, NOOP
            console.log(`File ${path} OK`)
        } else {
            console.error(`WARNING:  File ${path} doesn't EXIST, setting default file`)
            path = 'assets/file/' + DEFAULT_FILE
        }

        if(VERBOSE) {
            console.log(`file retrieve File ${path} ...`)
        }

        const stat = fs.statSync(path)
        const fileSize = stat.size
        const range = req.headers.range
        const mimetype = mime.lookup(path)
    
        if (range) {
        const parts = range.replace(/bytes=/, "").split("-")
        const start = parseInt(parts[0], 10)
        const end = parts[1]
            ? parseInt(parts[1], 10)
            : fileSize-1
    
        const chunksize = (end-start)+1
        const file = fs.createReadStream(path, {start, end})
        const head = {
            'Content-Range': `bytes ${start}-${end}/${fileSize}`,
            'Accept-Ranges': 'bytes',
            'Content-Length': chunksize,
            'Content-Type': mimetype,
        }
    
        res.writeHead(206, head)
        file.pipe(res)
        } else {
            const head = {
                'Content-Length': fileSize,
                'Content-Type': mimetype,
            }
            res.writeHead(200, head)
            fs.createReadStream(path).pipe(res)
        }
    })
}

app.listen(FS_PORT, function () {
    // if(VERBOSE)
    console.log('Listening on port ' + FS_PORT + '!')
})

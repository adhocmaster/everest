/**
 * File Name:           kafka.js
 * Description:         Kafka access acting as producer
 * 
 * Last Modified:       05/08/2019
 * Requirement:         
 *                      kafka-node
 *                        % npm install -S kafka-node
 *
 *
 **/

"use strict"

const kafka = require('kafka-node')

class Kafka {
    // all class level constants
    static get KAFKA_HOST() {
        //return "bootstrap.kafka"
        return 'master'
    }
    static get KAFKA_PORT() {
        // 9092
        return 32400
    }
    static get KAFKA_TYPE() {
        return "producer"
    }
    static get KAFKA_TOPIC() {
        return "mytopic"
    }

    constructor(host=Kafka.KAFKA_HOST, port=Kafka.KAFKA_PORT, type=Kafka.KAFKA_TYPE, topic=Kafka.KAFKA_TOPIC, verbose=false) {
      this._verbose = verbose
      this._host = host
      this._port = port
      this._type = type
      this._topic = topic
      this.connect()
    }
    get host() {
        return this._host
    }
    get port() {
        return this._port
    }
    get topic() {
        return this._topic
    }

    set verbose(on_or_off) {
        this._verbose = on_or_off
    }
    set topic(new_topic) {
        this._topic = new_topic
    }

    kafka_err (err) {
        console.log(err)
        console.log('[kafka-producer -> '+this._topic+']: connection errored')
        throw err
    }

    async send(key, json_data) {       
        if(!isEmpty(json_data)) {
            let KeyedMessage = kafka.KeyedMessage
            let dataKM = new KeyedMessage(key, JSON.stringify(json_data))
            let payloads = [
                {
                topic: this._topic,
                messages: dataKM
                }
            ]
            if(this._verbose)
                console.log(`kafka.js: sending json with the key ${key} with data ${json_data}`)
            
            let push_status = this.producer.send(payloads, (err, data) => {
                if (err) {
                console.log('[kafka-producer -> '+this._topic+']: broker update failed');
                } else {
                console.log('[kafka-producer -> '+this._topic+']: broker update success');
                }
            })
        } else {
            console.log('kafka.js: WARNING, trying to send emtpy data???')
        }
    }

    connect() {
        // if(this._verbose)
            console.log(`Trying to connect to Kafka at ${this._host}:${this._port}, topic ${this._topic}`)
        this.Producer = kafka.Producer
        this.client = new kafka.KafkaClient({kafkaHost: `${this._host}:${this._port}`})
        this.producer = new this.Producer(this.client)
        this.producer.on('ready', this.send)
        this.producer.on('error', this.kafka_err)
    }
    

 
}

module.exports = Kafka
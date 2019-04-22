import React, { Component } from 'react'
import Tabs from 'react-bootstrap/Tabs'
import Tab from 'react-bootstrap/Tab'
import TraceList from './Traces'
import MainProm from './MainProm'
import Debug from './Debug'
import Analytics from './Analytics'

class MainPanel extends Component {
    state = {
		data: {},
		prom_data: {},
		jaegers: {},
		total: 0
    }
    constructor() {
		super()
        this.getTraces()
		this.getJaegers()	
		this.getProms()
    }
    getTraces = () => {
	fetch('/api/traces')
	    .then((response) => response.json())
	    .then((responseJson) => {
		this.setState({
		    data: responseJson.data,
		    total: responseJson.data.length
		});
	    })	
	}
	getProms = () => {
		fetch('/api/metrics')
			.then((response) => response.json())
			.then((responseJson) => {
			this.setState({
				prom_data: responseJson.data
			});
			})	
		}
    getJaegers = () => {
	fetch('/api/jaegers')
	    .then((response) => response.json())
	    .then((responseJson) => {
		this.setState({
		    jaegers: responseJson.data
		});
	    })	
    }

    render() {
        return (
		<Tabs defaultActiveKey="trace" id="mainpanel">
		<Tab eventKey="trace" title="Traces">
		<TraceList data={this.state.data}/>
		</Tab>
		<Tab eventKey="prom" title="Prometheus">
		<MainProm />
		</Tab>
		<Tab eventKey="analytics" title="Analytics">
		<Analytics data={this.state.prom_data}/>
		</Tab>
		<Tab eventKey="debug" title="Debug">
		<Debug data={this.state.data}/>
		</Tab>
		<Tab eventKey="tbd" title="Others" disabled>
		<div />
		</Tab>
		</Tabs>
        )
    }
}

/*
  { this.state.data ? (
  <div id="data">
  <ReactJson src={this.state.data} name="All Traces" theme="monokai"/>
  </div>
  ) : "No Traces found" }
*/

export default MainPanel


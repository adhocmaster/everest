import React, { Component } from 'react'
import IFrame from './IFrame'

class PromList extends Component {
    state = {
		grafana_url: ""
	}

	constructor() {
		super()
		this.getGrafana()
    }
 
	getGrafana = () => {
		fetch('/api/grafana_url')
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					grafana_url: responseJson.data
				})
			})
	}
 

    render() {
        return (
	    <div id="parent">
			<p>Grafana Host: {this.state.grafana_url}</p>
			<IFrame src={this.state.grafana_url} height="1000" width="900"/>
        </div>
        )
    }
}
export default PromList;

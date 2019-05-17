import React, { Component } from 'react'
import IFrame from './IFrame'

class IstioG extends Component {
    state = {
		iGrafana_url: ""
	}

	constructor() {
		super()
		this.getIGrafana()
    }
 
	getIGrafana = () => {
		fetch('/api/istiografana_url')
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					iGrafana_url: responseJson.data
				})
			})
	}
 

    render() {
        return (
	    <div id="parent">
			<p>Istio Grafana URL: {this.state.iGrafana_url}</p>
			<IFrame src={this.state.iGrafana_url} height="1000" width="900"/>
        </div>
        )
    }
}
export default IstioG;

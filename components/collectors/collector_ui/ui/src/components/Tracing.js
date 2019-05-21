import React, { Component } from 'react'
import IFrame from './IFrame'

class Tracing extends Component {
    state = {
		tracing_url: ""
	}

	constructor() {
		super()
		this.getTracing()
    }
 
	getTracing = () => {
		fetch('/api/tracing_url')
			.then((response) => response.json())
			.then((responseJson) => {
				console.log("Tracing URL: " + responseJson.data)
				this.setState({
					tracing_url: responseJson.data
				})
			})
	}
 

    render() {
        return (
	    <div id="parent">
			<p>Istio Tracing (Jaeger) URL: {this.state.tracing_url}</p>
			{/* <IFrame src={this.state.tracing_url} height="1000" width="900"/> */}
			<IFrame src={this.state.tracing_url}/>
        </div>
        )
    }
}
export default Tracing;

import React, { Component } from 'react'
import IFrame from './IFrame'

class Kiali extends Component {
    state = {
		kiali_url: ""
	}

	constructor() {
		super()
		this.getKiali()
    }
 
	getKiali = () => {
		fetch('/api/kiali_url')
			.then((response) => response.json())
			.then((responseJson) => {
				this.setState({
					kiali_url: responseJson.data
				})
			})
	}
 

    render() {
        return (
	    <div id="parent">
			<p>Istio Kiali URL: {this.state.kiali_url}</p>
			{/* <IFrame src={this.state.kiali_url} height="1000" width="900"/> */}
			<IFrame src={this.state.kiali_url}/>
        </div>
        )
    }
}
export default Kiali;

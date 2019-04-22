import React, { Component } from 'react'

import Inspector from 'react-json-inspector'
import jsonStyles from 'react-json-inspector/json-inspector.css'

class Analytics extends Component {

    render() {
       return (
	     <div id="analytics">
                { this.props.data ? (
			<div id="inspector" className={jsonStyles}>
	    		<Inspector data={this.props.data}/>
			</div>
                ) : "No Data found" }
            </div>
        )
    }
}

/*
  Default Properties
*/
Analytics.defaultProps = {
    data: {},
    debug: false,
}
export default Analytics


import React, { Component } from 'react'

import Inspector from 'react-json-inspector';
import jsonStyles from 'react-json-inspector/json-inspector.css'

class Debug extends Component {
    render() {
        return (
	     <div id="parent">
                { this.props.data ? (
			<div id="inspector" className={jsonStyles}>
	    		<Inspector data={this.props.data}/>
			</div>
                ) : "No Traces found" }
            </div>
        )
    }
}

Debug.defaultProps = {
    data: {},
    debug: false,
}
export default Debug


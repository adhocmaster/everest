import React, { Component } from 'react'

class IFrame extends Component {
    render() {
        return (
	    <div id="parent">
            <iframe title="kube" src={this.props.src} height={this.props.height} width={this.props.width}/>
        </div>
        )
    }
}

/*
  Default Properties
*/
IFrame.defaultProps = {
    width: 900,
    height: 1700,
    debug: false,
    src: ''
}

export default IFrame;


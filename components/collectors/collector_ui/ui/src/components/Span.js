import React, { Component } from 'react'
import Chart from 'react-google-charts'

class SpanList extends Component {
    state = {
	data: {}
    }
    
    initData() {
	let data = []

	data.push([
	    { type: 'string', id: 'Name' },
	    { type: 'string', id: 'Label' },
	    { type: 'string', role: 'tooltip', p: {html: true} },
	    { type: 'date', id: 'Start' },
	    { type: 'date', id: 'End' },
	])

	/*
	let span = this.props.spans[0]
	let endTime = span['startTime'] + span['duration']
	data.push([span['spanID'].toString() , span['operationName'], new Date(span['startTime']), new Date(endTime)])
	*/
	
	/*
	data.push([
		'Magnolia Room',
		'Beginning JavaScript ---' + this.props.spans.length,
		new Date(0, 0, 0, 12, 0, 0),
		new Date(0, 0, 0, 13, 30, 0),
	])
	*/

	this.props.spans.forEach(span => {
	    let _endTime = span['startTime'] + span['duration']
	    let startTime = new Date(span['startTime'])
	    let endTime = new Date(_endTime)
	    let startTimeDate = new Date(Math.round(span['startTime']/1000)).toLocaleString('en-US', {
		day: 'numeric',
		month: 'numeric',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	    })
	    let toolTip = '<h5>ID: ' + span['spanID'] + '</h5><hr style="padding: 1;"/>' +
		'<p style="margin: 0;">Belong to Trace ID: ' + span['traceID'] + '</p>' +
		'<p style="margin: 0;">Start at: ' + startTimeDate + '</p>' +				
		'<p style="margin: 0;">Operation Name: ' + span['operationName'] + '</p>' + 
		'<p style="margin: 0;">Process ID:' + span['processID'] + '</p>'
	    let toolAux = '<hr/>'
	    let tags = span['tags']
	    //for (let tag of tags) {
	    //	toolAux += '<p style="margin: 0;">' + tag['key'] + ' : ' + tag['value'] + '</p>'
	    //
	    //}
	    tags.map(tag => {
		toolAux += tag['key'] + ": " + tag['value'] + " || "
		return true
	    })
	    if(span['operationName'].indexOf('tracing-') === 0) {
		toolTip = '<p style="background-color:powderblue;"><strong>&#9752; CLOVISOR Trace &#9752; </strong></p>' + toolTip +'<p style="margin: 0;">Duration: ' + span['duration'] + ' usecs</p>' + toolAux
		    
	    } else {
		toolTip = toolTip +'<p style="margin: 0;">Duration: ' + span['duration'] + ' msecs</p>' + toolAux
	    }
	    data.push([span['spanID'].toString() , span['operationName'], toolTip, startTime, endTime])
	    if(this.props.debug) {
		console.log("SPAN ID " + span['spanID'].toString() + " OP " + span['operationName'].toString() + " startTime " + span['startTime']  + " endTime " + (span['startTime'] + span['duration']) + " START_D " + startTime  + " END_D " + endTime)
	    }
	})

	//this.setState({
	//    data: data
	//})

	this.mydata = data
	//console.log(new Date() - start)
	//console.log("Finish .....")
	
    }

    
    render() {
	this.initData()
	
        return (	    
		<Chart
		  width={'100%'}
		  height={'80em'}
		  chartType="Timeline"
		  loader={<div>Loading Chart</div>}
	          data={this.mydata}
	          options={{
		    timeline: { colorByRowLabel: true },
		      backgroundColor: '#ffd',
		      allowHtml: true,
		      tooltip: {isHtml: true},
		  }}
		  rootProps={{ 'data-clover': '10' }}
		/>
	)
    }
}

/*
  Default Properties
*/
SpanList.defaultProps = {
    spans: [],
    traceID: "N/A",
    debug: false,
    processes: []
}


export default SpanList


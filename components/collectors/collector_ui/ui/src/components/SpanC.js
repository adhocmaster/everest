import React, { Component } from 'react'
import Chart from 'react-google-charts'

class SpanListC extends Component {
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
	//let startTime = new Date(span['startTime']/1000)
	let startTime = new Date(0)
	startTime.setUTCSeconds(span['startTime']/1000)
	let endTime = new Date(0)
	endTime.setUTCSeconds((span['startTime']/1000) + span['duration'])	
	if(this.props.debug) {
	    console.log("SPAN ID " + span['spanID'].toString() + " OP " + span['operationName'] + " START " + startTime  + " END " + endTime)
	}
	data.push([span['spanID'].toString() , span['operationName'].toString(), startTime, endTime])
	
	*/
	

	this.props.spans.forEach(span => {
	    let _startTimeInSeconds = Math.round(span['startTime']/1000)
	    let _endTimeInSeconds = _startTimeInSeconds + span['duration']
	    let startTime = new Date(0)
	    startTime.setUTCSeconds(_startTimeInSeconds)
	    let endTime = new Date(0)
		endTime.setUTCSeconds(_endTimeInSeconds)

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
	    tags.map(tag => {
			toolAux += tag['key'] + ": " + tag['value'] + " || "
			return true
		})
		toolTip = toolTip +'<p style="margin: 0;">Duration: ' + span['duration'] + ' usecs</p>' + toolAux


	    data.push([span['spanID'].toString(), span['operationName'].toString(), toolTip, startTime, endTime])
	    if(this.props.debug) {
		console.log("SPANC ID " + span['spanID'].toString() + " OP " + span['operationName'].toString() + " startTime -" + _startTimeInSeconds  + "- endTime -" + _endTimeInSeconds + "- START_D " + startTime  + " END_D " + endTime)
	    }
	})
	/*
	*/

	/*
	let data= [
	    [
		{ type: 'string', id: 'Name' },
		{ type: 'string', id: 'Label' },
		{ type: 'date', id: 'Start' },
		{ type: 'date', id: 'End' },
	    ],	    	    
	    [
		'Magnolia Room',
		'Beginning JavaScript ---' + this.props.spans.length,
		new Date(0, 0, 0, 12, 0, 0),
		new Date(0, 0, 0, 13, 30, 0),
	    ],
	    [
		'Magnolia Room',
		'Intermediate JavaScript ===' + this.props.traceID,
		new Date(0, 0, 0, 14, 0, 0),
		new Date(0, 0, 0, 15, 30, 0),
	    ],
	    [
		'Magnolia Room',
		'Advanced JavaScript',
		new Date(0, 0, 0, 16, 0, 0),
		new Date(0, 0, 0, 17, 30, 0),
	    ],
	    [
		'Willow Room',
		'Beginning Google Charts',
		new Date(0, 0, 0, 12, 30, 0),
		new Date(0, 0, 0, 14, 0, 0),
	    ],
	    [
		'Willow Room',
		'Intermediate Google Charts',
		new Date(0, 0, 0, 14, 30, 0),
		new Date(0, 0, 0, 16, 0, 0),
	    ],
	    [
		'Willow Room',
		'Advanced Google Charts',
		new Date(0, 0, 0, 16, 30, 0),
		new Date(0, 0, 0, 18, 0, 0),
	    ],
	]

	*/
	
	//this.setState({
	//    data: data
	//})

	this.mydata = data
	//console.log("ARRAY DATA " + JSON.stringify(this.data, null, '\t'))
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
		  }}
		  rootProps={{ 'data-testid': '6' }}
		/>
	)
    }
}

/*
  Default Properties
*/
SpanListC.defaultProps = {
    spans: [],
    traceID: "N/A",
    debug: false,
    processes: []
}


export default SpanListC


import React, { Component } from 'react'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Container from 'react-bootstrap/Container'
import Tab from 'react-bootstrap/Tab'
import Nav from 'react-bootstrap/Nav'
import NavDropdown from 'react-bootstrap/NavDropdown'
import SpanList from './Span'
import SpanListC from './SpanC'

class TraceList extends Component {
    state = {
	jaegers: {},
	serviceNodes: {},
	cServiceNodes: {},
	clover_title: "Clover + Clovisor"
	}
	
	constructor() {
		super()
		this.serviceNodes = {}
		this.cServiceNodes = {}
		this.clover_title = "Clover + Clovisor"
	}
 
    _convertJson(responseJson) {
	//console.log("Starting .....")
	//var start = new Date()

	let counter = 0
	let cloverServices = {}
	let clovisorServices = {}
	for(var jaeger_id in responseJson) {
	    if(counter === 0) {
		cloverServices = responseJson[jaeger_id]
	    } else {
		clovisorServices = responseJson[jaeger_id]		
	    }
	    counter++
	}

	Object.keys(clovisorServices).map(key => {
	    let service = clovisorServices[key]
	    for (let trace of service) {
		let spans = trace['spans']
		for (let span of spans) {
		    let tags = span['tags']
		    let openTracingReqID = ''
		    let operationName = ''
		    let traceID = ''
		    for (let tag of tags) {
			if(tag['key'] === "requestid") {
			    openTracingReqID = tag['value']
			    continue
			}
			if(tag['key'] === "envoydecorator") {
			    operationName = tag['value']
			    continue
			}
			if(tag['key'] === 'traceid') {
			    traceID = tag['value']
			    continue
			}
		    }
		    // now process clover spans
		    //console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID)
		    if(openTracingReqID !== '' && operationName !== '' && traceID !== '') {
			Object.keys(cloverServices).map(key => {
			    let serviceC = cloverServices[key]
			    for (let traceC of serviceC) {
				let spansC = traceC['spans']
				spansC.some(spanC => {
				    let foundSpan = false
				    if(spanC['operationName'] !== operationName) {
					return false
				    }
				    if(spanC['traceID'] !== traceID) {
					return false
				    }
				    let tagsC = spanC['tags']
				    let i = tagsC.length
				    while (i--) {
					if (tagsC[i]['value'] === openTracingReqID) {
					    //let references = spanC['references']
					    //console.log("BINGO ... add to clover span")
					    //if(references.length > 0) {
					    //	console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: " + spanC['references'][0]['spanID'] + " durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
					    //} else {
					    //	console.log("OpenTracing Request ID: " + openTracingReqID + " operationName: " + operationName + " traceID :" + traceID + " servicec: " + key + " spanIDc: " + spanC['spanID'] + " refc: NULL durationc: " + spanC['duration'] + " startTimec: " + spanC['startTime'] + " starttime: " + span['startTime'] + " duration: " + span['duration'])
					    //}
					    spansC.push(span)
					    foundSpan = true
					    break
					}
				    }
				    return foundSpan
				}) // some..
			    } //traceC of serviceC
			    return true
			})
		    }
		} //span of spans
	    } //trace of service
	    return true
	})//map	    

	//console.log(new Date() - start)
	//console.log("Finish .....")

	return cloverServices
	}
	

    processTraces = (jsonData) => {
		this._convertJson(jsonData)
		for(var jaeger_id in jsonData) {
		    let serviceNodes = jsonData[jaeger_id]
		    if(jaeger_id === 'Clover-Istio') {
				this.serviceNodes = serviceNodes
		    } else {
				this.cServiceNodes = serviceNodes
			}
		}
    }

    render() {
		this.processTraces(this.props.data)

        return (
	    <Tab.Container id="main-tabs" defaultActiveKey="first">
		<Row>
		<Col sm={2} style={{width: '100%', height: '40em', color: '#efefef', 'backgroundColor': 'aqua', borderColor: "blue"}}>
		<Nav variant="pills" className="flex-column">
	    
		<NavDropdown title={this.clover_title} onSelect={this.handleSelectClover} id="nav-clover">
		<NavDropdown.Header>Service</NavDropdown.Header>
                {
		    Object.keys(this.serviceNodes).map(function(key) {
			return (<NavDropdown.Item eventKey={'clover_' + key}>{key}</NavDropdown.Item>)
		    })
		}
		</NavDropdown>

	    	<NavDropdown title="Clovisor" id="nav-clovisor">
		<NavDropdown.Header>Service</NavDropdown.Header>		
                {
		    Object.keys(this.cServiceNodes).map(function(key) {
			return (<NavDropdown.Item eventKey={'clovisor_' + key}>{key}</NavDropdown.Item>)
		    })
		}
		</NavDropdown>
		</Nav>
		
		</Col>

		<Col sm={10} style={{width: '100%', height: '100%'}}>
		<Tab.Content>
		{
		    Object.keys(this.serviceNodes).map(key => {
			let service = this.serviceNodes[key]
			return(
				<Tab.Pane id={key} eventKey={'clover_' + key} style={{width: '100%', height: '40em'}}>
				
				<Tab.Container id={key + "-tabs"} defaultActiveKey={key + "_first"}>				
				<Row>
				<Col sm={12} style={{ color: '#efefef', 'backgroundColor': 'teal'}}>
				<h2>{'Clover + Clovisor:' + key}</h2>
			        </Col>
			        </Row>
				<Row>
				<Col sm={3}>
				<Nav variant="pills" className="flex-column">
				<NavDropdown title={<div style={{display: "inline-block"}}>Traces</div>} id="clover-dropdown">
				{ 
				    service.map(trace => {
					return (
						<NavDropdown.Item eventKey={trace['traceID']} >{trace['traceID']}</NavDropdown.Item>
					)
				    })
				}
			        </NavDropdown>				    
			        </Nav>
			        </Col>
				<Col sm={9}>

			        </Col>		
			    
				<Col sm={12} style={{width: '100%', height: '40em'}}>			    
				<Tab.Content>
				{
				    service.map(trace => {
					let span_numbers = trace['spans'].length
					let proc_numbers = Object.keys(trace['processes']).length
					
					return (
						<Tab.Pane eventKey={trace['traceID']}>
						<Container>

						<Row>
						<Col sm={12}>
						<h5>Trace ID: {trace['traceID']}, Number of Spans: {span_numbers}, Processes: {proc_numbers}</h5>
					        </Col>
						</Row>					    
						<Row>
						<Col sm={12}>
						<SpanList traceID={trace['traceID']} spans={trace['spans']} processes={trace['processes']} debug={false}/>
					        </Col>
						</Row>					    
					    
						</Container>
					        </Tab.Pane>
					)
				    })
				}

			         </Tab.Content>
			        </Col>				
				</Row>
				
			    </Tab.Container>
			    </Tab.Pane>
			)
		    }, this)
		}
		{
		    Object.keys(this.cServiceNodes).map(key => {
			let service = this.cServiceNodes[key]
			return(
				<Tab.Pane id={key} eventKey={'clovisor_' + key} style={{width: '100%', height: '40em'}}>
				

				<Tab.Container id={key + "-tabs"} defaultActiveKey={key + "_first"}>
				<Row>
				<Col sm={12} style={{ color: '#efefef', 'backgroundColor': 'teal'}}>
				<h2>{'Clovisor: ' + key} </h2>				
			        </Col>
			        </Row>
				<Row>
				<Col sm={3}>
				<Nav variant="pills" className="flex-column">
				<NavDropdown title={<div style={{display: "inline-block"}}>Traces</div>} id="clovisor-dropdown">
				{ 
				    service.map(trace => {
					return (
						<NavDropdown.Item eventKey={trace['traceID']} >{trace['traceID']}</NavDropdown.Item>
					)
				    })
				}
			        </NavDropdown>				    
			        </Nav>
			        </Col>
				<Col sm={9}>

			        </Col>		
				
				<Col sm={12} style={{width: '100%', height: '40em'}}>			    
				<Tab.Content>
				{
				    service.map(trace => {
					let span_numbers = trace['spans'].length
					let proc_numbers = Object.keys(trace['processes']).length
					
					return (
						<Tab.Pane eventKey={trace['traceID']}>
						<Container>

						<Row>
						<Col sm={12}>
						<h5>Trace ID: {trace['traceID']}, Number of Spans: {span_numbers}, Processes: {proc_numbers}</h5>
					        </Col>
						</Row>					    
						<Row>
						<Col sm={12}>
						<SpanListC traceID={trace['traceID']} spans={trace['spans']} processes={trace['processes']} debug={false} id="clovisor-spanlist"/>
					        </Col>
						</Row>					    
					    
						</Container>						
						</Tab.Pane>
					)
				    })
				}

 			        </Tab.Content>
			        </Col>				
				</Row>
				
			    </Tab.Container>
			    </Tab.Pane>
			)
		    }, this)
		}

	    </Tab.Content>
		
	    </Col>
            </Row>
	</Tab.Container>
		
        )
    }
}


/*
  Default Properties
*/
TraceList.defaultProps = {
	data: {},
    debug: false
}



export default TraceList


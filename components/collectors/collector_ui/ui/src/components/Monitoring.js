import React, { Component } from 'react'
import Tabs from 'react-bootstrap/Tabs'
import Tab from 'react-bootstrap/Tab'
import PromList from './Proms'
import IstioG from './Istio'
import Kiali from './Kiali'
import Tracing from './Tracing'

class Monitoring extends Component {
    render() {
        return (
		<Tabs defaultActiveKey="kube" id="mprom">
		<Tab eventKey="kube" title="Kubernetes">
			<PromList />
		</Tab>
		<Tab eventKey="kiali" title="Kiali (Service Graph)">
			<Kiali />
		</Tab>
		<Tab eventKey="tracing" title="Tracing (Jaeger)">
			<Tracing />
		</Tab>
		<Tab eventKey="istio" title="Istio">
			<IstioG />
		</Tab>
		</Tabs>
        )
    }
}

export default Monitoring


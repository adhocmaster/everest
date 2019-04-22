import React, { Component } from 'react'
import Tabs from 'react-bootstrap/Tabs'
import Tab from 'react-bootstrap/Tab'
import PromList from './Proms'
import IstioG from './Istio'

class MainProm extends Component {
    render() {
        return (
		<Tabs defaultActiveKey="kube" id="mprom">
		<Tab eventKey="kube" title="Kubernetes">
		<PromList />
		</Tab>
		<Tab eventKey="istio" title="Clover-Istio">
		<IstioG />
		</Tab>
		</Tabs>
        )
    }
}

export default MainProm;


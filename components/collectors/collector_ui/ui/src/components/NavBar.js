import React, { Component } from 'react';

import opnfvlogo from '../images/opnfv.png';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Form from 'react-bootstrap/Form';
import FormControl from 'react-bootstrap/FormControl';
import Button from 'react-bootstrap/Button';

class NavBar extends Component {

    handleSelect(eventKey) {
	//alert(`selected ${eventKey}`);
	alert('Not implemented yet');
    }
    
    render() {
	return(
		<Navbar bg="primary" variant="dark">
		<Navbar.Brand>
		<img
            src={opnfvlogo}
            width="170"
            height="100"
            className="d-inline-block align-top"
            alt="OPNFV logo" />
		</Navbar.Brand>
		<Navbar.Toggle aria-controls="basic-navbar-nav" />
		<Navbar.Collapse id="basic-navbar-nav">
		<Nav variant="pills" activeKey="1" onSelect={k => this.handleSelect(k)}>
		<Nav.Item>
		<Nav.Link eventKey="1">
		Configuration
                </Nav.Link>
		</Nav.Item>
		{/*
		<Nav.Item>
		<Nav.Link eventKey="2" disabled>
		Currently disabled
                </Nav.Link>
		</Nav.Item>
		 */}
		<NavDropdown title="Help" id="nav-dropdown">
		<NavDropdown.Item eventKey="4.1">Clover & Clovisor Help</NavDropdown.Item>
		<NavDropdown.Item eventKey="4.2">Acknowledgments</NavDropdown.Item>
		<NavDropdown.Divider />
		<NavDropdown.Item eventKey="4.3">About</NavDropdown.Item>
		</NavDropdown>
		</Nav>
		<Form inline>
		<FormControl type="text" placeholder="Search" className="mr-sm-2" />
		<Button variant="outline-success">Search</Button>
		</Form>
		</Navbar.Collapse>
		<Navbar.Text>
		<h2>Clover + Clovisor Visibility UI</h2>
	       </Navbar.Text>
	       </Navbar>
       )
   }
}

export default NavBar;


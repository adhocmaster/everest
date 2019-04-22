import React, { Component } from 'react';

import NavBar from './components/NavBar';
import MainPanel from './components/MainPanel';


class App extends Component {
    render() {
	return (
		<div id="parent">
		<NavBar />
		<MainPanel />
		</div>
    );
  }
}

export default App;

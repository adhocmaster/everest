import React from 'react'
import ReactDOM from 'react-dom'
import Analytics from '../Analytics'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<Analytics/>, div)
  ReactDOM.unmountComponentAtNode(div)
})
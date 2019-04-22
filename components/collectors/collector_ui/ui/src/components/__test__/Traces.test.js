import React from 'react'
import ReactDOM from 'react-dom'
import Traces from '../Traces'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<Traces />, div)
  ReactDOM.unmountComponentAtNode(div)
})

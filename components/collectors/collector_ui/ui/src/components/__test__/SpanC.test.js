import React from 'react'
import ReactDOM from 'react-dom'
import SpanC from '../SpanC'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<SpanC />, div)
  ReactDOM.unmountComponentAtNode(div)
})

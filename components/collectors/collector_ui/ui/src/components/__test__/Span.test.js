import React from 'react'
import ReactDOM from 'react-dom'
import Span from '../Span'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<Span />, div)
  ReactDOM.unmountComponentAtNode(div)
})

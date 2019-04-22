import React from 'react'
import ReactDOM from 'react-dom'
import Debug from '../Debug'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<Debug />, div)
  ReactDOM.unmountComponentAtNode(div)
})

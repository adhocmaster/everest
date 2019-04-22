import React from 'react'
import ReactDOM from 'react-dom'
import IFrame from '../IFrame'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<IFrame />, div)
  ReactDOM.unmountComponentAtNode(div)
})

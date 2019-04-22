import React from 'react'
import ReactDOM from 'react-dom'
import Proms from '../Proms'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<Proms />, div)
  ReactDOM.unmountComponentAtNode(div)
})

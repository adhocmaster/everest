import React from 'react'
import ReactDOM from 'react-dom'
import MainProm from '../MainProm'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<MainProm />, div)
  ReactDOM.unmountComponentAtNode(div)
})

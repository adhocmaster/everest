import React from 'react'
import ReactDOM from 'react-dom'
import MainPanel from '../MainPanel'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<MainPanel />, div)
  ReactDOM.unmountComponentAtNode(div)
})

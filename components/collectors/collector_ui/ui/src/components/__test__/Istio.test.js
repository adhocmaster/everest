import React from 'react'
import ReactDOM from 'react-dom'
import IstioG from '../Istio'

it('renders without crashing', () => {
  const div = document.createElement('div')
  ReactDOM.render(<IstioG />, div)
  ReactDOM.unmountComponentAtNode(div)
})

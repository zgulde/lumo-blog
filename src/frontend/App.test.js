import React from 'react'
import render from 'react-dom'
import { shallow } from 'enzyme'
import App from './App'

describe('<App />', () => {
  it("renders without crashing",() => {
    expect(shallow(<App />).text()).toMatch(/hello,\sworld/i)
  })
})

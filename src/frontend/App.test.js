import React from 'react'
import { shallow } from 'enzyme'
import App from './App'

describe('<App />', () => {
  it('renders without crashing', () => {
    expect(shallow(<App />).text()).toMatch(/hello,\sworld/i)
  })
})

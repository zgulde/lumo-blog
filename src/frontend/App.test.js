import React from 'react'
import { shallow, mount } from 'enzyme'
import configureMockStore from 'redux-mock-store'
import {Provider} from 'react-redux'
import App from './App'
import PostList from './components/PostList'
import initialState from './reducers'

describe('<App />', () => {
  it('renders without crashing', () => {
    let app = shallow(<App />)
    expect(app).toBeTruthy()
  })
  it.only('"mounts" a component', () => {
    let store = configureMockStore([], initialState)()
    console.log(store.getState())
    let app = mount((
      <Provider store={store}>
        <App />
      </Provider>
    ))
  })
  it.skip('contains the <PostList />', () => {
    let postList = shallow(<App />).find(<PostList />)
    /* console.log(shallow(<App />)) */
    /* expect(postList.length).toBe(1) */
    expect(postList.contains(<PostList />)).toBe(true)
  })
})

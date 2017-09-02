import React from 'react'
import { shallow } from 'enzyme'
import PostList from './PostList'
import Post from './Post'

describe('<PostList />', () => {
  it('renders without crashing', () => {
    expect(shallow(<PostList posts={[]} />)).toBeTruthy()
  })
  it('accepts posts as props and renders them', () => {
    const posts = [
      {id: 1, title: 'foo', body: 'bar'},
      {id: 2, title: 'baz', body: 'pony'},
      {id: 3, title: 'pony', body: 'quux'}
    ]
    const wrapper = shallow(<PostList posts={posts} />)
    expect(wrapper.find(Post).length).toBe(3)
  })
  it('has a class of .PostList', () => {
    const wrapper = shallow(<PostList posts={[]} />)
    expect(wrapper.hasClass('PostList')).toBe(true)
  })
})

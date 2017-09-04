import React from 'react'
import { shallow } from 'enzyme'
import {PostList, mapStateToProps, mapDispatchToProps} from './PostList'
import Post from './Post'
import {initialState} from '../reducers'
import {merge} from '../util'

jest.mock('../api', () => ({
  getPosts: () => Promise.resolve([{title: 'foo', body: 'bar'}])
}))


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
  it('has a class of .pending when requestPending is passed', () => {
    const wrapper = shallow(<PostList requestPending />)
    expect(wrapper.hasClass('pending')).toBe(true)
  })
  it('shows an error when network errors are present', () => {
    const wrapper = shallow(<PostList networkError='Internal Server Error' />)
    expect(wrapper.find('h2').text()).toMatch(/went wrong/)
  })
})

describe('mapStateToProps', () => {
  it('should be defined', () => {
    expect(mapStateToProps).toBeDefined()
  })
  it('should provide the posts property', () => {
    const posts = [{title: 'foo', body: 'bar', user_id: 1}]
    const state = {...initialState, posts}
    expect(mapStateToProps(state).posts).toEqual(posts)
  })
  it('should provide a networkError property', () => {
    const state = merge(initialState, {errors: {network: {posts: 'error'}}})
    expect(mapStateToProps(state).networkError).toBe('error')
  })
  it('should provice the requestPending property', () => {
    const state = merge(initialState, {pendingRequests: {posts: true}})
    expect(mapStateToProps(state).requestPending).toBe(true)
  })
})

describe('mapDispatchToProps', () => {
  it('provides a method for fetching posts', () => {
    const dispatch = jest.fn()
    const props = mapDispatchToProps(dispatch)
    expect(props.fetchPosts).toBeDefined()
    expect(typeof props.fetchPosts).toBe('function')
  })
  it('fetchPosts is called if the posts prop is null', () => {
    const fetchPosts = jest.fn()
    const list = shallow(<PostList posts={null} fetchPosts={fetchPosts} />)
    expect(fetchPosts).toHaveBeenCalled()
  })
  it('fetchPosts dipatches the FETCH_POSTS action', () => {
    const dispatch = jest.fn()
    const {fetchPosts} = mapDispatchToProps(dispatch)
    fetchPosts()
    expect(dispatch).toHaveBeenCalled()
    const arg = dispatch.mock.calls[0][0]
    expect(arg.type).toBe('FETCH_POSTS')
  })
})

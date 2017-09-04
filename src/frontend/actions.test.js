import * as actions from './actions'

jest.mock('./api', () => ({
  // eslint-disable-next-line no-unused-vars
  login: ({email, password}) => Promise.resolve({success: true}),
  getPosts: () => Promise.resolve([{title: 'foo', body: 'bar'}])
}))

describe('login', () => {
  it('creates a login action', () => {
    const action = actions.login()
    expect(action.type).toBe('LOGIN')
    return expect(action.payload).resolves.toHaveProperty('success')
  })
})

describe('logout', () => {
  it('creates a logout action', () => {
    expect(actions.logout()).toEqual({
      type: 'LOGOUT'
    })
  })
})

describe('fetchPosts', () => {
  it('should be defined', () => {
    expect(actions.fetchPosts).toBeDefined()
  })
  it('should have a type of FETCH_POSTS', () => {
    const action = actions.fetchPosts()
    expect(action.type).toBe('FETCH_POSTS')
  })
  it('has a payload that resolves to a list of posts', () => {
    const action = actions.fetchPosts()
    return action.payload.then(posts => {
      expect(Array.isArray(posts)).toBe(true)
      expect(posts.every(post => {
        return typeof post.title !== 'undefined'
          && typeof post.body !== 'undefined'
      })).toBe(true)
    })
  })
})

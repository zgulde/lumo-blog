import * as actions from './actions'

jest.mock('./api', () => ({
  // eslint-disable-next-line no-unused-vars
  login: ({email, password}) => Promise.resolve({success: true})
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

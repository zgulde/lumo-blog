import * as actions from './actions'

describe('login', () => {
  it('creates a login action', () => {
    expect(actions.login()).toEqual({
      type: 'LOGIN'
    })
  })
})

describe('logout', () => {
  it('creates a logout action', () => {
    expect(actions.logout()).toEqual({
      type: 'LOGOUT'
    })
  })
})

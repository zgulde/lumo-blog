import {rootReducer} from './reducers'
import * as actions from './actions'

describe('rootReducer', () => {
  it('returns the default state with an empty action', () => {
    expect(rootReducer(undefined, {})).toEqual({
      loggedIn: false
    })
  })
  it('handles the login action', () => {
    expect(rootReducer(undefined, actions.login())).toEqual({
      loggedIn: true
    })
  })
  it('handles the logout function', () => {
    expect(rootReducer({loggedIn: true}, actions.logout())).toEqual({
      loggedIn: false
    })
  })
})

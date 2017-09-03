import {rootReducer, initialState} from './reducers'
import {merge} from './util'

describe('rootReducer', () => {
  it('returns the default state with an empty action', () => {
    expect(rootReducer(undefined, {})).toEqual(initialState)
  })
})
describe('LOGIN actions', () => {
  const loginSuccess = {type: 'LOGIN_FULFILLED', payload: {success: true}}
  const loginFailed = {type: 'LOGIN_FULFILLED', payload: {success: false}}
  it('handles _PENDING', () => {
    const state = rootReducer(initialState, {type: 'LOGIN_PENDING'})
    expect(state.pendingRequests.login).toBe(true)
  })
  it('request is no longer pending on _FULFILLED or _REJECTED', () => {
    const initial = merge(initialState, {pendingRequests: {login: true}})
    expect(initial.pendingRequests.login).toBe(true)
    const state = rootReducer(initial, loginSuccess)
    expect(state.pendingRequests.login).toBe(false)
  })
  it('sets the loggedIn key based off of the payload of _FULFILLED', () => {
    let state = rootReducer(initialState, loginSuccess)
    expect(state.loggedIn).toBe(true)
    state = rootReducer(initialState, loginFailed)
    expect(state.loggedIn).toBe(false)
  })
})

describe('LOGOUT actions', () => {
  it('handles _PENDING', () => {
    const state = rootReducer(initialState, {type: 'LOGOUT_PENDING'})
    expect(state.pendingRequests.logout).toBe(true)
  })
  it('request is no longer pending on _FULFILLED or _REJECTED', () => {
    const initial = merge(initialState, {pendingRequests: {logout: true}})
    expect(initial.pendingRequests.logout).toBe(true)
    const state = rootReducer(initial, {type: 'LOGOUT_FULFILLED'})
    expect(state.pendingRequests.logout).toBe(false)
  })
})

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
  it('Clears network errors on _PENDING', () => {
    const initial = merge(initialState, {errors: {network: {login: 'error'}}})
    const state = rootReducer(initial, {type: 'LOGIN_PENDING'})
    expect(state.errors.network.login).toBe(null)
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
  it('Clears network errors on _PENDING', () => {
    const initial = merge(initialState, {errors: {network: {logout: 'error'}}})
    const state = rootReducer(initial, {type: 'LOGOUT_PENDING'})
    expect(state.errors.network.logout).toBe(null)
  })
  it('request is no longer pending on _FULFILLED or _REJECTED', () => {
    const initial = merge(initialState, {pendingRequests: {logout: true}})
    expect(initial.pendingRequests.logout).toBe(true)
    const state = rootReducer(initial, {type: 'LOGOUT_FULFILLED'})
    expect(state.pendingRequests.logout).toBe(false)
  })
})

describe('FETCH_POSTS', () => {
  it('handles _PENDING', () => {
    const state = rootReducer(initialState, {type: 'FETCH_POSTS_PENDING'})
    expect(state.pendingRequests.posts).toBe(true)
  })
  it('Clears network errors on _PENDING', () => {
    const initial = merge(initialState, {errors: {network: {posts: 'error'}}})
    const state = rootReducer(initial, {type: 'FETCH_POSTS_PENDING'})
    expect(state.errors.network.posts).toBe(null)
  })
  it('handles _REJECTED', () => {
    const action = {type: 'FETCH_POSTS_REJECTED', payload: 'Network Error'}
    const state = rootReducer(initialState, action)
    expect(state.errors.network.posts).not.toBe(null)
    expect(state.errors.network.posts).toBe(action.payload)
    expect(state.pendingRequests.posts).toBe(false)
  })
  it('handles _FULFILLED', () => {
    const action = {type: 'FETCH_POSTS_FULFILLED', payload: []}
    const state = rootReducer(initialState, action)
    expect(state.errors.network.posts).toBe(null)
    expect(state.pendingRequests.posts).toBe(false)
    expect(Array.isArray(state.posts)).toBe(true)
    expect(state.posts).toEqual(action.payload)
  })
})

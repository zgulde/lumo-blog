import {merge} from './util'

export const initialState = {
  loggedIn: false,
  pendingRequests: {
    login: false,
    logout: false,
    posts: false
  }
}

export const rootReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'LOGIN_PENDING':
      return merge(state, {pendingRequests: {login: true}})
    case 'LOGIN_FULFILLED':
    case 'LOGIN_REJECTED':
      return merge(state, {
        pendingRequests: {login: false},
        loggedIn: action.payload.success
      })
    case 'LOGOUT_PENDING':
      return merge(state, {pendingRequests: {logout: true}})
    case 'LOGOUT_FULFILLED':
    case 'LOGOUT_REJECTED':
      return merge(state, {pendingRequests: {logout: false}})
    default:
      return state
  }
}

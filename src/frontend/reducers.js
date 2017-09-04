import {merge} from './util'

export const initialState = {
  loggedIn: false,
  pendingRequests: {
    login: false,
    logout: false,
    posts: false,
  },
  errors: {
    network: {
      login: null,
      logout: null,
      posts: null
    },
  },
  posts: null
}

export const rootReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'FETCH_POSTS_PENDING':
      return merge(state, {
        pendingRequests: {posts: true},
        errors: {network: {posts: null}}
      })
    case 'FETCH_POSTS_REJECTED':
      return merge(state, {
        pendingRequests: {posts: false},
        errors: {network: {posts: action.payload}}
      })
    case 'FETCH_POSTS_FULFILLED':
      return merge(state, {
        pendingRequests: {posts: false},
        posts: action.payload
      })
    case 'LOGIN_PENDING':
      return merge(state, {
        pendingRequests: {login: true},
        errors: {network: {login: null}}
      })
    case 'LOGIN_FULFILLED':
    case 'LOGIN_REJECTED':
      return merge(state, {
        pendingRequests: {login: false},
        loggedIn: action.payload.success
      })
    case 'LOGOUT_PENDING':
      return merge(state, {
        pendingRequests: {logout: true},
        errors: {network: {logout: null}}
      })
    case 'LOGOUT_FULFILLED':
    case 'LOGOUT_REJECTED':
      return merge(state, {pendingRequests: {logout: false}})
    default:
      return state
  }
}

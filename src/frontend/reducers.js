import {merge} from './util'
import {createStore, applyMiddleware} from 'redux'
import promiseMiddleware from 'redux-promise-middleware'

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

// see http://redux.js.org/docs/advanced/Middleware.html
const logger = store => next => action => {
  console.group(action.type) // eslint-disable-line no-console
  console.info('dispatching', action) // eslint-disable-line no-console
  let result = next(action)
  console.log('next state', store.getState()) // eslint-disable-line no-console
  console.groupEnd(action.type) // eslint-disable-line no-console
  return result
}

export const configureStore = () => createStore(
  rootReducer,
  initialState,
  applyMiddleware(logger, promiseMiddleware())
)

import api from './api'

export const login = (email, password) => ({
  type: 'LOGIN',
  payload: api.login({email, password})
})
export const logout = () => ({type: 'LOGOUT'})

export const fetchPosts = () => ({
  type: 'FETCH_POSTS',
  payload: api.getPosts()
})

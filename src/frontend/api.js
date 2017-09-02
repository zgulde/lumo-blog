import 'isomorphic-fetch'
import env from './.env.js'

export default {
  getPosts() {
    const url = `${env.baseUrl}/posts`
    return fetch(url).then((response) => {
      if (response.ok) {
        return response.json()
      } else {
        throw new Error(response.statusText)
      }
    })
  },
  createPost(post) {
    return fetch(`${env.baseUrl}/posts`, {
      method: 'POST',
      body: JSON.stringify(post)
    }).then((response) => {
      if (response.status === 200) {
        return response.json().then(post => ({
          success: true,
          post
        }))
      } else if (response.status === 422) {
        return response.json().then(errors => ({success: false, errors}))
      } else {
        throw new Error(`${response.status}: ${response.statusText}`)
      }
    })
  },
  login({email, password}) {
    return fetch(`${env.baseUrl}/login`, {
      method: 'POST',
      body: JSON.stringify({email, password})
    }).then(response => response.json()).then((response) => {
      return response.success ? response : {success: false}
    })
  }
}

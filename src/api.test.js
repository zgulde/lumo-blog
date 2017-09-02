import api from './api'
import nock from 'nock'
import env from './.env'
import {merge} from './util'

it('should be defined', () => {
  expect(api).toBeDefined()
})

describe('api#getPosts', () => {
  it('should fetch posts', async (done) => {
    nock(env.baseUrl).get('/posts').reply(200, [{title: 'foo', body: 'bar'}])
    api.getPosts().then(
      posts => {
        expect(Array.isArray(posts)).toBe(true)
        done()
      },
      error => {
        console.error(error)
        done()
      })
  })
  it('rejects on a 500 response', () => {
    nock(env.baseUrl).get('/posts').reply(500, {error: 'Internal Server Error'})
    return expect(api.getPosts()).rejects.toBeDefined()
  })
  it('rejects on a non-200 response', () => {
    nock(env.baseUrl).get('/posts').reply(400, {error: 'Bad Request'})
    return expect(api.getPosts()).rejects.toBeDefined()
  })
})

describe('api#createPost', () => {
  const post = {title: 'foo', body: 'bar'}
  it('should be defined', () => {
    expect(api.createPost).toBeDefined()
  })
  it('submits a POST request to /posts', () => {
    const scope = nock(env.baseUrl)
      .post('/posts')
      .reply(200, Object.assign(post, {user_id: 1}))
    return api.createPost(post).then(() => {
      return expect(scope.isDone()).toBe(true)
    })
  })
  it('submits the passed title and body in the request', () => {
    const scope = nock(env.baseUrl)
      .post('/posts', post)
      .reply(200, Object.assign(post, {user_id: 1}))
    return api.createPost(post).then(() => {
      return expect(scope.isDone()).toBe(true)
    })
  })
  it('returns an object with errors on a 422 response', () => {
    const errors = {
      title: 'title must be present',
      body: 'body must be at least 6 characters'
    }
    const scope = nock(env.baseUrl)
      .post('/posts', post)
      .reply(422, errors)
    return api.createPost(post).then(result => {
      return Promise.all([
        expect(result.success).toBe(false),
        expect(result.errors).toBeDefined(),
        expect(result.errors.title).toBe(errors.title),
        expect(result.errors.body).toBe(errors.body)
      ])
    })
  })
  it('rejects on a 500 response', () => {
    const scope = nock(env.baseUrl)
      .post('/posts', post)
      .reply(500)
    return expect(api.createPost(post)).rejects.toBeDefined()
  })
  it('returns an object with the new post if everything goes well', () => {
    const scope = nock(env.baseUrl)
      .post('/posts', post)
      .reply(200, merge(post, {user_id: 1}))
    return expect(api.createPost(post)).resolves.toEqual(
      {success: true, post: merge(post, {user_id: 1})}
    )
  })
})

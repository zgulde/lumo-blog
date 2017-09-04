import api from './api'
import nock from 'nock'
import env from './.env'

it('should be defined', () => {
  expect(api).toBeDefined()
})

describe('api#getPosts', () => {
  it('should fetch posts', (done) => {
    nock(env.baseUrl).get('/posts').reply(200, [{title: 'foo', body: 'bar'}])
    api.getPosts().then(
      posts => {
        expect(Array.isArray(posts)).toBe(true)
        done()
      },
      error => {
        // eslint-disable-next-line no-console
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
  it('has a content-type header of "application/json"', () => {
    const reqheaders = {'content-type': 'application/json'}
    const scope = nock(env.baseUrl, {reqheaders})
      .post('/posts')
      .reply(200, Object.assign(post, {user_id: 1}))
    return api.createPost(post).then(() =>
      expect(scope.isDone()).toBe(true))
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
    nock(env.baseUrl)
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
    nock(env.baseUrl)
      .post('/posts', post)
      .reply(500)
    return expect(api.createPost(post)).rejects.toBeDefined()
  })
  it('returns an object with the new post if everything goes well', () => {
    nock(env.baseUrl)
      .post('/posts', post)
      .reply(200, {...post, user_id: 1})
    return expect(api.createPost(post)).resolves.toEqual(
      {success: true, post: {...post, user_id: 1}}
    )
  })
})

describe('api#login', () => {
  const user = {email: 'test@gmail.com', password: 'pass'}
  it('should be defined', () => {
    expect(api.login).toBeDefined()
    expect(typeof api.login).toBe('function')
  })
  it('submits a POST request to /login', () => {
    const ctx = nock(env.baseUrl).post('/login').reply(200, user)
    return api.login({email: '', password: ''}).then(() => expect(ctx.isDone()).toBe(true))
  })
  it('has a contnet-type header of "application/json"', () => {
    const config = {reqheaders: {'content-type': 'application/json'}}
    const ctx = nock(env.baseUrl, config).post('/login').reply(200, user)
    return api.login({email: '', password: ''}).then(() => expect(ctx.isDone()).toBe(true))
  })
  it('submits an email and password', () => {
    const ctx = nock(env.baseUrl).post('/login', user).reply(200, user)
    return api.login(user).then(() => expect(ctx.isDone()).toBe(true))
  })
  it('returns an object with a success property when login is good', () => {
    const response = {success: true}
    nock(env.baseUrl).post('/login', user).reply(200, response)
    return api.login(user).then((response) => {
      expect(response.success).toBeDefined()
      expect(response.success).toBe(true)
    })
  })
  it('returns an object with {success: false} when login fails', () => {
    const response = {error: 'Invalid Username or Password'}
    nock(env.baseUrl).post('/login', user).reply(200, response)
    return api.login(user).then((response) => {
      expect(response.success).toBeDefined()
      expect(response.success).toBe(false)
    })
  })
})

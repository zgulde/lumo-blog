import {merge} from './util'

describe('merge(...objects)', () => {
  it('Should be defined', () => {
    expect(merge).toBeDefined()
  })
  it('Merges multiple objects', () => {
    expect(merge({foo: 'bar'}, {baz: 'pony'}, {pony: 'quux'})).toEqual({
      foo: 'bar',
      baz: 'pony',
      pony: 'quux'
    })
  })
  it('Uses the last passed object if there are duplicate keys', () => {
    expect(merge({foo: 'bar'}, {foo: 'pony'})).toEqual({
      foo: 'pony'
    })
  })
  it('Does not mutate the passed objects (Object.assign mutates the first argument)', () => {
    const object1 = {foo: 'bar'}
    const object2 = {baz: 'pony'}
    merge(object1, object2)
    expect(object1).toEqual({foo: 'bar'})
    expect(object2).toEqual({baz: 'pony'})
  })
  it('Handles nested objects', () => {
    const initial = {nested: {foo: 'bar', baz: 'pony'}}
    const mergeMe = {nested: {baz: 'quux'}}
    expect(merge(initial, mergeMe)).toEqual({
      nested: {foo: 'bar', baz: 'quux'}
    })
  })
})

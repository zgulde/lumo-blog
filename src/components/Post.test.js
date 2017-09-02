import React from 'react'
import Post from './Post'
import { shallow } from 'enzyme'

describe('<Post />', () => {
  it('Has a class name of ".Post"', () => {
    expect(shallow(<Post />).hasClass('Post')).toBe(true)
  })
  it('renders the title as an h2', () => {
    const title = 'foo'
    expect(shallow(<Post title={title} />).contains(<h2>{title}</h2>)).toBe(true)
  })
  it('renders the body in a p tag', () => {
    const body = 'lorem ipsum et cetera'
    expect(shallow(<Post body={body} />).contains(<p>{body}</p>)).toBe(true)
  })
})

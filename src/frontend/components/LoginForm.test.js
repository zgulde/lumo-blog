import React from 'react'
import { shallow } from 'enzyme'
import { LoginForm, mapDispatchToProps } from './LoginForm'

describe('<LoginForm />', () => {
  it('renders with a class of .LoginForm', () => {
    expect(shallow(<LoginForm />).hasClass('LoginForm')).toBe(true)
  })
  it('contains inputs for email and password', () => {
    const form = shallow(<LoginForm />)
    expect(form.find('input[name="email"]').length).toBe(1)
    expect(form.find('input[name="password"]').length).toBe(1)
  })
  it('Calls the passed handler when submitted', () => {
    const handleSubmit = jest.fn()
    const form = shallow(<LoginForm handleSubmit={handleSubmit} />)
    form.simulate('submit')
    expect(handleSubmit).toHaveBeenCalled()
  })
  it('keeps the email and password in it\'s state', () => {
    const user = {email: 'test@gmail.com', password: 'password'}
    const form = shallow(<LoginForm />)

    const emailEvent = {target: {name: 'email', value: user.email}}
    form.find('input[name="email"]').simulate('change', emailEvent)
    const passEvent = {target: {name: 'password', value: user.password}}
    form.find('input[name="password"]').simulate('change', passEvent)

    expect(form.state()).toEqual({
      email: user.email,
      password: user.password
    })
  })
  it('calls the submit handler and passes the email and password', () => {
    const handleSubmit = jest.fn()
    const user = {email: 'test@gmail.com', password: 'password'}
    const form = shallow(<LoginForm handleSubmit={handleSubmit} />)

    const emailEvent = {target: {name: 'email', value: user.email}}
    form.find('input[name="email"]').simulate('change', emailEvent)
    const passEvent = {target: {name: 'password', value: user.password}}
    form.find('input[name="password"]').simulate('change', passEvent)

    form.simulate('submit')
    expect(handleSubmit).toHaveBeenCalledWith(user)
  })
  it('renders an error message if passed', () => {
    const form = shallow(<LoginForm error='Invalid email or password' />)
    expect(form.find('.alert').length).toBe(1)
  })
  it('renders without an error message if none is passed', () => {
    const form = shallow(<LoginForm />)
    expect(form.find('.alert').length).toBe(0)
  })
  it('renders with a "pending" class if prop is passed', () => {
    const pendingForm = shallow(<LoginForm requestPending />)
    expect(pendingForm.hasClass('pending')).toBe(true);
    const form = shallow(<LoginForm />)
    expect(form.hasClass('pending')).toBe(false);
  })
  it('shows an error when networkError is present', () => {
    const form = shallow(<LoginForm networkError='Internal Server Error' />)
    expect(form.find('h2').text()).toMatch(/went wrong/i)
  })
})

describe('mapDispatchToProps', () => {
  it('should be defined', () => {
    expect(mapDispatchToProps).toBeDefined()
  })
  it('provides a handleSubmit function', () => {
    const dispatch = jest.fn()
    const props = mapDispatchToProps(dispatch)
    expect(props.handleSubmit).toBeDefined()
  })
})

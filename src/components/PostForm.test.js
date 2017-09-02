import React from 'react'
import PostForm from './PostForm'
import { shallow } from 'enzyme'

describe('<PostForm />', () => {
  it('Renders and has a class of .PostForm', () => {
    expect(shallow(<PostForm />).hasClass('PostForm')).toBe(true)
  })
  it('has inputs for title and body', () => {
    expect(shallow(<PostForm />).find('input[name="title"]').length).toBe(1)
    expect(shallow(<PostForm />).find('input[name="body"]').length).toBe(1)
  })
  it('keeps the title and body in it\'s state', () => {
    const form = shallow(<PostForm />)

    const titleEvent = {target: {value: 'some title', name: 'title'}}
    form.find('input[name="title"]').simulate('change', titleEvent)
    const bodyEvent = {target: {value: 'some body', name: 'body'}}
    form.find('input[name="body"]').simulate('change', bodyEvent)

    const state = form.state()
    expect(state.title).toBe('some title')
    expect(state.body).toBe('some body')
  })
  it('calls the handleSubmit prop when submitted', () => {
    const handleSubmit = jest.fn()
    const wrapper = shallow(<PostForm handleSubmit={handleSubmit} />)
    wrapper.simulate('submit')
    expect(handleSubmit.mock.calls.length).toBe(1)
  })
  it('passes the title and body from it\'s state when submitted', () => {
    const handleSubmit = jest.fn()
    const form = shallow(<PostForm handleSubmit={handleSubmit} />)

    const titleEvent = {target: {value: 'some title', name: 'title'}}
    form.find('input[name="title"]').simulate('change', titleEvent)
    const bodyEvent = {target: {value: 'some body', name: 'body'}}
    form.find('input[name="body"]').simulate('change', bodyEvent)

    form.simulate('submit')
    expect(handleSubmit.mock.calls.length).toBe(1)
    expect(handleSubmit.mock.calls[0][0]).toEqual({
      title: 'some title',
      body: 'some body'
    })

  })
  it('shows an alert when passed errors', () => {
    const errors = {title: ['title must be present'], body: ['body must be present']}
    const form = shallow(<PostForm errors={errors} />)
    expect(form.find('.alert').length).toBe(1)
  })
  it('has the text of the errors in the alert', () => {
    const errors = {title: ['title must be present'], body: ['body must be present']}
    const form = shallow(<PostForm errors={errors} />)
    expect(form.find('.alert').length).toBe(1)
  })
  it('no alert is shown if errors are not passed', () => {
    const errors = {title: ['title must be present'], body: ['body must be present']}
    const form = shallow(<PostForm errors={errors} />)
    expect(form.find('.alert').text()).toMatch(/title must be present/)
    expect(form.find('.alert').text()).toMatch(/body must be present/)
  })
  it('highlights title input if it has an error', () => {
    const errors = {title: ['title must be present'], body: ['body must be present']}
    const form = shallow(<PostForm errors={errors} />)
    const titleInput = form.find('input[name="title"]')
    expect(titleInput.parent().hasClass('has-error')).toBe(true)
  })
  it('highlights the body input if it has an error', () => {
    const errors = {title: ['title must be present'], body: ['body must be present']}
    const form = shallow(<PostForm errors={errors} />)
    const bodyInput = form.find('input[name="body"]')
    expect(bodyInput.parent().hasClass('has-error')).toBe(true)
  })
  it('renders with a class of "pending" if requestPending prop is passed', () => {
    const pendingForm = shallow(<PostForm requestPending={true} />)
    expect(pendingForm.hasClass('pending')).toBe(true)
    const notPendingForm = shallow(<PostForm requestPending={false} />)
    expect(notPendingForm.hasClass('pending')).toBe(false)
  })
})

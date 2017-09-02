import React, { Component } from 'react'
import PropTypes from 'prop-types'

class PostForm extends Component {
  constructor(props) {
    super(props)
    this.state = {
      title: '',
      body: ''
    }
    this.handleChange = this.handleChange.bind(this)
    this.onSubmit = this.onSubmit.bind(this)
  }

  handleChange(e) {
    this.setState({
      [e.target.name]: e.target.value
    })
  }

  onSubmit() {
    const {title, body} = this.state
    this.props.handleSubmit({title, body})
  }

  render() {
    return (
      <form className={'PostForm' + (this.props.requestPending ? ' pending' : '')} onSubmit={this.onSubmit}>
        {this.props.errors &&
          <div className='alert alert-danger'>
            <ul>
              {Object.keys(this.props.errors)
                .map(k => this.props.errors[k])
                .reduce((acc, a) => acc.concat(a), [])
                .map((error, i) => <li key={i}>{error}</li>)}
            </ul>
          </div>}
        <div className={'form-group ' + (this.errors && this.errors.title) ? 'has-error' : ''}>
          <label htmlFor="title">Title</label>
          <input onChange={this.handleChange} className="form-control" type="text" id="title" name="title" />
        </div>
        <div className={'form-group ' + (this.errors && this.errors.title) ? 'has-error' : ''}>
          <label htmlFor="body">Content</label>
          <input onChange={this.handleChange} className="form-control" type="text" id="body" name="body" />
        </div>
      </form>
    )
  }
}

PostForm.propTypes = {
  errors: PropTypes.object,
  handleSubmit: PropTypes.func,
  requestPending: PropTypes.bool
}

export default PostForm


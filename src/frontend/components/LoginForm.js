import React from 'react'
import { connect } from 'react-redux'
import * as actions from '../actions'

export class LoginForm extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      email: '',
      password: ''
    }
    this.handleChange = this.handleChange.bind(this)
    this.handleSubmit = this.handleSubmit.bind(this)
  }

  handleChange(e) {
    this.setState({
      [e.target.name]: e.target.value
    })
  }

  handleSubmit(e) {
    e.preventDefault()
    const {email, password} = this.state
    this.props.handleSubmit({email, password})
  }

  render() {
    if (this.props.networkError) {
      return <h2>We're sorry, something went wrong.</h2>
    }
    return (
      <form className={'LoginForm' + (this.props.requestPending ? ' pending' : '')} onSubmit={this.handleSubmit}>
        {this.props.error &&
          <div className='alert alert-danger'>
            {this.props.error}
          </div>}
        <div className='form-group'>
          <label htmlFor='email'>Email</label>
          <input onChange={this.handleChange} className='form-control' type='text' id='email' name='email' />
        </div>
        <div className='form-group'>
          <label htmlFor='password'>Password</label>
          <input onChange={this.handleChange} className='form-control' type='password' id='password' name='password' />
        </div>
        <input className='btn btn-block btn-primary' type='submit' value='Submit' />
      </form>
    )
  }
}

export const mapDispatchToProps = (dispatch) => ({
  handleSubmit: ({email, password}) => dispatch(actions.login({email, password}))
})

export default connect(null, mapDispatchToProps)(LoginForm)

import React from 'react'
import Post from './Post'
import PropTypes from 'prop-types'
import * as actions from '../actions'
import {connect} from 'react-redux'

export const PostList = ({posts, networkError, requestPending, fetchPosts}) => {
  if (networkError) {
    return (
      <div>
        <h2>We're sorry, something went wrong!</h2>
      </div>
    )
  }

  if (posts === null) {
    fetchPosts()
    posts = []
  }

  return (
    <div className={'PostList' + (requestPending ? ' pending' : '')}>
      {posts.map((post) => <Post key={post.id} {...post} />)}
    </div>
  )
}

PostList.defaultProps = {
  posts: []
}

PostList.propTypes = {
  posts: PropTypes.array
}

export const mapStateToProps = ({posts, errors, pendingRequests}) => ({
  posts,
  networkError: errors.network.posts,
  requestPending: pendingRequests.posts
})

export const mapDispatchToProps = (dispatch) => ({
  fetchPosts: () => dispatch(actions.fetchPosts())
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PostList)

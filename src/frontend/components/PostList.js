import React from 'react'
import Post from './Post'

const PostList = ({posts, networkError}) => {
  if (networkError) {
    return (
      <div>
        <h2>We're sorry, something went wrong!</h2>
      </div>
    )
  }
  return (
    <div className='PostList'>
      {posts.map((post) => <Post key={post.id} {...post} />)}
    </div>
  )
}

export default PostList

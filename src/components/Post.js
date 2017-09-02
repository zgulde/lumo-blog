import React from 'react'

const Post = ({title, body}) => (
  <div className='Post'>
    <h2>{title}</h2>
    <p>{body}</p>
  </div>
)

export default Post

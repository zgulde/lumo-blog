import React from 'react'
import Post from './Post'

const PostList = ({posts}) => (
  <div className='PostList'>
    {posts.map((post) => <Post key={post.id} {...post} />)}
  </div>
)

export default PostList

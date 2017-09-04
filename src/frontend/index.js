import React from 'react'
import {render} from 'react-dom'
import App from './App'
import {Provider} from 'react-redux'
import {configureStore} from './reducers'

const store = configureStore()

render(
  (
    <Provider store={store}>
      <App />
    </Provider>
  ),
  document.querySelector('#root')
)

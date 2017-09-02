export const initialState = {
  loggedIn: false
}

export const rootReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'LOGIN':
      return Object.assign({}, state, {loggedIn: true})
    case 'LOGOUT':
      return Object.assign({}, state, {loggedIn: false})
    default:
      return state
  }
}

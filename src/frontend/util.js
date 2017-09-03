import _ from 'lodash'

export const merge = (...objects) => {
  return _.merge({}, ...objects)
}

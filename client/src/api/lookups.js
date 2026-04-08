import api from './axios.js'

export async function getLookups() {
  const { data } = await api.get('/api/lookups')
  return data
}

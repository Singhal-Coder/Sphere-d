import api from './axios.js'

export async function getUsers(page = 0, size = 20) {
  const { data } = await api.get('/users', { params: { page, size } })
  return data
}

export async function getUserById(id) {
  const { data } = await api.get(`/users/${encodeURIComponent(id)}`)
  return data
}

export async function createUser(dto) {
  const { data } = await api.post('/users', dto)
  return data
}

export async function updateUser(id, dto) {
  const { data } = await api.patch(`/users/${encodeURIComponent(id)}`, dto)
  return data
}

export async function deleteUser(id) {
  const { data } = await api.delete(`/users/${encodeURIComponent(id)}`)
  return data
}

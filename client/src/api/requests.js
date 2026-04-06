import api from './axios.js'

export async function getRequests(page = 0, size = 50) {
  const { data } = await api.get('/assets/requests', { params: { page, size } })
  return data
}

export async function getRequestById(id) {
  const { data } = await api.get(`/assets/requests/${encodeURIComponent(id)}`)
  return data
}

export async function createRequest(dto) {
  const { data } = await api.post('/assets/requests', dto)
  return data
}

export async function updateRequest(id, dto) {
  const { data } = await api.patch(`/assets/requests/${encodeURIComponent(id)}`, dto)
  return data
}

export async function deleteRequest(id) {
  const { data } = await api.delete(`/assets/requests/${encodeURIComponent(id)}`)
  return data
}

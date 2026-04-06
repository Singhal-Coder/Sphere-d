import api from './axios.js'

/** @param {string} department - e.g. IT, PROJECT, CULTURE */
export async function getSeatsByDepartment(department) {
  const { data } = await api.get('/seats', { params: { department } })
  return data
}

export async function createSeat(dto) {
  const { data } = await api.post('/seats', dto)
  return data
}

export async function deleteSeat(seatId) {
  const { data } = await api.delete(`/seats/${encodeURIComponent(seatId)}`)
  return data
}

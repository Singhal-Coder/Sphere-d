import api from './axios.js'

export async function getBookings(page = 0, size = 50, date) {
  const { data } = await api.get('/seats/bookings', {
    params: { page, size, date },
  })
  return data
}

export async function getBookingById(id) {
  const { data } = await api.get(`/seats/bookings/${encodeURIComponent(id)}`)
  return data
}

export async function createBooking(seatId, bookedDate) {
  const { data } = await api.post(
    `/seats/bookings/${encodeURIComponent(seatId)}`,
    { bookedDate },
  )
  return data
}

export async function cancelBooking(bookingId) {
  const { data } = await api.patch(
    `/seats/bookings/${encodeURIComponent(bookingId)}/cancel`,
  )
  return data
}

export async function deleteBookings(params) {
  const { data } = await api.delete('/seats/bookings', { params })
  return data
}

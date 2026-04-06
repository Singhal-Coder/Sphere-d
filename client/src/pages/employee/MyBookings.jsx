import { useCallback, useEffect, useMemo, useState } from 'react'
import { cancelBooking, getBookings } from '../../api/bookings.js'
import { getPageContent } from '../../api/hal.js'
import Badge from '../../components/ui/Badge.jsx'
import Spinner from '../../components/ui/Spinner.jsx'
import { useAuth } from '../../context/AuthContext.jsx'

function yesterdayYmd() {
  const d = new Date()
  d.setDate(d.getDate() - 1)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function todayYmd() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

export default function MyBookings() {
  const { user } = useAuth()
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [cancelling, setCancelling] = useState(null)

  const listDate = useMemo(() => yesterdayYmd(), [])

  const load = useCallback(async () => {
    setLoading(true)
    setErr(null)
    try {
      const res = await getBookings(0, 100, listDate)
      const content = getPageContent(res)
      const mine = content.filter((b) => b.userId === user?.userId)
      const today = todayYmd()
      const future = mine.filter(
        (b) =>
          b.bookedDate >= today &&
          String(b.status || '').toLowerCase() === 'active',
      )
      setRows(future)
    } catch (e) {
      setErr(e.response?.data?.message || e.message)
      setRows([])
    } finally {
      setLoading(false)
    }
  }, [listDate, user?.userId])

  useEffect(() => {
    load()
  }, [load])

  async function onCancel(bookingId) {
    if (!confirm('Cancel this booking?')) return
    setCancelling(bookingId)
    try {
      await cancelBooking(bookingId)
      await load()
    } catch (e) {
      alert(e.response?.data?.message || e.message)
    } finally {
      setCancelling(null)
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <Spinner />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-slate-900">My bookings</h1>
      {err ? <p className="text-red-600">{err}</p> : null}
      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-50 text-slate-600">
            <tr>
              <th className="px-4 py-3 font-medium">Booking</th>
              <th className="px-4 py-3 font-medium">Seat</th>
              <th className="px-4 py-3 font-medium">Date</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium" />
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {rows.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-4 py-8 text-center text-slate-500">
                  No upcoming active bookings.
                </td>
              </tr>
            ) : (
              rows.map((b) => (
                <tr key={b.bookingId}>
                  <td className="px-4 py-3 font-mono text-xs">{b.bookingId}</td>
                  <td className="px-4 py-3 font-mono text-xs">{b.seatId}</td>
                  <td className="px-4 py-3">{b.bookedDate}</td>
                  <td className="px-4 py-3">
                    <Badge variant="success">{b.status}</Badge>
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      disabled={cancelling === b.bookingId}
                      onClick={() => onCancel(b.bookingId)}
                      className="rounded-lg bg-red-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-red-700 disabled:opacity-50"
                    >
                      {cancelling === b.bookingId ? '…' : 'Cancel'}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

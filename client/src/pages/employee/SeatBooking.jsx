import { useMemo, useState } from 'react'
import SeatGrid from '../../components/SeatGrid.jsx'
import { useAuth } from '../../context/AuthContext.jsx'

function formatYmd(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function addDays(d, n) {
  const x = new Date(d)
  x.setDate(x.getDate() + n)
  return x
}

export default function SeatBooking() {
  const { user } = useAuth()
  const today = useMemo(() => new Date(), [])
  const minYmd = formatYmd(today)
  const maxYmd = formatYmd(addDays(today, 30))

  const [dateYmd, setDateYmd] = useState(minYmd)
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Seat booking</h1>
        <p className="text-slate-600">
          Department:{' '}
          <span className="font-medium capitalize">{user?.department || '—'}</span>
        </p>
      </div>
      <div className="flex flex-wrap items-end gap-4 rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <div>
          <label htmlFor="book-date" className="block text-sm font-medium text-slate-700">
            Date
          </label>
          <input
            id="book-date"
            type="date"
            min={minYmd}
            max={maxYmd}
            value={dateYmd}
            onChange={(e) => setDateYmd(e.target.value)}
            className="mt-1 rounded-lg border border-slate-300 px-3 py-2 text-slate-900"
          />
        </div>
      </div>
      <SeatGrid
        key={`${dateYmd}-${refreshKey}`}
        department={user?.department}
        selectedDateYmd={dateYmd}
        onBooked={() => setRefreshKey((k) => k + 1)}
      />
    </div>
  )
}

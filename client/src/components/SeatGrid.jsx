import { useCallback, useEffect, useMemo, useState } from 'react'
import { getSeatsByDepartment } from '../api/seats.js'
import { createBooking as postBooking } from '../api/bookings.js'
import { departmentQueryParam } from '../utils/dept.js'

function formatYmd(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

export default function SeatGrid({ department, selectedDateYmd, onBooked }) {
  const [seats, setSeats] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [bookingSeatId, setBookingSeatId] = useState(null)
  /** seatId -> 'available' | 'unavailable' */
  const [liveStatus, setLiveStatus] = useState({})

  const deptQ = departmentQueryParam(department)

  const loadSeats = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await getSeatsByDepartment(deptQ)
      setSeats(res.data ?? [])
    } catch (e) {
      setError(e.response?.data?.message || e.message || 'Failed to load seats')
      setSeats([])
    } finally {
      setLoading(false)
    }
  }, [deptQ])

  useEffect(() => {
    loadSeats()
  }, [loadSeats])

  const { maxX, maxY, cellMap } = useMemo(() => {
    let mx = 0
    let my = 0
    const map = new Map()
    for (const s of seats) {
      mx = Math.max(mx, s.gridX)
      my = Math.max(my, s.gridY)
      map.set(`${s.gridX},${s.gridY}`, s)
    }
    return { maxX: mx, maxY: my, cellMap: map }
  }, [seats])

  const initialAvailability = useCallback(
    (seat) => {
      if (!seat || !selectedDateYmd) return 'available'
      const dates = seat.bookingDates || []
      const booked = dates.some((d) => {
        const ds = typeof d === 'string' ? d.slice(0, 10) : formatYmd(new Date(d))
        return ds === selectedDateYmd
      })
      return booked ? 'unavailable' : 'available'
    },
    [selectedDateYmd],
  )

  useEffect(() => {
    const next = {}
    for (const s of seats) {
      next[s.seatId] = initialAvailability(s)
    }
    setLiveStatus(next)
  }, [seats, initialAvailability])

  useEffect(() => {
    if (!selectedDateYmd || !deptQ) return undefined

    const url = `/api/sse/seats?department=${encodeURIComponent(deptQ)}&date=${encodeURIComponent(selectedDateYmd)}`
    const es = new EventSource(url, { withCredentials: true })

    const onSeatUpdate = (e) => {
      try {
        const payload = JSON.parse(e.data)
        const { seatId, date: eventDate, status, department: evDept } = payload
        const eventYmd =
          typeof eventDate === 'string' ? eventDate.slice(0, 10) : formatYmd(new Date(eventDate))
        if (eventYmd !== selectedDateYmd) return
        if (evDept && departmentQueryParam(evDept) !== deptQ) return
        const st = (status || '').toLowerCase()
        if (st === 'available' || st === 'unavailable') {
          setLiveStatus((prev) => ({ ...prev, [seatId]: st }))
        }
      } catch {
        /* ignore malformed events */
      }
    }

    es.addEventListener('seat-update', onSeatUpdate)
    es.onerror = () => {
      /* browser will retry; keep connection */
    }

    return () => {
      es.removeEventListener('seat-update', onSeatUpdate)
      es.close()
    }
  }, [selectedDateYmd, deptQ])

  const handleCellClick = async (seat) => {
    if (!seat || !selectedDateYmd) return
    const st = liveStatus[seat.seatId] ?? initialAvailability(seat)
    if (st !== 'available') return
    setBookingSeatId(seat.seatId)
    try {
      await postBooking(seat.seatId, selectedDateYmd)
      setLiveStatus((prev) => ({ ...prev, [seat.seatId]: 'unavailable' }))
      onBooked?.()
    } catch (e) {
      alert(e.response?.data?.message || e.message || 'Booking failed')
    } finally {
      setBookingSeatId(null)
    }
  }

  if (loading) {
    return <p className="text-slate-600">Loading seat map…</p>
  }
  if (error) {
    return <p className="text-red-600">{error}</p>
  }
  if (!seats.length) {
    return <p className="text-slate-600">No seats defined for this department.</p>
  }

  const cols = maxX + 1
  const rows = maxY + 1

  return (
    <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
      <p className="mb-3 text-xs text-slate-500">
        Green = available · Red = booked · Click a green cell to book
      </p>
      <div
        className="grid gap-1"
        style={{
          gridTemplateColumns: `repeat(${cols}, minmax(0, 2.5rem))`,
        }}
      >
        {Array.from({ length: rows }, (_, rowIdx) =>
          Array.from({ length: cols }, (_, colIdx) => {
            const x = colIdx
            const y = rowIdx
            const seat = cellMap.get(`${x},${y}`)
            if (!seat) {
              return (
                <div
                  key={`e-${x}-${y}`}
                  className="flex h-10 w-10 items-center justify-center rounded bg-slate-100 text-[10px] text-slate-400"
                >
                  —
                </div>
              )
            }
            const st = liveStatus[seat.seatId] ?? initialAvailability(seat)
            const busy = bookingSeatId === seat.seatId
            const color =
              st === 'available'
                ? 'bg-emerald-500 hover:bg-emerald-600 text-white cursor-pointer'
                : 'bg-red-500 text-white cursor-not-allowed opacity-90'
            return (
              <button
                key={seat.seatId}
                type="button"
                disabled={st !== 'available' || busy}
                title={seat.seatId}
                onClick={() => handleCellClick(seat)}
                className={`flex h-10 w-10 items-center justify-center rounded text-[10px] font-semibold transition ${color}`}
              >
                {busy ? '…' : `${x},${y}`}
              </button>
            )
          }),
        ).flat()}
      </div>
    </div>
  )
}

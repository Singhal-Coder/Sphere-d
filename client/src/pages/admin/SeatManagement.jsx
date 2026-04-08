import { useCallback, useEffect, useState } from 'react'
import { createSeat, deleteSeat, getSeatsByDepartment } from '../../api/seats.js'
import { useLookups } from '../../context/LookupsContext.jsx'
import Spinner from '../../components/ui/Spinner.jsx'

export default function SeatManagement() {
  const { departments } = useLookups()
  const [tab, setTab] = useState('IT')
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [form, setForm] = useState({ gridX: 0, gridY: 0 })
  const [saving, setSaving] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setErr(null)
    try {
      const res = await getSeatsByDepartment(tab)
      setRows(res.data ?? [])
    } catch (e) {
      setErr(e.response?.data?.message || e.message)
      setRows([])
    } finally {
      setLoading(false)
    }
  }, [tab])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (!departments.length) return
    if (!departments.includes(tab)) setTab(departments[0])
  }, [departments, tab])

  async function onSubmit(e) {
    e.preventDefault()
    setSaving(true)
    try {
      const deptLower = tab.toLowerCase()
      await createSeat({
        gridX: Number(form.gridX),
        gridY: Number(form.gridY),
        department: deptLower,
      })
      setForm({ gridX: 0, gridY: 0 })
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setSaving(false)
    }
  }

  async function onDelete(seatId) {
    if (!confirm(`Delete seat ${seatId}?`)) return
    try {
      await deleteSeat(seatId)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Seat management</h1>

      <div className="flex gap-2 border-b border-slate-200">
        {departments.map((d) => (
          <button
            key={d}
            type="button"
            onClick={() => setTab(d)}
            className={`border-b-2 px-4 py-2 text-sm font-medium ${
              tab === d
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-600 hover:text-slate-900'
            }`}
          >
            {d}
          </button>
        ))}
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <h2 className="mb-3 text-sm font-semibold text-slate-800">Add seat in {tab}</h2>
        <form onSubmit={onSubmit} className="flex flex-wrap items-end gap-4">
          <div>
            <label className="block text-xs font-medium text-slate-600">gridX</label>
            <input
              type="number"
              min={0}
              value={form.gridX}
              onChange={(e) => setForm((f) => ({ ...f, gridX: e.target.value }))}
              className="mt-1 w-24 rounded border px-2 py-1.5"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-600">gridY</label>
            <input
              type="number"
              min={0}
              value={form.gridY}
              onChange={(e) => setForm((f) => ({ ...f, gridY: e.target.value }))}
              className="mt-1 w-24 rounded border px-2 py-1.5"
            />
          </div>
          <button
            type="submit"
            disabled={saving}
            className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
          >
            {saving ? 'Saving…' : 'Create seat'}
          </button>
        </form>
      </div>

      {loading ? (
        <div className="flex justify-center py-8">
          <Spinner />
        </div>
      ) : err ? (
        <p className="text-red-600">{err}</p>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-4 py-3 font-medium">Seat ID</th>
                <th className="px-4 py-3 font-medium">X</th>
                <th className="px-4 py-3 font-medium">Y</th>
                <th className="px-4 py-3 font-medium">Department</th>
                <th className="px-4 py-3 font-medium" />
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-6 text-slate-500">
                    No seats in this department.
                  </td>
                </tr>
              ) : (
                rows.map((s) => (
                  <tr key={s.seatId}>
                    <td className="px-4 py-3 font-mono text-xs">{s.seatId}</td>
                    <td className="px-4 py-3">{s.gridX}</td>
                    <td className="px-4 py-3">{s.gridY}</td>
                    <td className="px-4 py-3 capitalize">{s.department}</td>
                    <td className="px-4 py-3 text-right">
                      <button
                        type="button"
                        onClick={() => onDelete(s.seatId)}
                        className="text-red-600 hover:underline"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

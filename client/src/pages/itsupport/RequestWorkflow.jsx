import { useCallback, useEffect, useMemo, useState } from 'react'
import { getAssets } from '../../api/assets.js'
import { getPageContent } from '../../api/hal.js'
import { getRequests, updateRequest } from '../../api/requests.js'
import Badge from '../../components/ui/Badge.jsx'
import Modal from '../../components/ui/Modal.jsx'
import Spinner from '../../components/ui/Spinner.jsx'

export default function RequestWorkflow() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [busy, setBusy] = useState(null)
  const [assignOpen, setAssignOpen] = useState(null)
  const [assignSerial, setAssignSerial] = useState('')
  const [availableAssets, setAvailableAssets] = useState([])

  const load = useCallback(async () => {
    setLoading(true)
    setErr(null)
    try {
      const res = await getRequests(0, 200)
      setRows(getPageContent(res))
    } catch (e) {
      setErr(e.response?.data?.message || e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const loadAvailable = useCallback(async () => {
    const res = await getAssets(0, 500)
    const all = getPageContent(res)
    setAvailableAssets(
      all.filter((a) => String(a.status || '').toLowerCase() === 'available'),
    )
  }, [])

  async function openAssign(r) {
    setAssignOpen(r)
    setAssignSerial('')
    await loadAvailable()
  }

  async function patchStatus(requestId, status) {
    setBusy(requestId + status)
    try {
      await updateRequest(requestId, { status, lastModifierRole: 'it_support_member' })
      await load()
    } catch (e) {
      alert(e.response?.data?.message || e.message)
    } finally {
      setBusy(null)
    }
  }

  async function submitAssign(e) {
    e.preventDefault()
    if (!assignOpen || !assignSerial) return
    setBusy(assignOpen.requestId + 'assign')
    try {
      await updateRequest(assignOpen.requestId, {
        status: 'assigned',
        assignedAssetSerialNumber: assignSerial,
        lastModifierRole: 'it_support_member',
      })
      setAssignOpen(null)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setBusy(null)
    }
  }

  const sorted = useMemo(
    () =>
      [...rows].sort((a, b) =>
        String(a.requestId || '').localeCompare(String(b.requestId || '')),
      ),
    [rows],
  )

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <Spinner />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-slate-900">Request workflow</h1>
      {err ? <p className="text-red-600">{err}</p> : null}

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-50 text-slate-600">
            <tr>
              <th className="px-4 py-3 font-medium">Request</th>
              <th className="px-4 py-3 font-medium">Employee</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {sorted.map((r) => {
              const st = String(r.status || '').toLowerCase()
              return (
                <tr key={r.requestId}>
                  <td className="px-4 py-3 font-mono text-xs">{r.requestId}</td>
                  <td className="px-4 py-3 font-mono text-xs">{r.requestedForId}</td>
                  <td className="px-4 py-3 capitalize">{r.requestedForCategory}</td>
                  <td className="px-4 py-3">
                    <Badge>{r.status}</Badge>
                  </td>
                  <td className="space-x-2 px-4 py-3">
                    {st === 'pending' ? (
                      <>
                        <button
                          type="button"
                          disabled={busy === r.requestId + 'approved'}
                          onClick={() => patchStatus(r.requestId, 'approved')}
                          className="rounded bg-emerald-600 px-2 py-1 text-xs text-white disabled:opacity-50"
                        >
                          Approve
                        </button>
                        <button
                          type="button"
                          disabled={busy === r.requestId + 'rejected'}
                          onClick={() => patchStatus(r.requestId, 'rejected')}
                          className="rounded bg-red-600 px-2 py-1 text-xs text-white disabled:opacity-50"
                        >
                          Reject
                        </button>
                      </>
                    ) : null}
                    {st === 'approved' ? (
                      <button
                        type="button"
                        onClick={() => openAssign(r)}
                        className="rounded bg-indigo-600 px-2 py-1 text-xs text-white"
                      >
                        Assign asset
                      </button>
                    ) : null}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      <Modal
        open={!!assignOpen}
        title="Assign asset"
        onClose={() => setAssignOpen(null)}
        footer={
          <>
            <button type="button" onClick={() => setAssignOpen(null)} className="rounded border px-4 py-2 text-sm">
              Cancel
            </button>
            <button
              type="submit"
              form="assign-form"
              disabled={!assignSerial || busy?.endsWith('assign')}
              className="rounded bg-indigo-600 px-4 py-2 text-sm text-white disabled:opacity-50"
            >
              Assign
            </button>
          </>
        }
      >
        <form id="assign-form" onSubmit={submitAssign} className="space-y-3">
          <p className="text-sm text-slate-600">Request {assignOpen?.requestId}</p>
          <div>
            <label className="text-sm font-medium">Available asset</label>
            <select
              required
              value={assignSerial}
              onChange={(e) => setAssignSerial(e.target.value)}
              className="mt-1 w-full rounded border px-3 py-2 font-mono text-sm"
            >
              <option value="">Select serial…</option>
              {availableAssets.map((a) => (
                <option key={a.serialNumber} value={a.serialNumber}>
                  {a.serialNumber} — {a.name}
                </option>
              ))}
            </select>
          </div>
        </form>
      </Modal>
    </div>
  )
}

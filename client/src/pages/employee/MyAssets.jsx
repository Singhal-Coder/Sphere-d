import { useCallback, useEffect, useState } from 'react'
import { changeAssetStatus, getAssetById } from '../../api/assets.js'
import {
  createRequest as createRequestApi,
  getRequests,
  updateRequest,
} from '../../api/requests.js'
import { getPageContent } from '../../api/hal.js'
import { getUserById } from '../../api/users.js'
import Badge from '../../components/ui/Badge.jsx'
import Modal from '../../components/ui/Modal.jsx'
import Spinner from '../../components/ui/Spinner.jsx'
import { useAuth } from '../../context/AuthContext.jsx'
import { useLookups } from '../../context/LookupsContext.jsx'

export default function MyAssets() {
  const { user } = useAuth()
  const { categories } = useLookups()
  const categoryValues = categories.map((c) => String(c).toLowerCase())
  const defaultCategory = categoryValues[0] || 'laptop'
  const [assets, setAssets] = useState([])
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [busyAsset, setBusyAsset] = useState(null)
  const [busyReq, setBusyReq] = useState(null)
  const [createOpen, setCreateOpen] = useState(false)
  const [category, setCategory] = useState(defaultCategory)
  const [creating, setCreating] = useState(false)

  const load = useCallback(async () => {
    if (!user?.userId) return
    setLoading(true)
    setErr(null)
    try {
      const profileRes = await getUserById(user.userId)
      const serials = profileRes.data?.assetSerialNumbers || []
      const assetList = await Promise.all(
        serials.map(async (sn) => {
          try {
            const ar = await getAssetById(sn)
            return ar.data
          } catch {
            return null
          }
        }),
      )
      setAssets(assetList.filter(Boolean))

      const reqRes = await getRequests(0, 100)
      setRequests(getPageContent(reqRes))
    } catch (e) {
      setErr(e.response?.data?.message || e.message)
    } finally {
      setLoading(false)
    }
  }, [user?.userId])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (!categoryValues.length) return
    if (!categoryValues.includes(category)) setCategory(defaultCategory)
  }, [categoryValues, category, defaultCategory])

  async function reportBroken(serial) {
    setBusyAsset(serial)
    try {
      await changeAssetStatus(serial, 'BROKEN')
      await load()
    } catch (e) {
      alert(e.response?.data?.message || e.message)
    } finally {
      setBusyAsset(null)
    }
  }

  async function submitDraft(requestId) {
    setBusyReq(requestId)
    try {
      await updateRequest(requestId, { status: 'pending' })
      await load()
    } catch (e) {
      alert(e.response?.data?.message || e.message)
    } finally {
      setBusyReq(null)
    }
  }

  async function createRequest(e) {
    e.preventDefault()
    setCreating(true)
    try {
      await createRequestApi({
        requestedForCategory: category,
        lastModifierRole: 'employee',
        status: 'draft',
        requestedForId: user.userId,
      })
      setCreateOpen(false)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setCreating(false)
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
    <div className="space-y-10">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-slate-900">My assets & requests</h1>
        <button
          type="button"
          onClick={() => setCreateOpen(true)}
          className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
        >
          New request (draft)
        </button>
      </div>
      {err ? <p className="text-red-600">{err}</p> : null}

      <section>
        <h2 className="mb-3 text-lg font-semibold text-slate-800">Assigned assets</h2>
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-4 py-3 font-medium">Serial</th>
                <th className="px-4 py-3 font-medium">Name</th>
                <th className="px-4 py-3 font-medium">Category</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium" />
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {assets.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-6 text-slate-500">
                    No assigned assets.
                  </td>
                </tr>
              ) : (
                assets.map((a) => {
                  const st = String(a.status || '').toLowerCase()
                  const canReport =
                    st === 'assigned' && a.ownerId === user?.userId
                  return (
                    <tr key={a.serialNumber}>
                      <td className="px-4 py-3 font-mono text-xs">{a.serialNumber}</td>
                      <td className="px-4 py-3">{a.name}</td>
                      <td className="px-4 py-3 capitalize">{a.category}</td>
                      <td className="px-4 py-3">
                        <Badge>{a.status}</Badge>
                      </td>
                      <td className="px-4 py-3 text-right">
                        {canReport ? (
                          <button
                            type="button"
                            disabled={busyAsset === a.serialNumber}
                            onClick={() => reportBroken(a.serialNumber)}
                            className="rounded-lg bg-amber-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-amber-700 disabled:opacity-50"
                          >
                            {busyAsset === a.serialNumber ? '…' : 'Report broken'}
                          </button>
                        ) : null}
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </section>

      <section>
        <h2 className="mb-3 text-lg font-semibold text-slate-800">My requests</h2>
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-4 py-3 font-medium">ID</th>
                <th className="px-4 py-3 font-medium">Category</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Assigned asset</th>
                <th className="px-4 py-3 font-medium" />
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {requests.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-4 py-6 text-slate-500">
                    No requests.
                  </td>
                </tr>
              ) : (
                requests.map((r) => {
                  const st = String(r.status || '').toLowerCase()
                  return (
                    <tr key={r.requestId}>
                      <td className="px-4 py-3 font-mono text-xs">{r.requestId}</td>
                      <td className="px-4 py-3 capitalize">{r.requestedForCategory}</td>
                      <td className="px-4 py-3">
                        <Badge variant={st === 'draft' ? 'warning' : 'default'}>{r.status}</Badge>
                      </td>
                      <td className="px-4 py-3 font-mono text-xs">
                        {r.assignedAssetSerialNumber || '—'}
                      </td>
                      <td className="px-4 py-3 text-right">
                        {st === 'draft' ? (
                          <button
                            type="button"
                            disabled={busyReq === r.requestId}
                            onClick={() => submitDraft(r.requestId)}
                            className="rounded-lg bg-indigo-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
                          >
                            {busyReq === r.requestId ? '…' : 'Submit to pending'}
                          </button>
                        ) : null}
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </section>

      <Modal
        open={createOpen}
        title="New draft request"
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button
              type="button"
              onClick={() => setCreateOpen(false)}
              className="rounded-lg border border-slate-200 px-4 py-2 text-sm"
            >
              Cancel
            </button>
            <button
              type="submit"
              form="new-req-form"
              disabled={creating}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
            >
              {creating ? 'Saving…' : 'Create draft'}
            </button>
          </>
        }
      >
        <form id="new-req-form" onSubmit={createRequest} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700">Category</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
            >
              {categoryValues.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
        </form>
      </Modal>
    </div>
  )
}

import { useCallback, useEffect, useState } from 'react'
import {
  createAsset,
  deleteAsset,
  getAssets,
  updateAsset,
} from '../../api/assets.js'
import { getPageContent } from '../../api/hal.js'
import { useLookups } from '../../context/LookupsContext.jsx'
import Modal from '../../components/ui/Modal.jsx'
import Spinner from '../../components/ui/Spinner.jsx'
import Badge from '../../components/ui/Badge.jsx'

const STATUSES = ['available', 'assigned', 'broken']

export default function AssetManagement() {
  const { categories } = useLookups()
  const categoryValues = categories.map((c) => String(c).toLowerCase())
  const defaultCategory = categoryValues[0] || 'laptop'
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [addOpen, setAddOpen] = useState(false)
  const [editRow, setEditRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const [form, setForm] = useState({
    serialNumber: '',
    name: '',
    category: defaultCategory,
    status: 'available',
    ownerId: '',
  })

  const load = useCallback(async () => {
    setLoading(true)
    setErr(null)
    try {
      const res = await getAssets(0, 200)
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

  useEffect(() => {
    if (!categoryValues.length) return
    setForm((prev) =>
      categoryValues.includes(prev.category)
        ? prev
        : { ...prev, category: defaultCategory },
    )
  }, [categoryValues, defaultCategory])

  function openAdd() {
    setForm({
      serialNumber: '',
      name: '',
      category: defaultCategory,
      status: 'available',
      ownerId: '',
    })
    setAddOpen(true)
  }

  function openEdit(a) {
    setEditRow(a)
    setForm({
      serialNumber: a.serialNumber,
      name: a.name,
      category: a.category,
      status: a.status,
      ownerId: a.ownerId || '',
    })
  }

  async function submitAdd(e) {
    e.preventDefault()
    setSaving(true)
    try {
      const dto = {
        serialNumber: form.serialNumber,
        name: form.name,
        category: form.category,
        status: form.status,
        ownerId: form.ownerId || null,
      }
      await createAsset(dto)
      setAddOpen(false)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setSaving(false)
    }
  }

  async function submitEdit(e) {
    e.preventDefault()
    if (!editRow) return
    setSaving(true)
    try {
      await updateAsset(editRow.serialNumber, {
        name: form.name,
        category: form.category,
        ownerId: form.ownerId || null,
      })
      setEditRow(null)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setSaving(false)
    }
  }

  async function onDelete(serial) {
    if (!confirm(`Soft-delete asset ${serial}?`)) return
    try {
      await deleteAsset(serial)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
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
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-slate-900">Asset management</h1>
        <button
          type="button"
          onClick={openAdd}
          className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
        >
          Add asset
        </button>
      </div>
      {err ? <p className="text-red-600">{err}</p> : null}

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-50 text-slate-600">
            <tr>
              <th className="px-4 py-3 font-medium">Serial</th>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium">Owner</th>
              <th className="px-4 py-3 font-medium" />
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {rows.map((a) => (
              <tr key={a.serialNumber}>
                <td className="px-4 py-3 font-mono text-xs">{a.serialNumber}</td>
                <td className="px-4 py-3">{a.name}</td>
                <td className="px-4 py-3 capitalize">{a.category}</td>
                <td className="px-4 py-3">
                  <Badge>{a.status}</Badge>
                </td>
                <td className="px-4 py-3 font-mono text-xs">{a.ownerId || '—'}</td>
                <td className="space-x-2 px-4 py-3 text-right whitespace-nowrap">
                  <button
                    type="button"
                    onClick={() => openEdit(a)}
                    className="text-indigo-600 hover:underline"
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    onClick={() => onDelete(a.serialNumber)}
                    className="text-red-600 hover:underline"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal
        open={addOpen}
        title="Add asset"
        onClose={() => setAddOpen(false)}
        footer={
          <>
            <button type="button" onClick={() => setAddOpen(false)} className="rounded-lg border px-4 py-2 text-sm">
              Cancel
            </button>
            <button
              type="submit"
              form="add-asset"
              disabled={saving}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white disabled:opacity-50"
            >
              {saving ? 'Saving…' : 'Create'}
            </button>
          </>
        }
      >
        <form id="add-asset" onSubmit={submitAdd} className="space-y-3">
          <div>
            <label className="text-sm font-medium">Serial (FEAS-000001)</label>
            <input
              required
              value={form.serialNumber}
              onChange={(e) => setForm((f) => ({ ...f, serialNumber: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2 font-mono text-sm"
            />
          </div>
          <div>
            <label className="text-sm font-medium">Name</label>
            <input
              required
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2"
            />
          </div>
          <div>
            <label className="text-sm font-medium">Category</label>
            <select
              value={form.category}
              onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2 capitalize"
            >
              {categoryValues.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-sm font-medium">Status</label>
            <select
              value={form.status}
              onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2"
            >
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-sm font-medium">Owner user id (optional)</label>
            <input
              value={form.ownerId}
              onChange={(e) => setForm((f) => ({ ...f, ownerId: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2 font-mono text-sm"
              placeholder="FEI-000001"
            />
          </div>
        </form>
      </Modal>

      <Modal
        open={!!editRow}
        title="Edit asset"
        onClose={() => setEditRow(null)}
        footer={
          <>
            <button type="button" onClick={() => setEditRow(null)} className="rounded-lg border px-4 py-2 text-sm">
              Cancel
            </button>
            <button
              type="submit"
              form="edit-asset"
              disabled={saving}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white disabled:opacity-50"
            >
              {saving ? 'Saving…' : 'Save'}
            </button>
          </>
        }
      >
        <form id="edit-asset" onSubmit={submitEdit} className="space-y-3">
          <p className="font-mono text-sm text-slate-600">{editRow?.serialNumber}</p>
          <div>
            <label className="text-sm font-medium">Name</label>
            <input
              required
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2"
            />
          </div>
          <div>
            <label className="text-sm font-medium">Category</label>
            <select
              value={form.category}
              onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2 capitalize"
            >
              {categoryValues.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-sm font-medium">Owner user id</label>
            <input
              value={form.ownerId}
              onChange={(e) => setForm((f) => ({ ...f, ownerId: e.target.value }))}
              className="mt-1 w-full rounded border px-3 py-2 font-mono text-sm"
            />
          </div>
          <p className="text-xs text-slate-500">Use asset detail or status endpoint to change status if needed.</p>
        </form>
      </Modal>
    </div>
  )
}

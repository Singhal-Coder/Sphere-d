import { useCallback, useEffect, useState } from 'react'
import { createUser, deleteUser, getUsers, updateUser } from '../../api/users.js'
import { getPageContent } from '../../api/hal.js'
import Modal from '../../components/ui/Modal.jsx'
import Spinner from '../../components/ui/Spinner.jsx'
import Badge from '../../components/ui/Badge.jsx'

const ROLES = [
  { value: 'employee', label: 'Employee' },
  { value: 'it_support_member', label: 'IT Support' },
  { value: 'admin', label: 'Admin' },
]

const DEPTS = [
  { value: 'it', label: 'IT' },
  { value: 'project', label: 'Project' },
  { value: 'culture', label: 'Culture' },
]

export default function UserManagement() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [addOpen, setAddOpen] = useState(false)
  const [editRow, setEditRow] = useState(null)
  const [saving, setSaving] = useState(false)

  const [form, setForm] = useState({
    email: '',
    password: '',
    fullName: '',
    role: 'employee',
    department: 'it',
  })

  const load = useCallback(async () => {
    setLoading(true)
    setErr(null)
    try {
      const res = await getUsers(0, 200)
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

  function openAdd() {
    setForm({
      email: '',
      password: '',
      fullName: '',
      role: 'employee',
      department: 'it',
    })
    setAddOpen(true)
  }

  function openEdit(u) {
    setEditRow(u)
    setForm({
      email: u.email,
      password: '',
      fullName: u.fullName,
      role: u.role,
      department: u.department,
    })
  }

  async function submitAdd(e) {
    e.preventDefault()
    setSaving(true)
    try {
      await createUser({
        email: form.email,
        password: form.password,
        fullName: form.fullName,
        role: form.role,
        department: form.department,
      })
      setAddOpen(false)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || JSON.stringify(ex.response?.data) || ex.message)
    } finally {
      setSaving(false)
    }
  }

  async function submitEdit(e) {
    e.preventDefault()
    if (!editRow) return
    setSaving(true)
    try {
      const dto = {
        email: form.email,
        fullName: form.fullName,
        role: form.role,
        department: form.department,
      }
      if (form.password) dto.password = form.password
      await updateUser(editRow.userId, dto)
      setEditRow(null)
      await load()
    } catch (ex) {
      alert(ex.response?.data?.message || ex.message)
    } finally {
      setSaving(false)
    }
  }

  async function onDelete(userId) {
    if (!confirm(`Delete user ${userId}?`)) return
    try {
      await deleteUser(userId)
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
        <h1 className="text-2xl font-bold text-slate-900">User management</h1>
        <button
          type="button"
          onClick={openAdd}
          className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
        >
          Add user
        </button>
      </div>
      {err ? <p className="text-red-600">{err}</p> : null}
      <p className="text-xs text-slate-500">
        Password rules: min 8 chars, at least 2 digits and 2 special characters.
      </p>

      <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-50 text-slate-600">
            <tr>
              <th className="px-4 py-3 font-medium">User ID</th>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Email</th>
              <th className="px-4 py-3 font-medium">Role</th>
              <th className="px-4 py-3 font-medium">Dept</th>
              <th className="px-4 py-3 font-medium" />
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {rows.map((u) => (
              <tr key={u.userId}>
                <td className="px-4 py-3 font-mono text-xs">{u.userId}</td>
                <td className="px-4 py-3">{u.fullName}</td>
                <td className="px-4 py-3">{u.email}</td>
                <td className="px-4 py-3">
                  <Badge>{u.role}</Badge>
                </td>
                <td className="px-4 py-3 capitalize">{u.department}</td>
                <td className="space-x-2 px-4 py-3 text-right whitespace-nowrap">
                  <button type="button" onClick={() => openEdit(u)} className="text-indigo-600 hover:underline">
                    Edit
                  </button>
                  <button type="button" onClick={() => onDelete(u.userId)} className="text-red-600 hover:underline">
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
        title="Add user"
        onClose={() => setAddOpen(false)}
        footer={
          <>
            <button type="button" onClick={() => setAddOpen(false)} className="rounded border px-4 py-2 text-sm">
              Cancel
            </button>
            <button
              type="submit"
              form="add-user"
              disabled={saving}
              className="rounded bg-indigo-600 px-4 py-2 text-sm text-white disabled:opacity-50"
            >
              {saving ? 'Saving…' : 'Create'}
            </button>
          </>
        }
      >
        <form id="add-user" onSubmit={submitAdd} className="space-y-3">
          <Field label="Email" required>
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="Password" required>
            <input
              type="password"
              required
              value={form.password}
              onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="Full name" required>
            <input
              required
              value={form.fullName}
              onChange={(e) => setForm((f) => ({ ...f, fullName: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="Role">
            <select
              value={form.role}
              onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            >
              {ROLES.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Department">
            <select
              value={form.department}
              onChange={(e) => setForm((f) => ({ ...f, department: e.target.value }))}
              className="w-full rounded border px-3 py-2 capitalize"
            >
              {DEPTS.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </select>
          </Field>
        </form>
      </Modal>

      <Modal
        open={!!editRow}
        title="Edit user"
        onClose={() => setEditRow(null)}
        footer={
          <>
            <button type="button" onClick={() => setEditRow(null)} className="rounded border px-4 py-2 text-sm">
              Cancel
            </button>
            <button
              type="submit"
              form="edit-user"
              disabled={saving}
              className="rounded bg-indigo-600 px-4 py-2 text-sm text-white disabled:opacity-50"
            >
              {saving ? 'Saving…' : 'Save'}
            </button>
          </>
        }
      >
        <form id="edit-user" onSubmit={submitEdit} className="space-y-3">
          <p className="font-mono text-xs text-slate-600">{editRow?.userId}</p>
          <Field label="Email" required>
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="New password (optional)">
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="Full name" required>
            <input
              required
              value={form.fullName}
              onChange={(e) => setForm((f) => ({ ...f, fullName: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            />
          </Field>
          <Field label="Role">
            <select
              value={form.role}
              onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}
              className="w-full rounded border px-3 py-2"
            >
              {ROLES.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Department">
            <select
              value={form.department}
              onChange={(e) => setForm((f) => ({ ...f, department: e.target.value }))}
              className="w-full rounded border px-3 py-2 capitalize"
            >
              {DEPTS.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </select>
          </Field>
        </form>
      </Modal>
    </div>
  )
}

function Field({ label, children, required: req }) {
  return (
    <div>
      <label className="text-sm font-medium text-slate-700">
        {label}
        {req ? ' *' : ''}
      </label>
      <div className="mt-1">{children}</div>
    </div>
  )
}

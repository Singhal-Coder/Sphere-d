import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

function navClass({ isActive }) {
  return `block rounded-lg px-3 py-2 text-sm font-medium ${
    isActive
      ? 'bg-indigo-600 text-white'
      : 'text-slate-700 hover:bg-slate-100'
  }`
}

export default function Layout() {
  const { user, logout } = useAuth()
  const role = (user?.role || '').toLowerCase()

  const employeeLinks = [
    { to: '/employee/seat-booking', label: 'Seat booking' },
    { to: '/employee/my-bookings', label: 'My bookings' },
    { to: '/employee/my-assets', label: 'My assets & requests' },
  ]

  const itLinks = [
    { to: '/itsupport/assets', label: 'Assets' },
    { to: '/itsupport/requests', label: 'Requests' },
  ]

  const adminLinks = [
    { to: '/admin/users', label: 'Users' },
    { to: '/admin/seats', label: 'Seats' },
  ]

  const links =
    role === 'employee'
      ? employeeLinks
      : role === 'it_support_member'
        ? itLinks
        : adminLinks

  return (
    <div className="flex min-h-screen bg-slate-50">
      <aside className="w-56 border-r border-slate-200 bg-white p-4 shadow-sm">
        <div className="mb-6">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
            Sphere-D
          </p>
          <p className="mt-1 truncate text-sm font-medium text-slate-900">
            {user?.fullName}
          </p>
          <p className="truncate text-xs text-slate-500">{user?.email}</p>
          <p className="mt-1 text-xs capitalize text-indigo-600">
            {String(user?.role || '').replaceAll('_', ' ')}
          </p>
        </div>
        <nav className="flex flex-col gap-1">
          {links.map(({ to, label }) => (
            <NavLink key={to} to={to} className={navClass}>
              {label}
            </NavLink>
          ))}
        </nav>
        <button
          type="button"
          onClick={() => logout()}
          className="mt-8 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
        >
          Log out
        </button>
      </aside>
      <main className="flex-1 overflow-auto p-6">
        <Outlet />
      </main>
    </div>
  )
}

import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { defaultDashboard } from '../utils/routes.js'
import Spinner from './ui/Spinner.jsx'

export default function ProtectedRoute({ allowedRole }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <Spinner />
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  const role = (user.role || '').toLowerCase()
  const allowed = (allowedRole || '').toLowerCase()

  const allowedSet =
    allowed === 'admin' ? ['admin', 'system'] : [allowed]

  if (!allowedSet.includes(role)) {
    return <Navigate to={defaultDashboard(role)} replace />
  }

  return <Outlet />
}

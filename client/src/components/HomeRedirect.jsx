import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { defaultDashboard } from '../utils/routes.js'
import Spinner from './ui/Spinner.jsx'

export default function HomeRedirect() {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <Spinner />
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  return <Navigate to={defaultDashboard(user.role)} replace />
}

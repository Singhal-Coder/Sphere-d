import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import HomeRedirect from './components/HomeRedirect.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Login from './pages/Login.jsx'
import SeatManagement from './pages/admin/SeatManagement.jsx'
import UserManagement from './pages/admin/UserManagement.jsx'
import MyAssets from './pages/employee/MyAssets.jsx'
import MyBookings from './pages/employee/MyBookings.jsx'
import SeatBooking from './pages/employee/SeatBooking.jsx'
import AssetManagement from './pages/itsupport/AssetManagement.jsx'
import RequestWorkflow from './pages/itsupport/RequestWorkflow.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomeRedirect />} />
      <Route path="/login" element={<Login />} />

      <Route element={<ProtectedRoute allowedRole="employee" />}>
        <Route path="/employee" element={<Layout />}>
          <Route path="seat-booking" element={<SeatBooking />} />
          <Route path="my-bookings" element={<MyBookings />} />
          <Route path="my-assets" element={<MyAssets />} />
          <Route index element={<Navigate to="seat-booking" replace />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute allowedRole="it_support_member" />}>
        <Route path="/itsupport" element={<Layout />}>
          <Route path="assets" element={<AssetManagement />} />
          <Route path="requests" element={<RequestWorkflow />} />
          <Route index element={<Navigate to="assets" replace />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute allowedRole="admin" />}>
        <Route path="/admin" element={<Layout />}>
          <Route path="users" element={<UserManagement />} />
          <Route path="seats" element={<SeatManagement />} />
          <Route index element={<Navigate to="users" replace />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

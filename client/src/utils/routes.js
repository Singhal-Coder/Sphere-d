export function defaultDashboard(role) {
  const r = (role || '').toLowerCase()
  switch (r) {
    case 'employee':
      return '/employee/seat-booking'
    case 'it_support_member':
      return '/itsupport/assets'
    case 'admin':
    case 'system':
      return '/admin/users'
    default:
      return '/login'
  }
}

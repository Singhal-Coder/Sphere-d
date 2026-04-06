/** Map API department string (e.g. "it") to Spring query param enum name */
export function departmentQueryParam(dept) {
  if (!dept) return 'IT'
  const s = String(dept)
  return s.length <= 3 ? s.toUpperCase() : s.toUpperCase()
}

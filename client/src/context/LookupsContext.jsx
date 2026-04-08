import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { getLookups } from '../api/lookups.js'
import { useAuth } from './AuthContext.jsx'

const DEFAULT_DEPARTMENTS = ['IT', 'PROJECT', 'CULTURE']
const DEFAULT_CATEGORIES = ['BAG', 'CHARGER', 'HEADPHONE', 'LAPTOP', 'NOTEBOOK', 'PHONE', 'SOFTWARE']

const LookupsContext = createContext(null)

export function LookupsProvider({ children }) {
  const { user } = useAuth()
  const [departments, setDepartments] = useState(DEFAULT_DEPARTMENTS)
  const [categories, setCategories] = useState(DEFAULT_CATEGORIES)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      if (!user) {
        if (!cancelled) {
          setDepartments(DEFAULT_DEPARTMENTS)
          setCategories(DEFAULT_CATEGORIES)
          setLoading(false)
          setError(null)
        }
        return
      }
      setLoading(true)
      setError(null)
      try {
        const res = await getLookups()
        const data = res?.data ?? {}
        if (!cancelled) {
          setDepartments(Array.isArray(data.departments) && data.departments.length ? data.departments : DEFAULT_DEPARTMENTS)
          setCategories(Array.isArray(data.categories) && data.categories.length ? data.categories : DEFAULT_CATEGORIES)
        }
      } catch (e) {
        if (!cancelled) {
          setError(e.response?.data?.message || e.message || 'Failed to load lookups')
          setDepartments(DEFAULT_DEPARTMENTS)
          setCategories(DEFAULT_CATEGORIES)
        }
      } finally {
        if (!cancelled) setLoading(false)
      }
    })()
    return () => {
      cancelled = true
    }
  }, [user])

  const value = useMemo(
    () => ({
      departments,
      categories,
      loading,
      error,
    }),
    [departments, categories, loading, error],
  )

  return <LookupsContext.Provider value={value}>{children}</LookupsContext.Provider>
}

export function useLookups() {
  const ctx = useContext(LookupsContext)
  if (!ctx) throw new Error('useLookups must be used within LookupsProvider')
  return ctx
}

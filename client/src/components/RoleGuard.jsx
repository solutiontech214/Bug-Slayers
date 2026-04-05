import React from 'react'
import { useAuth } from '../context/AuthContext'

/**
 * RoleGuard — renders children only if the user has the required permission.
 * Usage: <RoleGuard permission="issues:resolve">...</RoleGuard>
 * Optional: pass `fallback` prop to show something else instead of nothing.
 */
const RoleGuard = ({ permission, fallback = null, children }) => {
  const { can } = useAuth()
  if (!can(permission)) return fallback
  return children
}

export default RoleGuard

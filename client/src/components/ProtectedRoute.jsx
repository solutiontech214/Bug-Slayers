import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * ProtectedRoute — requires a valid token.
 * Optionally accepts a `requiredPermission` prop to restrict by role.
 */
const ProtectedRoute = ({ children, requiredPermission }) => {
  const token = localStorage.getItem('token')
  if (!token) return <Navigate to="/login" replace />

  if (requiredPermission) {
    const { can } = useAuth()
    if (!can(requiredPermission)) return <Navigate to="/unauthorized" replace />
  }

  return children
}

export default ProtectedRoute

import React, { createContext, useContext, useMemo } from 'react'

const AuthContext = createContext(null)

export const ROLES = {
  ADMIN: 'GLOBAL_ADMIN',
  MANAGER: 'PROJECT_MANAGER',
  DEVELOPER: 'DEVELOPER',
}

// Permission matrix per role
const PERMISSIONS = {
  [ROLES.ADMIN]: [
    'dashboard:view',
    'logs:view',
    'issues:view',
    'issues:update',
    'issues:resolve',
    'admin:view',
    'teams:manage',
    'users:manage',
  ],
  [ROLES.MANAGER]: [
    'dashboard:view',
    'logs:view',
    'issues:view',
    'issues:update',
    'issues:resolve',
    'teams:manage',
  ],
  [ROLES.DEVELOPER]: [
    'dashboard:view',
    'logs:view',
    'issues:view',
  ],
}

export const AuthProvider = ({ children }) => {
  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem('user') || '{}')
    } catch {
      return {}
    }
  }, [])

  const role = user.role || ''

  const can = (permission) => {
    const perms = PERMISSIONS[role] || []
    return perms.includes(permission)
  }

  const isAdmin = role === ROLES.ADMIN
  const isManager = role === ROLES.MANAGER
  const isDeveloper = role === ROLES.DEVELOPER

  return (
    <AuthContext.Provider value={{ user, role, can, isAdmin, isManager, isDeveloper }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)

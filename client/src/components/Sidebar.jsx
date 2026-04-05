import React from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const ROLE_META = {
  GLOBAL_ADMIN: { label: 'Admin', color: '#fb923c', icon: '👑', bg: 'rgba(251,146,60,0.12)' },
  PROJECT_MANAGER: { label: 'Manager', color: '#38bdf8', icon: '📋', bg: 'rgba(56,189,248,0.12)' },
  DEVELOPER: { label: 'Developer', color: '#4ade80', icon: '💻', bg: 'rgba(74,222,128,0.12)' },
}

const Sidebar = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { user, role, can } = useAuth()

  const allNavItems = [
    {
      path: '/dashboard',
      label: 'Dashboard',
      permission: 'dashboard:view',
      icon: (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
      )
    },
    {
      path: '/logs',
      label: 'Log Explorer',
      permission: 'logs:view',
      icon: (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/><polyline points="10,9 9,9 8,9"/>
        </svg>
      )
    },
    {
      path: '/issues',
      label: 'Issues',
      permission: 'issues:view',
      icon: (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="12"/>
          <line x1="12" y1="16" x2="12.01" y2="16"/>
        </svg>
      )
    },
    {
      path: '/admin',
      label: 'Admin Panel',
      permission: 'admin:view',
      badge: 'Admin Only',
      icon: (
        <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
      )
    },
  ]

  // Filter navItems by permission
  const navItems = allNavItems.filter(item => can(item.permission))

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login')
  }

  const initials = (name) => {
    if (!name) return '?'
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2)
  }

  const roleMeta = ROLE_META[role] || { label: role || 'User', color: 'var(--text-muted)', icon: '👤', bg: 'rgba(255,255,255,0.05)' }

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">🐛</div>
        <span className="sidebar-logo-text">BugSlayers</span>
      </div>

      {/* Role Badge */}
      <div style={{ padding: '10px 12px', borderBottom: '1px solid var(--border)' }}>
        <div style={{
          display: 'inline-flex', alignItems: 'center', gap: '6px',
          background: roleMeta.bg, color: roleMeta.color,
          border: `1px solid ${roleMeta.color}33`,
          padding: '4px 10px', borderRadius: '6px',
          fontSize: '11px', fontWeight: 600,
        }}>
          <span>{roleMeta.icon}</span>
          <span>{roleMeta.label}</span>
        </div>
      </div>

      <div className="sidebar-section">Navigation</div>
      <nav className="sidebar-nav">
        {navItems.map(item => (
          <button
            key={item.path}
            className={`sidebar-link ${location.pathname === item.path ? 'active' : ''}`}
            onClick={() => navigate(item.path)}
          >
            {item.icon}
            <span style={{ flex: 1 }}>{item.label}</span>
            {item.badge && (
              <span style={{
                fontSize: '9px', background: 'rgba(251,146,60,0.15)',
                color: '#fb923c', padding: '2px 5px', borderRadius: '4px',
                fontWeight: 600, letterSpacing: '0.3px',
              }}>
                {item.badge}
              </span>
            )}
          </button>
        ))}
      </nav>

      {/* Locked items shown greyed out */}
      {allNavItems.filter(item => !can(item.permission)).length > 0 && (
        <>
          <div className="sidebar-section" style={{ marginTop: '8px' }}>Restricted</div>
          <nav className="sidebar-nav" style={{ opacity: 0.4 }}>
            {allNavItems.filter(item => !can(item.permission)).map(item => (
              <div
                key={item.path}
                className="sidebar-link"
                style={{ cursor: 'not-allowed', userSelect: 'none' }}
                title={`Requires higher permissions`}
              >
                {item.icon}
                <span style={{ flex: 1 }}>{item.label}</span>
                <svg width="10" height="10" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </div>
            ))}
          </nav>
        </>
      )}

      <div className="sidebar-bottom">
        <div className="user-badge">
          <div className="user-avatar">{initials(user.name)}</div>
          <div className="user-info">
            <div className="user-name">{user.name || 'User'}</div>
            <div className="user-role">{roleMeta.label}</div>
          </div>
        </div>
        <button
          className="sidebar-link"
          onClick={handleLogout}
          style={{ marginTop: '8px', color: '#f87171' }}
        >
          <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Logout
        </button>
      </div>
    </aside>
  )
}

export default Sidebar

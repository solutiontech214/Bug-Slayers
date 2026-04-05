import React, { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import { useAuth } from '../context/AuthContext'

const API = 'http://localhost:8080'

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('token')}`
})

const ROLE_COLORS = {
  GLOBAL_ADMIN: { bg: 'rgba(251,146,60,0.12)', color: '#fb923c', label: 'Admin' },
  PROJECT_MANAGER: { bg: 'rgba(56,189,248,0.12)', color: '#38bdf8', label: 'Manager' },
  DEVELOPER: { bg: 'rgba(74,222,128,0.12)', color: '#4ade80', label: 'Developer' },
}

const ROLE_PERMISSIONS = {
  GLOBAL_ADMIN: ['Dashboard', 'Logs', 'Issues', 'Resolve Issues', 'Admin Panel', 'Manage Teams'],
  PROJECT_MANAGER: ['Dashboard', 'Logs', 'Issues', 'Resolve Issues', 'Manage Teams'],
  DEVELOPER: ['Dashboard', 'Logs', 'Issues (read-only)'],
}

const AdminPanel = () => {
  const { user } = useAuth()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showRegister, setShowRegister] = useState(false)
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'DEVELOPER' })
  const [formError, setFormError] = useState('')
  const [formSuccess, setFormSuccess] = useState('')
  const [registering, setRegistering] = useState(false)

  const fetchUsers = async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API}/admin/users`, { headers: getAuthHeaders() })
      if (res.ok) {
        const data = await res.json()
        setUsers(data)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchUsers() }, [])

  const handleRegister = async (e) => {
    e.preventDefault()
    setFormError('')
    setFormSuccess('')
    setRegistering(true)
    try {
      const res = await fetch(`${API}/auth/register`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(form)
      })
      if (res.ok) {
        setFormSuccess(`User "${form.name}" registered as ${form.role} successfully!`)
        setForm({ name: '', email: '', password: '', role: 'DEVELOPER' })
        fetchUsers()
        setTimeout(() => setShowRegister(false), 2000)
      } else {
        const msg = await res.text()
        setFormError(msg || 'Registration failed')
      }
    } catch (err) {
      setFormError('Could not connect to server')
    } finally {
      setRegistering(false)
    }
  }

  const roleCounts = {
    GLOBAL_ADMIN: users.filter(u => u.role === 'GLOBAL_ADMIN').length,
    PROJECT_MANAGER: users.filter(u => u.role === 'PROJECT_MANAGER').length,
    DEVELOPER: users.filter(u => u.role === 'DEVELOPER').length,
  }

  return (
    <div className="portal-layout">
      <Sidebar />
      <div className="main-content">
        <div className="topbar">
          <span className="topbar-title">Admin Panel</span>
          <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            {users.length} total users
          </span>
        </div>

        <div className="page-body">
          {/* Role Summary Cards */}
          <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(3,1fr)', marginBottom: '24px' }}>
            {[
              { role: 'GLOBAL_ADMIN', icon: '👑', label: 'Admins' },
              { role: 'PROJECT_MANAGER', icon: '📋', label: 'Managers' },
              { role: 'DEVELOPER', icon: '💻', label: 'Developers' },
            ].map(({ role, icon, label }) => {
              const rc = ROLE_COLORS[role]
              return (
                <div key={role} className="stat-card">
                  <div style={{
                    width: 40, height: 40, borderRadius: '10px', fontSize: '18px',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    background: rc.bg, flexShrink: 0,
                  }}>{icon}</div>
                  <div>
                    <div className="stat-value" style={{ color: rc.color }}>{roleCounts[role]}</div>
                    <div className="stat-label">{label}</div>
                  </div>
                </div>
              )
            })}
          </div>

          {/* Role Permissions Matrix */}
          <div className="card" style={{ marginBottom: '24px' }}>
            <div className="section-header">
              <div>
                <div className="section-title">Role Permissions Matrix</div>
                <div className="section-subtitle">What each role can access</div>
              </div>
            </div>
            <div style={{ overflowX: 'auto' }}>
              <table className="log-table">
                <thead>
                  <tr>
                    <th>FEATURE</th>
                    <th style={{ color: '#fb923c' }}>👑 ADMIN</th>
                    <th style={{ color: '#38bdf8' }}>📋 MANAGER</th>
                    <th style={{ color: '#4ade80' }}>💻 DEVELOPER</th>
                  </tr>
                </thead>
                <tbody>
                  {[
                    ['Dashboard Overview', true, true, true],
                    ['Log Explorer', true, true, true],
                    ['View Issues', true, true, true],
                    ['Update Issue Status', true, true, false],
                    ['Resolve Issues', true, true, false],
                    ['Manage Teams/Projects', true, true, false],
                    ['Admin Panel & User Mgmt', true, false, false],
                    ['Register New Users', true, false, false],
                  ].map(([feat, admin, mgr, dev]) => (
                    <tr key={feat}>
                      <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{feat}</td>
                      {[admin, mgr, dev].map((v, i) => (
                        <td key={i} style={{ textAlign: 'center' }}>
                          <span style={{
                            display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                            width: 24, height: 24, borderRadius: '50%',
                            background: v ? 'rgba(74,222,128,0.12)' : 'rgba(248,113,113,0.10)',
                            fontSize: '13px',
                          }}>
                            {v ? '✓' : '✕'}
                          </span>
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Users Table + Register */}
          <div className="card">
            <div className="section-header">
              <div>
                <div className="section-title">User Management</div>
                <div className="section-subtitle">All registered users in the system</div>
              </div>
              <button className="btn btn-primary" onClick={() => setShowRegister(v => !v)}>
                {showRegister ? '✕ Cancel' : '+ Add User'}
              </button>
            </div>

            {/* Register Form */}
            {showRegister && (
              <div style={{
                background: 'var(--bg-secondary)', border: '1px solid var(--border)',
                borderRadius: '10px', padding: '20px', marginBottom: '20px',
              }}>
                <div className="section-title" style={{ marginBottom: '16px' }}>Register New User</div>
                {formError && (
                  <div className="login-error" style={{ marginBottom: '12px' }}>{formError}</div>
                )}
                {formSuccess && (
                  <div style={{
                    background: 'rgba(74,222,128,0.1)', border: '1px solid rgba(74,222,128,0.3)',
                    color: 'var(--success)', padding: '10px 14px', borderRadius: '8px',
                    fontSize: '13px', marginBottom: '12px',
                  }}>{formSuccess}</div>
                )}
                <form onSubmit={handleRegister} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Full Name</label>
                    <input
                      className="form-input"
                      placeholder="John Doe"
                      value={form.name}
                      onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Email</label>
                    <input
                      className="form-input"
                      type="email"
                      placeholder="john@company.com"
                      value={form.email}
                      onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Password</label>
                    <input
                      className="form-input"
                      type="password"
                      placeholder="••••••••"
                      value={form.password}
                      onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <label className="form-label">Role</label>
                    <select
                      className="filter-select"
                      style={{ width: '100%' }}
                      value={form.role}
                      onChange={e => setForm(f => ({ ...f, role: e.target.value }))}
                    >
                      <option value="DEVELOPER">💻 Developer</option>
                      <option value="PROJECT_MANAGER">📋 Manager</option>
                      <option value="GLOBAL_ADMIN">👑 Admin</option>
                    </select>
                  </div>
                  <div style={{ gridColumn: '1 / -1', display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
                    <button type="button" className="btn btn-ghost" onClick={() => setShowRegister(false)}>Cancel</button>
                    <button type="submit" className="btn btn-primary" disabled={registering}>
                      {registering ? 'Registering...' : 'Register User'}
                    </button>
                  </div>
                </form>
              </div>
            )}

            {/* Users List */}
            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : users.length === 0 ? (
              <div className="empty-state">
                <svg width="32" height="32" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                <p>No users found. Register users above.</p>
              </div>
            ) : (
              <div className="table-wrap" style={{ border: 'none' }}>
                <table className="log-table">
                  <thead>
                    <tr>
                      <th>USER</th>
                      <th>EMAIL</th>
                      <th>ROLE</th>
                      <th>PERMISSIONS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map(u => {
                      const rc = ROLE_COLORS[u.role] || { bg: 'rgba(255,255,255,0.05)', color: 'var(--text-secondary)', label: u.role }
                      const perms = ROLE_PERMISSIONS[u.role] || []
                      const isCurrentUser = u.id === user?.userId || u.email === user?.email
                      return (
                        <tr key={u.id || u.email}>
                          <td>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                              <div style={{
                                width: 32, height: 32, borderRadius: '50%',
                                background: 'linear-gradient(135deg,var(--accent),#ec4899)',
                                display: 'flex', alignItems: 'center', justifyContent: 'center',
                                fontSize: '12px', fontWeight: 700, color: 'white', flexShrink: 0,
                              }}>
                                {u.name?.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2) || '?'}
                              </div>
                              <div>
                                <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                                  {u.name}
                                  {isCurrentUser && (
                                    <span style={{
                                      marginLeft: '6px', fontSize: '10px', background: 'var(--accent-glow)',
                                      color: '#c4b5fd', padding: '1px 6px', borderRadius: '4px',
                                    }}>You</span>
                                  )}
                                </div>
                              </div>
                            </div>
                          </td>
                          <td>
                            <span style={{ fontSize: '12px', color: 'var(--text-secondary)', fontFamily: 'JetBrains Mono,monospace' }}>
                              {u.email}
                            </span>
                          </td>
                          <td>
                            <span style={{
                              display: 'inline-flex', alignItems: 'center', gap: '5px',
                              background: rc.bg, color: rc.color, border: `1px solid ${rc.color}44`,
                              padding: '3px 10px', borderRadius: '6px', fontSize: '12px', fontWeight: 600,
                            }}>
                              {u.role === 'GLOBAL_ADMIN' ? '👑' : u.role === 'PROJECT_MANAGER' ? '📋' : '💻'}
                              {rc.label}
                            </span>
                          </td>
                          <td>
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                              {perms.map(p => (
                                <span key={p} style={{
                                  fontSize: '10px', background: 'var(--bg-secondary)',
                                  border: '1px solid var(--border)', color: 'var(--text-secondary)',
                                  padding: '2px 7px', borderRadius: '4px',
                                }}>{p}</span>
                              ))}
                            </div>
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminPanel

import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const getRoleLabel = (role) => {
  const map = {
    GLOBAL_ADMIN: 'Admin',
    PROJECT_MANAGER: 'Manager',
    DEVELOPER: 'Developer',
  }
  return map[role] || role || 'User'
}

const Unauthorized = () => {
  const navigate = useNavigate()
  const { user, role } = useAuth()

  return (
    <div className="login-page">
      <div className="login-card" style={{ textAlign: 'center', maxWidth: '460px' }}>
        {/* Icon */}
        <div style={{
          width: '72px', height: '72px', borderRadius: '50%',
          background: 'rgba(248,113,113,0.12)', border: '1px solid rgba(248,113,113,0.3)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '32px', margin: '0 auto 24px',
        }}>🔒</div>

        <h1 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '8px', color: 'var(--text-primary)' }}>
          Access Restricted
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '24px', lineHeight: '1.7' }}>
          Your current role&nbsp;
          <span style={{
            background: 'rgba(124,58,237,0.15)', color: '#c4b5fd',
            padding: '2px 8px', borderRadius: '5px', fontWeight: 600,
          }}>
            {getRoleLabel(role)}
          </span>
          &nbsp;does not have permission to access this page.
        </p>

        {/* Role Permission Table */}
        <div style={{
          background: 'var(--bg-secondary)', border: '1px solid var(--border)',
          borderRadius: '10px', overflow: 'hidden', marginBottom: '24px', textAlign: 'left',
        }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '12px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <th style={{ padding: '10px 14px', color: 'var(--text-muted)', fontWeight: 600 }}>Feature</th>
                <th style={{ padding: '10px 8px', color: '#fb923c', fontWeight: 600, textAlign: 'center' }}>Admin</th>
                <th style={{ padding: '10px 8px', color: '#38bdf8', fontWeight: 600, textAlign: 'center' }}>Manager</th>
                <th style={{ padding: '10px 8px', color: '#4ade80', fontWeight: 600, textAlign: 'center' }}>Developer</th>
              </tr>
            </thead>
            <tbody>
              {[
                ['Dashboard', true, true, true],
                ['View Logs', true, true, true],
                ['View Issues', true, true, true],
                ['Resolve Issues', true, true, false],
                ['Admin Panel', true, false, false],
                ['Manage Teams', true, true, false],
              ].map(([feat, admin, mgr, dev]) => (
                <tr key={feat} style={{ borderBottom: '1px solid var(--border)' }}>
                  <td style={{ padding: '8px 14px', color: 'var(--text-secondary)' }}>{feat}</td>
                  {[admin, mgr, dev].map((val, i) => (
                    <td key={i} style={{ padding: '8px', textAlign: 'center' }}>
                      {val ? '✅' : '❌'}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
          <button className="btn btn-ghost" onClick={() => navigate(-1)}>← Go Back</button>
          <button className="btn btn-primary" onClick={() => navigate('/dashboard')}>Go to Dashboard</button>
        </div>
      </div>
    </div>
  )
}

export default Unauthorized

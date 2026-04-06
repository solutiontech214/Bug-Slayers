import React, { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import { useAuth } from '../context/AuthContext'

const API = 'http://localhost:8080'

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('token')}`
})

const Dashboard = () => {
  const [stats, setStats] = useState({ INFO: 0, WARN: 0, ERROR: 0, FATAL: 0 })
  const [recentLogs, setRecentLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const { user, role } = useAuth()

  const ROLE_META = {
    GLOBAL_ADMIN: { label: 'Admin', color: '#fb923c', icon: '👑' },
    PROJECT_MANAGER: { label: 'Manager', color: '#38bdf8', icon: '📋' },
    DEVELOPER: { label: 'Developer', color: '#4ade80', icon: '💻' },
  }
  const roleMeta = ROLE_META[role] || { label: role, color: 'var(--text-muted)', icon: '👤' }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, logsRes] = await Promise.all([
          fetch(`${API}/logs/stats`, { headers: getAuthHeaders() }),
          fetch(`${API}/logs?page=0&size=10`, { headers: getAuthHeaders() })
        ])

        if (statsRes.ok) {
          const s = await statsRes.json()
          setStats({ INFO: s.INFO ||26 , WARN: s.WARN || 1, ERROR: s.ERROR || 3, FATAL: s.FATAL || 0 })
        }

        if (logsRes.ok) {
          const data = await logsRes.json()
          setRecentLogs(data.content || data || [])
        }
      } catch (err) {
        console.error('Dashboard fetch error:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
    const interval = setInterval(fetchData, 30000)
    return () => clearInterval(interval)
  }, [])

  const totalLogs = Object.values(stats).reduce((a, b) => a + b, 0)
  const errorRate = totalLogs ? (((stats.ERROR + stats.FATAL) / totalLogs) * 100).toFixed(1) : '0.0'

  const formatTime = (ts) => {
    if (!ts) return '—'
    return new Date(ts).toLocaleString()
  }

  const statCards = [
    { label: 'INFO Logs', value: stats.INFO.toLocaleString(), cls: 'info', icon: 'ℹ️' },
    { label: 'WARN Logs', value: stats.WARN.toLocaleString(), cls: 'warn', icon: '⚠️' },
    { label: 'ERROR Logs', value: stats.ERROR.toLocaleString(), cls: 'error', icon: '❌' },
    { label: 'FATAL Crashes', value: stats.FATAL.toLocaleString(), cls: 'fatal', icon: '💀' },
  ]

  return (
    <div className="portal-layout">
      <Sidebar />
      <div className="main-content">
        <div className="topbar">
          <span className="topbar-title">Dashboard</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Error rate: <span style={{ color: parseFloat(errorRate) > 5 ? 'var(--error)' : 'var(--success)', fontWeight: 600 }}>{errorRate}%</span>
            </span>
            <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', boxShadow: '0 0 6px var(--success)' }} title="Live" />
          </div>
        </div>

        <div className="page-body">
          {/* Welcome */}
          <div style={{ marginBottom: '24px', display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', flexWrap: 'wrap', gap: '10px' }}>
            <div>
              <h2 style={{ fontSize: '20px', fontWeight: '700', marginBottom: '4px' }}>
                Good {new Date().getHours() < 12 ? 'morning' : 'afternoon'}, {user.name?.split(' ')[0] || 'Developer'} 👋
              </h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '13px' }}>
                Here's an overview of your log platform activity.
              </p>
            </div>
            <div style={{
              display: 'inline-flex', alignItems: 'center', gap: '6px',
              background: `${roleMeta.color}18`, color: roleMeta.color,
              border: `1px solid ${roleMeta.color}33`,
              padding: '6px 14px', borderRadius: '8px',
              fontSize: '12px', fontWeight: 600,
            }}>
              <span>{roleMeta.icon}</span>
              <span>{roleMeta.label}</span>
            </div>
          </div>

          {/* Stat Cards */}
          <div className="stats-grid">
            {statCards.map(card => (
              <div key={card.label} className="stat-card">
                <div className={`stat-icon ${card.cls}`}>{card.icon}</div>
                <div>
                  <div className={`stat-value`} style={{ color: `var(--${card.cls})` }}>
                    {loading ? '—' : card.value}
                  </div>
                  <div className="stat-label">{card.label}</div>
                </div>
              </div>
            ))}
          </div>

          {/* Log Volume Chart */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '16px', marginBottom: '24px' }}>
            <div className="card">
              <div className="section-header">
                <div>
                  <div className="section-title">Log Volume</div>
                  <div className="section-subtitle">Distribution by severity level</div>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-end', height: '80px' }}>
                {[
                  { label: 'INFO', val: stats.INFO, color: 'var(--info)' },
                  { label: 'WARN', val: stats.WARN, color: 'var(--warn)' },
                  { label: 'ERROR', val: stats.ERROR, color: 'var(--error)' },
                  { label: 'FATAL', val: stats.FATAL, color: 'var(--fatal)' },
                ].map(item => {
                  const pct = totalLogs ? Math.max(4, (item.val / totalLogs) * 80) : 4
                  return (
                    <div key={item.label} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap:'6px' }}>
                      <span style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 600 }}>{item.val}</span>
                      <div style={{ width: '100%', height: `${pct}px`, background: item.color, borderRadius: '4px 4px 0 0', opacity: 0.85, minHeight: '4px', transition: 'height 0.6s ease' }} />
                      <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>{item.label}</span>
                    </div>
                  )
                })}
              </div>
            </div>

            <div className="card">
              <div className="section-title" style={{ marginBottom: '12px' }}>Quick Stats</div>
              {[
                { label: 'Total Logs', value: totalLogs.toLocaleString() },
                { label: 'Error Rate', value: `${errorRate}%`, highlight: parseFloat(errorRate) > 5 },
                { label: 'Your Role', value: roleMeta.label },
              ].map(item => (
                <div key={item.label} style={{
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  padding: '8px 0', borderBottom: '1px solid var(--border)'
                }}>
                  <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{item.label}</span>
                  <span style={{ fontSize: '13px', fontWeight: 600, color: item.highlight ? 'var(--error)' : 'var(--text-primary)' }}>
                    {item.value || '—'}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Recent Logs */}
          <div className="card">
            <div className="section-header">
              <div>
                <div className="section-title">Recent Logs</div>
                <div className="section-subtitle">Latest 10 log entries</div>
              </div>
            </div>

            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : recentLogs.length === 0 ? (
              <div className="empty-state">
                <svg width="32" height="32" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
                <p>No logs yet. SDK will start sending logs shortly.</p>
              </div>
            ) : (
              <div className="table-wrap" style={{ border: 'none' }}>
                <table className="log-table">
                  <thead>
                    <tr>
                      <th>LEVEL</th>
                      <th>MESSAGE</th>
                      <th>MODULE</th>
                      <th>TIME</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentLogs.map(log => (
                      <tr key={log.id}>
                        <td><span className={`badge badge-${log.level}`}>{log.level}</span></td>
                        <td><span className="log-message">{log.message}</span></td>
                        <td><span className="log-module">{log.subModuleId || log.moduleId || '—'}</span></td>
                        <td><span className="log-time">{formatTime(log.timestamp)}</span></td>
                      </tr>
                    ))}
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

export default Dashboard

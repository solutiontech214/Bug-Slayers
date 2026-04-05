import React, { useState, useEffect, useCallback } from 'react'
import Sidebar from '../components/Sidebar'

const API = 'http://localhost:8080'

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('token')}`
})

const Issues = () => {
  const [issues, setIssues] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [selectedIssue, setSelectedIssue] = useState(null)
  const [updating, setUpdating] = useState(false)
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  const fetchIssues = useCallback(async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API}/issues?page=${page}&size=20`, { headers: getAuthHeaders() })
      if (res.ok) {
        const data = await res.json()
        setIssues(data.content || [])
        setTotalPages(data.totalPages || 0)
        setTotalElements(data.totalElements || 0)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [page])

  useEffect(() => { fetchIssues() }, [fetchIssues])

  const handleResolve = async (issue) => {
    setUpdating(true)
    try {
      const res = await fetch(`${API}/issues/${issue.id}/status`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({ status: 'RESOLVED' })
      })
      if (res.ok) {
        fetchIssues()
        setSelectedIssue(null)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setUpdating(false)
    }
  }

  const handleMarkInProgress = async (issue) => {
    setUpdating(true)
    try {
      const res = await fetch(`${API}/issues/${issue.id}/status`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({ status: 'IN_PROGRESS' })
      })
      if (res.ok) {
        fetchIssues()
        setSelectedIssue(null)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setUpdating(false)
    }
  }

  const formatTime = (ts) => {
    if (!ts) return '—'
    try { return new Date(ts).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'short' }) }
    catch { return ts }
  }

  const openCount = issues.filter(i => i.status === 'OPEN').length
  const inProgCount = issues.filter(i => i.status === 'IN_PROGRESS').length
  const resolvedCount = issues.filter(i => i.status === 'RESOLVED').length

  return (
    <div className="portal-layout">
      <Sidebar />
      <div className="main-content">
        <div className="topbar">
          <span className="topbar-title">Issues</span>
          <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            {totalElements > 0 ? `${totalElements} total` : 'Auto-created from ERROR/FATAL logs'}
          </span>
        </div>

        <div className="page-body">
          {/* Summary */}
          <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(3, 1fr)' }}>
            {[
              { label: 'Open', value: openCount, cls: 'error', icon: '🔴' },
              { label: 'In Progress', value: inProgCount, cls: 'warn', icon: '🟡' },
              { label: 'Resolved', value: resolvedCount, cls: 'success', icon: '🟢' },
            ].map(c => (
              <div key={c.label} className="stat-card">
                <div className={`stat-icon ${c.cls}`}>{c.icon}</div>
                <div>
                  <div className="stat-value" style={{ color: `var(--${c.cls})` }}>{c.value}</div>
                  <div className="stat-label">{c.label}</div>
                </div>
              </div>
            ))}
          </div>

          {/* Issues Table */}
          <div className="table-wrap">
            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : issues.length === 0 ? (
              <div className="empty-state">
                <svg width="36" height="36" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <p>No issues yet. Issues are auto-created from ERROR and FATAL logs.</p>
              </div>
            ) : (
              <table className="log-table">
                <thead>
                  <tr>
                    <th style={{ width: '90px' }}>SEVERITY</th>
                    <th>TITLE</th>
                    <th>PROJECT</th>
                    <th>MODULE</th>
                    <th style={{ width: '110px' }}>STATUS</th>
                    <th>ASSIGNED TO</th>
                    <th>CREATED</th>
                    <th style={{ width: '80px' }}>ACTIONS</th>
                  </tr>
                </thead>
                <tbody>
                  {issues.map(issue => (
                    <tr key={issue.id} style={{ cursor: 'pointer' }} onClick={() => setSelectedIssue(issue)}>
                      <td><span className={`badge badge-${issue.severity}`}>{issue.severity}</span></td>
                      <td>
                        <span className="log-message" style={{ maxWidth: '280px' }}>{issue.title}</span>
                      </td>
                      <td><span className="log-module">{issue.projectId || '—'}</span></td>
                      <td><span className="log-module">{issue.subModuleId || '—'}</span></td>
                      <td><span className={`badge badge-${issue.status}`}>{issue.status?.replace('_', ' ')}</span></td>
                      <td>
                        <span style={{ fontSize: '12px', color: issue.assignedTo ? 'var(--text-primary)' : 'var(--text-muted)' }}>
                          {issue.assignedTo?.name || 'Unassigned'}
                        </span>
                      </td>
                      <td><span className="log-time">{formatTime(issue.createdAt)}</span></td>
                      <td onClick={e => e.stopPropagation()}>
                        {issue.status !== 'RESOLVED' && (
                          <button
                            className="btn btn-success"
                            style={{ padding: '4px 8px', fontSize: '11px' }}
                            onClick={() => handleResolve(issue)}
                            disabled={updating}
                          >
                            Resolve
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="pagination">
              <button className="page-btn" onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>‹</button>
              {Array.from({ length: Math.min(5, totalPages) }, (_, i) => (
                <button key={i} className={`page-btn ${i === page ? 'active' : ''}`} onClick={() => setPage(i)}>{i + 1}</button>
              ))}
              <button className="page-btn" onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page === totalPages - 1}>›</button>
            </div>
          )}
        </div>
      </div>

      {/* Issue Detail Modal */}
      {selectedIssue && (
        <div className="modal-backdrop" onClick={() => setSelectedIssue(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
              <h2 className="modal-title" style={{ flex: 1, marginRight: '12px' }}>Issue #{selectedIssue.id}</h2>
              <button className="btn btn-ghost" style={{ padding: '4px 8px' }} onClick={() => setSelectedIssue(null)}>✕</button>
            </div>

            <div style={{ display: 'flex', gap: '8px', marginBottom: '16px', flexWrap: 'wrap' }}>
              <span className={`badge badge-${selectedIssue.severity}`}>{selectedIssue.severity}</span>
              <span className={`badge badge-${selectedIssue.status}`}>{selectedIssue.status?.replace('_', ' ')}</span>
            </div>

            <p style={{ fontSize: '14px', color: 'var(--text-primary)', marginBottom: '16px', lineHeight: '1.6' }}>
              {selectedIssue.title}
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '16px' }}>
              {[
                ['Project', selectedIssue.projectId],
                ['Module', selectedIssue.subModuleId],
                ['Assigned To', selectedIssue.assignedTo?.name || 'Unassigned'],
                ['Created', formatTime(selectedIssue.createdAt)],
                ['Resolved', formatTime(selectedIssue.resolvedAt)],
              ].map(([k, v]) => (
                <div key={k}>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '2px' }}>{k}</div>
                  <div style={{ fontSize: '13px', color: 'var(--text-primary)' }}>{v || '—'}</div>
                </div>
              ))}
            </div>

            {selectedIssue.stackTrace && (
              <div style={{ marginBottom: '16px' }}>
                <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '6px' }}>Stack Trace</div>
                <div className="stack-trace">{selectedIssue.stackTrace}</div>
              </div>
            )}

            {selectedIssue.status !== 'RESOLVED' && (
              <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                {selectedIssue.status === 'OPEN' && (
                  <button
                    className="btn btn-ghost"
                    onClick={() => handleMarkInProgress(selectedIssue)}
                    disabled={updating}
                  >
                    Mark In Progress
                  </button>
                )}
                <button
                  className="btn btn-success"
                  onClick={() => handleResolve(selectedIssue)}
                  disabled={updating}
                >
                  ✓ Mark Resolved
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

export default Issues

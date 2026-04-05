import React, { useState, useEffect, useCallback } from 'react'
import Sidebar from '../components/Sidebar'

const API = 'http://localhost:8080'

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('token')}`
})

const Logs = () => {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [level, setLevel] = useState('')
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [expandedLog, setExpandedLog] = useState(null)

  const fetchLogs = useCallback(async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams({ page, size: 50 })
      if (level) params.set('level', level)
      if (search) params.set('search', search)

      const res = await fetch(`${API}/logs?${params}`, { headers: getAuthHeaders() })
      if (res.ok) {
        const data = await res.json()
        if (data.content !== undefined) {
          setLogs(data.content)
          setTotalPages(data.totalPages)
          setTotalElements(data.totalElements)
        } else {
          setLogs(Array.isArray(data) ? data : [])
        }
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [page, level, search])

  useEffect(() => { fetchLogs() }, [fetchLogs])

  const handleSearch = (e) => {
    e.preventDefault()
    setSearch(searchInput)
    setPage(0)
  }

  const formatTime = (ts) => {
    if (!ts) return '—'
    return new Date(ts).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'medium' })
  }

  const getLevelColor = (lvl) => {
    const map = { INFO: 'var(--info)', WARN: 'var(--warn)', ERROR: 'var(--error)', FATAL: 'var(--fatal)' }
    return map[lvl] || 'var(--text-secondary)'
  }

  return (
    <div className="portal-layout">
      <Sidebar />
      <div className="main-content">
        <div className="topbar">
          <span className="topbar-title">Log Explorer</span>
          <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            {totalElements > 0 ? `${totalElements.toLocaleString()} total logs` : ''}
          </span>
        </div>

        <div className="page-body">
          {/* Toolbar */}
          <div className="toolbar">
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '8px', flex: 1, minWidth: 0 }}>
              <div className="search-input-wrap">
                <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
                </svg>
                <input
                  type="text"
                  className="search-input"
                  placeholder="Full-text search logs... (PostgreSQL FTS)"
                  value={searchInput}
                  onChange={e => setSearchInput(e.target.value)}
                />
              </div>
              <button type="submit" className="btn btn-primary">Search</button>
              {search && (
                <button type="button" className="btn btn-ghost" onClick={() => { setSearch(''); setSearchInput(''); setPage(0); }}>
                  Clear
                </button>
              )}
            </form>

            <select
              className="filter-select"
              value={level}
              onChange={e => { setLevel(e.target.value); setPage(0); }}
            >
              <option value="">All Levels</option>
              <option value="INFO">INFO</option>
              <option value="WARN">WARN</option>
              <option value="ERROR">ERROR</option>
              <option value="FATAL">FATAL</option>
            </select>

            <button className="btn btn-ghost" onClick={fetchLogs} title="Refresh">
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <polyline points="23 4 23 10 17 10"/>
                <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
              </svg>
              Refresh
            </button>
          </div>

          {/* Table */}
          <div className="table-wrap">
            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : logs.length === 0 ? (
              <div className="empty-state">
                <svg width="36" height="36" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
                <p>No logs found{search ? ` for "${search}"` : ''}{level ? ` with level ${level}` : ''}</p>
              </div>
            ) : (
              <table className="log-table">
                <thead>
                  <tr>
                    <th style={{ width: '80px' }}>LEVEL</th>
                    <th>MESSAGE</th>
                    <th>PROJECT</th>
                    <th>MODULE</th>
                    <th>ENV</th>
                    <th>TIME</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map(log => (
                    <React.Fragment key={log.id}>
                      <tr
                        style={{ cursor: 'pointer' }}
                        onClick={() => setExpandedLog(expandedLog === log.id ? null : log.id)}
                      >
                        <td><span className={`badge badge-${log.level}`}>{log.level}</span></td>
                        <td>
                          <span className="log-message">{log.message}</span>
                          {log.stackTrace && (
                            <span style={{ marginLeft: '6px', fontSize: '10px', color: 'var(--error)', cursor: 'pointer' }}>
                              [stack trace]
                            </span>
                          )}
                        </td>
                        <td><span className="log-module">{log.projectId || '—'}</span></td>
                        <td><span className="log-module">{log.subModuleId || log.moduleId || '—'}</span></td>
                        <td>
                          <span style={{ fontSize: '11px', color: log.environment === 'production' ? 'var(--fatal)' : 'var(--info)' }}>
                            {log.environment || '—'}
                          </span>
                        </td>
                        <td><span className="log-time">{formatTime(log.timestamp)}</span></td>
                      </tr>
                      {expandedLog === log.id && log.stackTrace && (
                        <tr>
                          <td colSpan={6} style={{ padding: '0 16px 12px', background: 'var(--bg-secondary)' }}>
                            <div className="stack-trace">{log.stackTrace}</div>
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="pagination">
              <button className="page-btn" onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>
                ‹
              </button>
              {Array.from({ length: Math.min(7, totalPages) }, (_, i) => {
                const p = page <= 3 ? i : page - 3 + i
                if (p >= totalPages) return null
                return (
                  <button key={p} className={`page-btn ${p === page ? 'active' : ''}`} onClick={() => setPage(p)}>
                    {p + 1}
                  </button>
                )
              })}
              <button className="page-btn" onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page === totalPages - 1}>
                ›
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default Logs

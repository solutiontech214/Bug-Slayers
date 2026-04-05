import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'

const API = 'http://localhost:8080'

const Login = () => {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const res = await fetch(`${API}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })

      if (!res.ok) {
        setError('Invalid email or password')
        return
      }

      const data = await res.json()
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify({
        name: data.name,
        role: data.role,
        userId: data.userId
      }))
      navigate('/dashboard')
    } catch (err) {
      setError('Cannot connect to server. Make sure the backend is running.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-logo">
          <div className="login-logo-icon">🐛</div>
          <span className="login-logo-text">BugSlayers</span>
        </div>

        <h1 className="login-title">Welcome back</h1>
        <p className="login-sub">Sign in to your developer portal</p>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="email">Email address</label>
            <input
              id="email"
              type="email"
              className="form-input"
              placeholder="you@company.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              autoComplete="email"
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>

        <div style={{ marginTop: '20px', paddingTop: '20px', borderTop: '1px solid var(--border)' }}>
          <p style={{ fontSize: '11px', color: 'var(--text-muted)', textAlign: 'center', marginBottom: '10px', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.8px' }}>
            Default Accounts
          </p>
          {[
            { role: 'Admin', icon: '👑', color: '#fb923c', email: 'admin@bugslayers.com' },
            { role: 'Manager', icon: '📋', color: '#38bdf8', email: 'manager@bugslayers.com' },
            { role: 'Developer', icon: '💻', color: '#4ade80', email: 'dev@bugslayers.com' },
          ].map(({ role, icon, color, email }) => (
            <div key={role} style={{
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
              padding: '6px 10px', borderRadius: '6px',
              background: 'var(--bg-secondary)', marginBottom: '6px',
            }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '11px', color, fontWeight: 600 }}>
                {icon} {role}
              </span>
              <code style={{ fontSize: '10px', color: 'var(--text-muted)', background: 'var(--bg-primary)', padding: '2px 6px', borderRadius: '3px' }}>
                {email}
              </code>
            </div>
          ))}
          <p style={{ fontSize: '10px', color: 'var(--text-muted)', textAlign: 'center', marginTop: '8px' }}>
            Password: <code style={{ background: 'var(--bg-secondary)', padding: '1px 5px', borderRadius: '3px' }}>admin123</code>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login
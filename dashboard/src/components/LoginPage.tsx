import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const MANAGER_IDS = ['MGR001', 'MGR002'];

export default function LoginPage() {
  const [staffId, setStaffId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleLogin() {
    const id = staffId.trim().toUpperCase();

    if (!id) {
      setError('Please enter your Staff ID');
      return;
    }
    if (!id.startsWith('EMP') && !id.startsWith('MGR')) {
      setError('Staff ID must start with EMP or MGR');
      return;
    }
    if (!password) {
      setError('Please enter your password');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ staffNumber: id, password }),
      });

      if (!res.ok) {
        setError('Invalid Staff ID or password.');
        setLoading(false);
        return;
      }

      if (MANAGER_IDS.includes(id)) {
        navigate(`/manager/${id}`);
      } else {
        navigate(`/dashboard/${id}`);
      }
    } catch {
      setError('Cannot connect to server. Please try again.');
    }

    setLoading(false);
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (e.key === 'Enter') handleLogin();
  }

  return (
    <div style={{
      minHeight: '100vh',
      background: '#060a10',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontFamily: "'DM Mono', monospace",
    }}>
      {/* Noise overlay */}
      <div style={{
        position: 'fixed',
        inset: 0,
        backgroundImage: 'url("data:image/svg+xml,%3Csvg viewBox=\'0 0 256 256\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cfilter id=\'noise\'%3E%3CfeTurbulence type=\'fractalNoise\' baseFrequency=\'0.9\' numOctaves=\'4\' stitchTiles=\'stitch\'/%3E%3C/filter%3E%3Crect width=\'100%25\' height=\'100%25\' filter=\'url(%23noise)\' opacity=\'0.03\'/%3E%3C/svg%3E")',
        pointerEvents: 'none',
        zIndex: 0,
      }} />

      <div style={{
        position: 'relative',
        zIndex: 1,
        width: '100%',
        maxWidth: '400px',
        padding: '0 24px',
      }}>
        {/* Logo / Title */}
        <div style={{ textAlign: 'center', marginBottom: '48px' }}>
          <div style={{
            width: '48px',
            height: '48px',
            background: '#00ffb322',
            border: '1px solid #00ffb344',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 20px',
            fontSize: '1.4rem',
          }}>
            ⏱
          </div>
          <h1 style={{
            fontFamily: "'Syne', sans-serif",
            fontSize: '1.6rem',
            fontWeight: 800,
            color: '#f9fafb',
            margin: '0 0 8px',
            letterSpacing: '-0.02em',
          }}>
            Attendance
          </h1>
          <p style={{
            fontSize: '0.75rem',
            color: '#4b5563',
            margin: 0,
          }}>
            Sign in with your Staff ID and password
          </p>
        </div>

        {/* Card */}
        <div style={{
          background: '#0d1117',
          border: '1px solid #1f2937',
          borderRadius: '16px',
          padding: '32px',
        }}>
          {/* Staff ID field */}
          <label style={{
            display: 'block',
            fontFamily: "'Syne', sans-serif",
            fontSize: '0.6rem',
            fontWeight: 600,
            letterSpacing: '0.15em',
            textTransform: 'uppercase',
            color: '#4b5563',
            marginBottom: '8px',
          }}>
            Staff ID
          </label>
          <input
            value={staffId}
            onChange={e => { setStaffId(e.target.value); setError(''); }}
            onKeyDown={handleKeyDown}
            placeholder="e.g. EMP001"
            autoFocus
            style={{
              width: '100%',
              background: '#060a10',
              border: `1px solid ${error ? '#f87171' : '#1f2937'}`,
              borderRadius: '8px',
              padding: '12px 14px',
              color: '#f9fafb',
              fontFamily: "'DM Mono', monospace",
              fontSize: '0.9rem',
              outline: 'none',
              boxSizing: 'border-box',
              letterSpacing: '0.05em',
              transition: 'border-color 0.2s',
              marginBottom: '16px',
            }}
            onFocus={e => { if (!error) e.target.style.borderColor = '#00ffb344'; }}
            onBlur={e => { if (!error) e.target.style.borderColor = '#1f2937'; }}
          />

          {/* Password field */}
          <label style={{
            display: 'block',
            fontFamily: "'Syne', sans-serif",
            fontSize: '0.6rem',
            fontWeight: 600,
            letterSpacing: '0.15em',
            textTransform: 'uppercase',
            color: '#4b5563',
            marginBottom: '8px',
          }}>
            Password
          </label>
          <input
            type="password"
            value={password}
            onChange={e => { setPassword(e.target.value); setError(''); }}
            onKeyDown={handleKeyDown}
            placeholder="••••••••"
            style={{
              width: '100%',
              background: '#060a10',
              border: `1px solid ${error ? '#f87171' : '#1f2937'}`,
              borderRadius: '8px',
              padding: '12px 14px',
              color: '#f9fafb',
              fontFamily: "'DM Mono', monospace",
              fontSize: '0.9rem',
              outline: 'none',
              boxSizing: 'border-box',
              letterSpacing: '0.1em',
              transition: 'border-color 0.2s',
            }}
            onFocus={e => { if (!error) e.target.style.borderColor = '#00ffb344'; }}
            onBlur={e => { if (!error) e.target.style.borderColor = '#1f2937'; }}
          />

          {error && (
            <p style={{
              fontSize: '0.7rem',
              color: '#f87171',
              margin: '8px 0 0',
              fontFamily: "'DM Mono', monospace",
            }}>
              ⚠ {error}
            </p>
          )}

          <button
            onClick={handleLogin}
            disabled={loading}
            style={{
              width: '100%',
              marginTop: '20px',
              background: loading ? '#1f2937' : '#00ffb3',
              border: 'none',
              borderRadius: '8px',
              padding: '12px',
              color: loading ? '#4b5563' : '#060a10',
              fontFamily: "'Syne', sans-serif",
              fontSize: '0.75rem',
              fontWeight: 700,
              letterSpacing: '0.1em',
              textTransform: 'uppercase',
              cursor: loading ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s',
            }}
          >
            {loading ? 'Signing in...' : 'Sign In →'}
          </button>
        </div>

        {/* Tap screen link */}
        <div style={{ textAlign: 'center', marginTop: '24px' }}>
          <a href="/tap" style={{
            fontSize: '0.65rem',
            color: '#374151',
            textDecoration: 'none',
            fontFamily: "'DM Mono', monospace",
          }}>
            Office kiosk? → <span style={{ color: '#4b5563' }}>Tap Screen</span>
          </a>
        </div>
      </div>

      <style>{`
        * { box-sizing: border-box; }
        body { margin: 0; }
      `}</style>
    </div>
  );
}

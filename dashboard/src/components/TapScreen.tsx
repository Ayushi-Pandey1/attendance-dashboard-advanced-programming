import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function TapScreen() {
  const [staffId, setStaffId] = useState('');
  const [message, setMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function sendEvent(type: 'IN' | 'OUT') {
    const id = staffId.trim().toUpperCase();
    if (!id) {
      setMessage({ text: 'Please enter your Staff ID', type: 'error' });
      return;
    }
    if (!id.startsWith('EMP')) {
      setMessage({ text: 'Staff ID must start with EMP', type: 'error' });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      const res = await fetch('http://localhost:8080/api/attendance/tap', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ employeeId: id, eventType: type }),
      });

      if (res.ok) {
        // Format time as HH:MM:SS — no milliseconds
        const now = new Date();
        const timeStr = now.toLocaleTimeString('en-GB', {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        });
        setMessage({
          text: `Tap ${type} recorded for ${id} at ${timeStr}`,
          type: 'success',
        });
        setStaffId('');
      } else {
        setMessage({ text: 'Failed to record tap. Try again.', type: 'error' });
      }
    } catch {
      setMessage({ text: 'Cannot connect to server.', type: 'error' });
    }

    setLoading(false);
  }

  return (
    <div style={{
      minHeight: '100vh',
      background: '#060a10',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      fontFamily: "'DM Mono', monospace",
      color: '#e5e7eb',
    }}>
      {/* Noise overlay */}
      <div style={{
        position: 'fixed',
        inset: 0,
        backgroundImage: 'url("data:image/svg+xml,%3Csvg viewBox=\'0 0 256 256\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cfilter id=\'noise\'%3E%3CfeTurbulence type=\'fractalNoise\' baseFrequency=\'0.9\' numOctaves=\'4\' stitchTiles=\'stitch\'/%3E%3C/filter%3E%3Crect width=\'100%25\' height=\'100%25\' filter=\'url(%23noise)\' opacity=\'0.03\'/%3E%3C/svg%3E")',
        pointerEvents: 'none',
        zIndex: 0,
      }} />

      <div style={{ position: 'relative', zIndex: 1, width: '100%', maxWidth: '420px', padding: '0 24px' }}>

        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '48px' }}>
          <div style={{
            width: '64px',
            height: '64px',
            background: '#00ffb311',
            border: '1px solid #00ffb333',
            borderRadius: '16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 20px',
            fontSize: '1.8rem',
          }}>
            🏢
          </div>
          <h1 style={{
            fontFamily: "'Syne', sans-serif",
            fontSize: '1.8rem',
            fontWeight: 800,
            color: '#f9fafb',
            margin: '0 0 8px',
            letterSpacing: '-0.02em',
          }}>
            Office Kiosk
          </h1>
          <p style={{ fontSize: '0.75rem', color: '#4b5563', margin: 0 }}>
            Tap in or out using your Staff ID
          </p>
        </div>

        {/* Card */}
        <div style={{
          background: '#0d1117',
          border: '1px solid #1f2937',
          borderRadius: '16px',
          padding: '32px',
        }}>
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
            onChange={e => {
              setStaffId(e.target.value);
              setMessage(null);
            }}
            onKeyDown={e => e.key === 'Enter' && sendEvent('IN')}
            placeholder="e.g. EMP001"
            autoFocus
            style={{
              width: '100%',
              background: '#060a10',
              border: '1px solid #1f2937',
              borderRadius: '8px',
              padding: '14px',
              color: '#f9fafb',
              fontFamily: "'DM Mono', monospace",
              fontSize: '1rem',
              outline: 'none',
              boxSizing: 'border-box',
              letterSpacing: '0.08em',
              textAlign: 'center',
            }}
          />

          {/* Message */}
          {message && (
            <div style={{
              marginTop: '12px',
              padding: '10px 14px',
              borderRadius: '8px',
              background: message.type === 'success' ? '#00ffb311' : '#f8717111',
              border: `1px solid ${message.type === 'success' ? '#00ffb333' : '#f8717133'}`,
              color: message.type === 'success' ? '#00ffb3' : '#f87171',
              fontSize: '0.75rem',
              textAlign: 'center',
            }}>
              {message.type === 'success' ? '✓ ' : '⚠ '}{message.text}
            </div>
          )}

          {/* Tap buttons */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginTop: '20px' }}>
            <button
              onClick={() => sendEvent('IN')}
              disabled={loading}
              style={{
                padding: '16px',
                background: '#00ffb322',
                border: '1px solid #00ffb344',
                borderRadius: '10px',
                color: '#00ffb3',
                fontFamily: "'Syne', sans-serif",
                fontSize: '0.8rem',
                fontWeight: 700,
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                cursor: loading ? 'not-allowed' : 'pointer',
                transition: 'all 0.15s',
              }}
            >
              ↓ Tap In
            </button>
            <button
              onClick={() => sendEvent('OUT')}
              disabled={loading}
              style={{
                padding: '16px',
                background: '#f8717122',
                border: '1px solid #f8717144',
                borderRadius: '10px',
                color: '#f87171',
                fontFamily: "'Syne', sans-serif",
                fontSize: '0.8rem',
                fontWeight: 700,
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                cursor: loading ? 'not-allowed' : 'pointer',
                transition: 'all 0.15s',
              }}
            >
              ↑ Tap Out
            </button>
          </div>
        </div>

        {/* Back to login */}
        <div style={{ textAlign: 'center', marginTop: '24px' }}>
          <button
            onClick={() => navigate('/')}
            style={{
              background: 'none',
              border: 'none',
              color: '#374151',
              fontFamily: "'DM Mono', monospace",
              fontSize: '0.65rem',
              cursor: 'pointer',
            }}
          >
            ← back to login
          </button>
        </div>
      </div>

      <style>{`
        * { box-sizing: border-box; }
        body { margin: 0; }
      `}</style>
    </div>
  );
}

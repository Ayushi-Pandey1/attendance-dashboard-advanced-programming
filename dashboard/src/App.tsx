import { useState } from 'react';
import { Routes, Route, useParams, useNavigate } from 'react-router-dom';
import { useAttendanceData } from './hooks/useAttendanceData';
import { StatusBadge } from './components/StatusBadge';
import { LiveTimerCard } from './components/LiveTimerCard';
import { AttendanceTable } from './components/AttendanceTable';
import { Charts } from './components/Charts';
import { StatsRow } from './components/StatsRow';
import LoginPage from './components/LoginPage';
import ManagerView from './components/ManagerView';
import TapScreen from './components/TapScreen';
import type { AttendanceStatus } from './types';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/dashboard/:employeeId" element={<Dashboard />} />
      <Route path="/manager/:managerId" element={<ManagerView />} />
      <Route path="/tap" element={<TapScreen />} />
    </Routes>
  );
}

function Dashboard() {
  const { employeeId } = useParams<{ employeeId: string }>();
  const navigate = useNavigate();
  const id = employeeId ?? 'EMP001';

  const { todayStatus, history, monthData, loading, error, refresh } = useAttendanceData(id);
  const [tab, setTab] = useState<'overview'>('overview');
  const [showAllRecords, setShowAllRecords] = useState(false);

  const now = new Date();
  const dateStr = now.toLocaleDateString('en-GB', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  });

  const displayedRecords = showAllRecords ? history : history.slice(0, 10);

  return (
    <div style={{
      minHeight: '100vh',
      background: '#060a10',
      color: '#e5e7eb',
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

      <div style={{ position: 'relative', zIndex: 1, maxWidth: 1100, margin: '0 auto', padding: '40px 24px' }}>

        {/* Header */}
        <header style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          marginBottom: '40px',
        }}>
          <div>
            <p style={{
              fontFamily: "'Syne', sans-serif",
              fontSize: '0.65rem',
              fontWeight: 600,
              letterSpacing: '0.2em',
              textTransform: 'uppercase',
              color: '#374151',
              margin: '0 0 6px',
            }}>
              Attendance Dashboard
            </p>
            <h1 style={{
              fontFamily: "'Syne', sans-serif",
              fontSize: '2rem',
              fontWeight: 800,
              color: '#f9fafb',
              margin: '0 0 4px',
              letterSpacing: '-0.02em',
            }}>
              {id}
            </h1>
            <p style={{
              fontFamily: "'DM Mono', monospace",
              fontSize: '0.75rem',
              color: '#4b5563',
              margin: '0',
            }}>
              Employee
            </p>
          </div>

          <div style={{ textAlign: 'right' }}>
            {todayStatus && (
              <div style={{ marginBottom: '8px' }}>
                <StatusBadge status={todayStatus.status as AttendanceStatus} />
              </div>
            )}
            <p style={{
              fontFamily: "'DM Mono', monospace",
              fontSize: '0.7rem',
              color: '#374151',
              margin: '0 0 8px',
            }}>
              {dateStr}
            </p>
            <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
              <button
                onClick={refresh}
                style={{
                  background: 'none',
                  border: '1px solid #1f2937',
                  borderRadius: '6px',
                  color: '#4b5563',
                  fontFamily: "'DM Mono', monospace",
                  fontSize: '0.65rem',
                  padding: '4px 10px',
                  cursor: 'pointer',
                }}
              >
                ↻ refresh
              </button>
              <button
                onClick={() => navigate('/')}
                style={{
                  background: 'none',
                  border: '1px solid #1f2937',
                  borderRadius: '6px',
                  color: '#4b5563',
                  fontFamily: "'DM Mono', monospace",
                  fontSize: '0.65rem',
                  padding: '4px 10px',
                  cursor: 'pointer',
                }}
              >
                ← sign out
              </button>
            </div>
          </div>
        </header>

        {/* Error banner */}
        {error && (
          <div style={{
            background: '#1a0808',
            border: '1px solid #f8717144',
            borderRadius: '10px',
            padding: '12px 16px',
            marginBottom: '24px',
            fontFamily: "'DM Mono', monospace",
            fontSize: '0.75rem',
            color: '#f87171',
          }}>
            ⚠ {error}
          </div>
        )}

        {loading && !error && (
          <div style={{
            textAlign: 'center',
            padding: '80px',
            color: '#374151',
            fontFamily: "'DM Mono', monospace",
            fontSize: '0.8rem',
          }}>
            loading...
          </div>
        )}

        {!loading && !error && (
          <>
            {/* Top row */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: '1.5fr 1fr',
              gap: '16px',
              marginBottom: '16px',
            }}>
              <LiveTimerCard
                lastInTime={todayStatus?.lastInTime ?? null}
                status={todayStatus?.status ?? 'NO_DATA'}
              />

              <div style={{
                background: '#0d1117',
                border: '1px solid #1f2937',
                borderRadius: '16px',
                padding: '32px',
              }}>
                <p style={{
                  fontFamily: "'Syne', sans-serif",
                  fontSize: '0.65rem',
                  fontWeight: 600,
                  letterSpacing: '0.15em',
                  textTransform: 'uppercase',
                  color: '#4b5563',
                  margin: '0 0 16px',
                }}>
                  Today's Record
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  <Row label="Status" value={
                    todayStatus
                      ? <StatusBadge status={todayStatus.status as AttendanceStatus} />
                      : '—'
                  } />
                  <Row
                    label="Duration"
                    value={
                      todayStatus?.duration === 'ONGOING'
                        ? <span style={{ color: '#00ffb3' }}>Ongoing</span>
                        : (todayStatus?.duration ?? '—')
                    }
                  />
                  <Row
                    label="Last Check-in"
                    value={
                      todayStatus?.lastInTime
                        ? new Date(todayStatus.lastInTime).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
                        : '—'
                    }
                  />
                </div>
              </div>
            </div>

            <div style={{ marginBottom: '16px' }}>
              <StatsRow history={history} />
            </div>

            <div style={{ marginBottom: '16px' }}>
              <Charts monthData={monthData} history={history} />
            </div>

            {/* Overview table */}
            <div style={{
              background: '#0d1117',
              border: '1px solid #1f2937',
              borderRadius: '16px',
              overflow: 'hidden',
            }}>
              <div style={{ display: 'flex', borderBottom: '1px solid #1f2937' }}>
                {(['overview'] as const).map(t => (
                  <button
                    key={t}
                    onClick={() => setTab(t)}
                    style={{
                      padding: '12px 24px',
                      background: 'none',
                      border: 'none',
                      borderBottom: tab === t ? '2px solid #00ffb3' : '2px solid transparent',
                      color: tab === t ? '#00ffb3' : '#4b5563',
                      fontFamily: "'Syne', sans-serif",
                      fontSize: '0.65rem',
                      fontWeight: 600,
                      letterSpacing: '0.12em',
                      textTransform: 'uppercase',
                      cursor: 'pointer',
                    }}
                  >
                    {t}
                  </button>
                ))}
              </div>
              <div style={{ padding: '8px 0' }}>
                {tab === 'overview' && (
                  <>
                    <AttendanceTable records={displayedRecords} />
                    {history.length > 10 && (
                      <div style={{
                        padding: '12px 24px',
                        borderTop: '1px solid #111827',
                        display: 'flex',
                        justifyContent: 'center',
                      }}>
                        <button
                          onClick={() => setShowAllRecords(prev => !prev)}
                          style={{
                            background: 'none',
                            border: '1px solid #1f2937',
                            borderRadius: '6px',
                            color: '#4b5563',
                            fontFamily: "'DM Mono', monospace",
                            fontSize: '0.65rem',
                            padding: '6px 16px',
                            cursor: 'pointer',
                            letterSpacing: '0.05em',
                          }}
                        >
                          {showAllRecords
                            ? `↑ show less`
                            : `↓ show all ${history.length} records`}
                        </button>
                      </div>
                    )}
                  </>
                )}
              </div>
            </div>
          </>
        )}
      </div>

      <style>{`
        @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }
        @keyframes shimmer { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }
        * { box-sizing: border-box; }
        body { margin: 0; }
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #060a10; }
        ::-webkit-scrollbar-thumb { background: #1f2937; border-radius: 3px; }
      `}</style>
    </div>
  );
}

function Row({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <span style={{
        fontFamily: "'Syne', sans-serif",
        fontSize: '0.6rem',
        fontWeight: 600,
        letterSpacing: '0.1em',
        textTransform: 'uppercase',
        color: '#374151',
      }}>
        {label}
      </span>
      <span style={{
        fontFamily: "'DM Mono', monospace",
        fontSize: '0.8rem',
        color: '#9ca3af',
      }}>
        {value}
      </span>
    </div>
  );
}

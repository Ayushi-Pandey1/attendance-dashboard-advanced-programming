import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

interface EmployeeSummary {
  employeeId: string;
  todayStatus: string;
  daysPresent: number;
  daysPartial: number;
  daysIncomplete: number;
  lastSeen: string | null;
}

// The employees this manager oversees
const TEAM_MEMBERS = ['EMP001', 'EMP002', 'EMP003'];

const STATUS_COLORS: Record<string, string> = {
  PRESENT: '#00ffb3',
  PARTIAL: '#facc15',
  IN_OFFICE: '#60a5fa',
  INCOMPLETE: '#f87171',
  NO_DATA: '#374151',
  INVALID: '#9ca3af',
};

export default function ManagerView() {
  const { managerId } = useParams<{ managerId: string }>();
  const navigate = useNavigate();
  const [team, setTeam] = useState<EmployeeSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchTeam() {
      setLoading(true);
      const results = await Promise.all(
        TEAM_MEMBERS.map(async (empId) => {
          try {
            const [todayRes, historyRes] = await Promise.all([
              fetch(`http://localhost:8080/api/attendance/today/${empId}`),
              fetch(`http://localhost:8080/api/attendance/history/${empId}`),
            ]);
            const today = await todayRes.json();
            const history = await historyRes.json();

            const daysPresent = history.filter((d: any) => d.status === 'PRESENT').length;
            const daysPartial = history.filter((d: any) => d.status === 'PARTIAL').length;
            const daysIncomplete = history.filter((d: any) => d.status === 'INCOMPLETE').length;

            const lastRecord = history.find((d: any) =>
              d.status === 'PRESENT' || d.status === 'PARTIAL' || d.status === 'IN_OFFICE'
            );

            return {
              employeeId: empId,
              todayStatus: today.status ?? 'NO_DATA',
              daysPresent,
              daysPartial,
              daysIncomplete,
              lastSeen: lastRecord?.attendanceDate ?? null,
            };
          } catch {
            return {
              employeeId: empId,
              todayStatus: 'NO_DATA',
              daysPresent: 0,
              daysPartial: 0,
              daysIncomplete: 0,
              lastSeen: null,
            };
          }
        })
      );
      setTeam(results);
      setLoading(false);
    }

    fetchTeam();
  }, []);

  const now = new Date();
  const dateStr = now.toLocaleDateString('en-GB', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  });

  const inOfficeToday = team.filter(e =>
    e.todayStatus === 'IN_OFFICE' || e.todayStatus === 'PRESENT'
  ).length;

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
              Manager Dashboard
            </p>
            <h1 style={{
              fontFamily: "'Syne', sans-serif",
              fontSize: '2rem',
              fontWeight: 800,
              color: '#f9fafb',
              margin: '0 0 4px',
              letterSpacing: '-0.02em',
            }}>
              Team Overview
            </h1>
            <p style={{
              fontSize: '0.75rem',
              color: '#4b5563',
              margin: 0,
            }}>
              {managerId} · Manager
            </p>
          </div>

          <div style={{ textAlign: 'right' }}>
            <p style={{
              fontSize: '0.7rem',
              color: '#374151',
              margin: '0 0 8px',
            }}>
              {dateStr}
            </p>
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
        </header>

        {/* Summary stats */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: '16px',
          marginBottom: '24px',
        }}>
          <StatCard label="In Office Today" value={`${inOfficeToday} / ${team.length}`} color="#00ffb3" />
          <StatCard label="Team Size" value={String(team.length)} color="#60a5fa" />
          <StatCard
            label="Absent Today"
            value={String(team.filter(e => e.todayStatus === 'NO_DATA' || e.todayStatus === 'INCOMPLETE').length)}
            color="#f87171"
          />
        </div>

        {/* Team table */}
        <div style={{
          background: '#0d1117',
          border: '1px solid #1f2937',
          borderRadius: '16px',
          overflow: 'hidden',
        }}>
          <div style={{
            padding: '20px 24px',
            borderBottom: '1px solid #1f2937',
          }}>
            <p style={{
              fontFamily: "'Syne', sans-serif",
              fontSize: '0.65rem',
              fontWeight: 600,
              letterSpacing: '0.15em',
              textTransform: 'uppercase',
              color: '#4b5563',
              margin: 0,
            }}>
              Team Members
            </p>
          </div>

          {loading ? (
            <div style={{ padding: '48px', textAlign: 'center', color: '#374151', fontSize: '0.8rem' }}>
              loading team data...
            </div>
          ) : (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  {['Employee', 'Today', 'Days Present', 'Partial', 'Incomplete', 'Last Seen'].map(h => (
                    <th key={h} style={{
                      padding: '12px 24px',
                      textAlign: 'left',
                      fontFamily: "'Syne', sans-serif",
                      fontSize: '0.6rem',
                      fontWeight: 600,
                      letterSpacing: '0.1em',
                      textTransform: 'uppercase',
                      color: '#374151',
                      borderBottom: '1px solid #1f2937',
                    }}>
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {team.map((emp, i) => (
                  <tr key={emp.employeeId} style={{
                    borderBottom: i < team.length - 1 ? '1px solid #111827' : 'none',
                  }}>
                    <td style={{ padding: '16px 24px' }}>
                      <span style={{
                        fontFamily: "'DM Mono', monospace",
                        fontSize: '0.85rem',
                        color: '#f9fafb',
                        fontWeight: 600,
                      }}>
                        {emp.employeeId}
                      </span>
                    </td>
                    <td style={{ padding: '16px 24px' }}>
                      <span style={{
                        display: 'inline-block',
                        padding: '3px 10px',
                        borderRadius: '6px',
                        background: `${STATUS_COLORS[emp.todayStatus] ?? '#374151'}22`,
                        border: `1px solid ${STATUS_COLORS[emp.todayStatus] ?? '#374151'}44`,
                        color: STATUS_COLORS[emp.todayStatus] ?? '#374151',
                        fontFamily: "'DM Mono', monospace",
                        fontSize: '0.65rem',
                        letterSpacing: '0.05em',
                      }}>
                        {emp.todayStatus}
                      </span>
                    </td>
                    <td style={{ padding: '16px 24px', color: '#00ffb3', fontSize: '0.85rem' }}>
                      {emp.daysPresent}
                    </td>
                    <td style={{ padding: '16px 24px', color: '#facc15', fontSize: '0.85rem' }}>
                      {emp.daysPartial}
                    </td>
                    <td style={{ padding: '16px 24px', color: '#f87171', fontSize: '0.85rem' }}>
                      {emp.daysIncomplete}
                    </td>
                    <td style={{ padding: '16px 24px', color: '#4b5563', fontSize: '0.75rem' }}>
                      {emp.lastSeen ?? '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* View individual employee */}
        <div style={{ marginTop: '16px' }}>
          <p style={{ fontSize: '0.65rem', color: '#374151', marginBottom: '8px' }}>
            View individual employee dashboard:
          </p>
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {TEAM_MEMBERS.map(id => (
              <button
                key={id}
                onClick={() => navigate(`/dashboard/${id}`)}
                style={{
                  background: 'none',
                  border: '1px solid #1f2937',
                  borderRadius: '6px',
                  color: '#9ca3af',
                  fontFamily: "'DM Mono', monospace",
                  fontSize: '0.7rem',
                  padding: '6px 14px',
                  cursor: 'pointer',
                }}
              >
                {id} →
              </button>
            ))}
          </div>
        </div>
      </div>

      <style>{`
        * { box-sizing: border-box; }
        body { margin: 0; }
      `}</style>
    </div>
  );
}

function StatCard({ label, value, color }: { label: string; value: string; color: string }) {
  return (
    <div style={{
      background: '#0d1117',
      border: '1px solid #1f2937',
      borderRadius: '16px',
      padding: '24px',
    }}>
      <p style={{
        fontFamily: "'Syne', sans-serif",
        fontSize: '0.6rem',
        fontWeight: 600,
        letterSpacing: '0.15em',
        textTransform: 'uppercase',
        color: '#4b5563',
        margin: '0 0 12px',
      }}>
        {label}
      </p>
      <p style={{
        fontFamily: "'Syne', sans-serif",
        fontSize: '2rem',
        fontWeight: 800,
        color,
        margin: 0,
        letterSpacing: '-0.02em',
      }}>
        {value}
      </p>
    </div>
  );
}

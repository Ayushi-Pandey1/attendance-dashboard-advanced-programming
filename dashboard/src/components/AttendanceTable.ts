import type { DailyRecord, AttendanceStatus } from '../types';
import { StatusBadge } from './StatusBadge';

interface Props {
  records: DailyRecord[];
}

const STATUS_COLOR: Record<string, string> = {
  IN_OFFICE:  '#00ffb3',
  PRESENT:    '#4ade80',
  PARTIAL:    '#fbbf24',
  INCOMPLETE: '#f87171',
  INVALID:    '#a78bfa',
  NO_DATA:    '#6b7280',
};

function formatDate(dateStr: string): string {
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' });
}

export function AttendanceTable({ records }: Props) {
  if (records.length === 0) {
    return (
      <div style={{
        textAlign: 'center',
        padding: '48px',
        color: '#374151',
        fontFamily: "'DM Mono', monospace",
        fontSize: '0.85rem',
      }}>
        No attendance records found.
      </div>
    );
  }

  return (
    <div style={{ overflowX: 'auto' }}>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            {['Date', 'Status', 'Duration', 'Notes'].map(h => (
              <th key={h} style={{
                textAlign: 'left',
                padding: '8px 16px',
                fontFamily: "'Syne', sans-serif",
                fontSize: '0.65rem',
                fontWeight: 600,
                letterSpacing: '0.12em',
                textTransform: 'uppercase',
                color: '#374151',
                borderBottom: '1px solid #1f2937',
              }}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {records.map((r, i) => (
            <tr key={r.attendanceDate} style={{
              borderBottom: '1px solid #111827',
              background: i % 2 === 0 ? 'transparent' : '#0a0f1a',
              transition: 'background 0.15s',
            }}
              onMouseEnter={e => (e.currentTarget.style.background = '#111827')}
              onMouseLeave={e => (e.currentTarget.style.background = i % 2 === 0 ? 'transparent' : '#0a0f1a')}
            >
              <td style={{
                padding: '12px 16px',
                fontFamily: "'DM Mono', monospace",
                fontSize: '0.8rem',
                color: '#9ca3af',
              }}>
                {formatDate(r.attendanceDate)}
              </td>
              <td style={{ padding: '12px 16px' }}>
                <StatusBadge status={r.status as AttendanceStatus} />
              </td>
              <td style={{
                padding: '12px 16px',
                fontFamily: "'DM Mono', monospace",
                fontSize: '0.8rem',
                color: r.finalInOfficeDuration ? '#d1d5db' : '#374151',
              }}>
                {r.finalInOfficeDuration === 'ONGOING'
                  ? <span style={{ color: '#00ffb3' }}>ONGOING</span>
                  : r.finalInOfficeDuration ?? '—'}
              </td>
              <td style={{
                padding: '12px 16px',
                fontFamily: "'DM Mono', monospace",
                fontSize: '0.75rem',
                color: '#4b5563',
              }}>
                {r.invalidReason ?? '—'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

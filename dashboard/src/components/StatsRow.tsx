import type { DailyRecord } from '../types';

interface Props {
  history: DailyRecord[];
}

function parseDurationToMinutes(dur: string | null): number {
  if (!dur || dur === 'ONGOING') return 0;
  const h = parseInt(dur.match(/(\d+) hours?/)?.[1] ?? '0');
  const m = parseInt(dur.match(/(\d+) minutes?/)?.[1] ?? '0');
  return h * 60 + m;
}

export function StatsRow({ history }: Props) {
  const total = history.length;
  const present = history.filter(r => r.status === 'PRESENT').length;
  const partial = history.filter(r => r.status === 'PARTIAL').length;
  const incomplete = history.filter(r => r.status === 'INCOMPLETE').length;

  const allMinutes = history
    .filter(r => r.finalInOfficeDuration && r.finalInOfficeDuration !== 'ONGOING')
    .map(r => parseDurationToMinutes(r.finalInOfficeDuration));

  const avgMinutes = allMinutes.length > 0
    ? Math.round(allMinutes.reduce((a, b) => a + b, 0) / allMinutes.length)
    : 0;

  const stats = [
    { label: 'Total Days',    value: String(total),                 color: '#9ca3af' },
    { label: 'Complete',      value: String(present),               color: '#4ade80' },
    { label: 'Partial',       value: String(partial),               color: '#fbbf24' },
    { label: 'Incomplete',    value: String(incomplete),            color: '#f87171' },
    { label: 'Avg Duration',  value: avgMinutes > 0 ? `${Math.floor(avgMinutes/60)}h ${avgMinutes%60}m` : '—', color: '#60a5fa' },
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '12px' }}>
      {stats.map(s => (
        <div key={s.label} style={{
          background: '#0d1117',
          border: '1px solid #1f2937',
          borderRadius: '12px',
          padding: '16px 20px',
        }}>
          <div style={{
            fontFamily: "'DM Mono', monospace",
            fontSize: '1.75rem',
            fontWeight: 300,
            color: s.color,
            lineHeight: 1,
            marginBottom: '6px',
          }}>
            {s.value}
          </div>
          <div style={{
            fontFamily: "'Syne', sans-serif",
            fontSize: '0.6rem',
            fontWeight: 600,
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            color: '#374151',
          }}>
            {s.label}
          </div>
        </div>
      ))}
    </div>
  );
}

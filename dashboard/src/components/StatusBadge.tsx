import type { AttendanceStatus } from '../types';

interface Props {
  status: AttendanceStatus;
}

const CONFIG: Record<AttendanceStatus, { label: string; color: string; dot: string }> = {
  IN_OFFICE:  { label: 'In Office',  color: '#00ffb3', dot: '#00ffb3' },
  PRESENT:    { label: 'Complete',   color: '#4ade80', dot: '#4ade80' },
  PARTIAL:    { label: 'Partial',    color: '#fbbf24', dot: '#fbbf24' },
  INCOMPLETE: { label: 'Incomplete', color: '#f87171', dot: '#f87171' },
  INVALID:    { label: 'Invalid',    color: '#a78bfa', dot: '#a78bfa' },
  NO_DATA:    { label: 'No Data',    color: '#6b7280', dot: '#6b7280' },
};

export function StatusBadge({ status }: Props) {
  const cfg = CONFIG[status] ?? CONFIG['NO_DATA'];
  return (
    <span style={{
      display: 'inline-flex',
      alignItems: 'center',
      gap: '6px',
      padding: '4px 12px',
      borderRadius: '999px',
      border: `1px solid ${cfg.color}33`,
      background: `${cfg.color}15`,
      color: cfg.color,
      fontFamily: "'DM Mono', monospace",
      fontSize: '0.75rem',
      fontWeight: 500,
      letterSpacing: '0.08em',
      textTransform: 'uppercase',
    }}>
      <span style={{
        width: 7,
        height: 7,
        borderRadius: '50%',
        background: cfg.dot,
        boxShadow: status === 'IN_OFFICE' ? `0 0 8px ${cfg.dot}` : 'none',
        animation: status === 'IN_OFFICE' ? 'pulse 2s infinite' : 'none',
      }} />
      {cfg.label}
    </span>
  );
}

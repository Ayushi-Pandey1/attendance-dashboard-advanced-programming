import { useLiveTimer } from '../hooks/useLiveTimer';

interface Props {
  lastInTime: string | null;
  status: string;
}

export function LiveTimerCard({ lastInTime, status }: Props) {
  const elapsed = useLiveTimer(lastInTime);
  const isLive = status === 'IN_OFFICE' && lastInTime;

  return (
    <div style={{
      background: 'linear-gradient(135deg, #0d1117 0%, #111827 100%)',
      border: isLive ? '1px solid #00ffb344' : '1px solid #1f2937',
      borderRadius: '16px',
      padding: '32px',
      position: 'relative',
      overflow: 'hidden',
    }}>
      {isLive && (
        <div style={{
          position: 'absolute',
          top: 0, left: 0, right: 0,
          height: '2px',
          background: 'linear-gradient(90deg, transparent, #00ffb3, transparent)',
          animation: 'shimmer 3s ease-in-out infinite',
        }} />
      )}

      <p style={{
        fontFamily: "'Syne', sans-serif",
        fontSize: '0.7rem',
        fontWeight: 600,
        letterSpacing: '0.15em',
        textTransform: 'uppercase',
        color: '#4b5563',
        margin: '0 0 12px',
      }}>
        Time in Office Today
      </p>

      {isLive ? (
        <div style={{
          fontFamily: "'DM Mono', monospace",
          fontSize: '3.5rem',
          fontWeight: 300,
          color: '#00ffb3',
          letterSpacing: '-0.02em',
          lineHeight: 1,
          textShadow: '0 0 40px #00ffb355',
        }}>
          {elapsed || '—'}
        </div>
      ) : (
        <div style={{
          fontFamily: "'DM Mono', monospace",
          fontSize: '3.5rem',
          fontWeight: 300,
          color: '#1f2937',
          letterSpacing: '-0.02em',
          lineHeight: 1,
        }}>
          ——:——:——
        </div>
      )}

      <p style={{
        fontFamily: "'DM Mono', monospace",
        fontSize: '0.7rem',
        color: isLive ? '#00ffb366' : '#374151',
        margin: '8px 0 0',
      }}>
        {isLive ? 'live · updates every second' : 'not currently in office'}
      </p>
    </div>
  );
}

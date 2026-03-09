import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import type { DailyRecord } from '../types';

interface Props {
  monthData: DailyRecord[];
  history: DailyRecord[];
}

const STATUS_COLORS: Record<string, string> = {
  PRESENT:    '#4ade80',
  PARTIAL:    '#fbbf24',
  INCOMPLETE: '#f87171',
  INVALID:    '#a78bfa',
  IN_OFFICE:  '#00ffb3',
  NO_DATA:    '#374151',
};

function parseDurationToMinutes(dur: string | null): number {
  if (!dur || dur === 'ONGOING') return 0;
  const h = parseInt(dur.match(/(\d+) hours?/)?.[1] ?? '0');
  const m = parseInt(dur.match(/(\d+) minutes?/)?.[1] ?? '0');
  return h * 60 + m;
}

function shortDate(dateStr: string): string {
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString('en-GB', { day: 'numeric', month: 'short' });
}

const CARD_STYLE: React.CSSProperties = {
  background: '#0d1117',
  border: '1px solid #1f2937',
  borderRadius: '16px',
  padding: '24px',
};

const SECTION_TITLE: React.CSSProperties = {
  fontFamily: "'Syne', sans-serif",
  fontSize: '0.65rem',
  fontWeight: 600,
  letterSpacing: '0.15em',
  textTransform: 'uppercase',
  color: '#4b5563',
  margin: '0 0 20px',
};

export function Charts({ monthData, history }: Props) {
  // Bar chart: daily duration this month
  const barData = monthData.map(r => ({
    date: shortDate(r.attendanceDate),
    minutes: parseDurationToMinutes(r.finalInOfficeDuration),
    status: r.status,
  }));

  // Pie chart: status distribution from all history
  const statusCounts: Record<string, number> = {};
  history.forEach(r => {
    const s = r.status ?? 'NO_DATA';
    statusCounts[s] = (statusCounts[s] ?? 0) + 1;
  });
  const pieData = Object.entries(statusCounts)
    .filter(([, v]) => v > 0)
    .map(([name, value]) => ({ name, value }));

  const CustomTooltip = ({ active, payload, label }: any) => {
    if (!active || !payload?.length) return null;
    const mins = payload[0].value as number;
    const h = Math.floor(mins / 60);
    const m = mins % 60;
    return (
      <div style={{
        background: '#111827',
        border: '1px solid #1f2937',
        borderRadius: 8,
        padding: '8px 12px',
        fontFamily: "'DM Mono', monospace",
        fontSize: '0.75rem',
        color: '#d1d5db',
      }}>
        <div style={{ color: '#6b7280', marginBottom: 4 }}>{label}</div>
        <div>{h}h {m}m</div>
      </div>
    );
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>

      {/* Bar Chart */}
      <div style={CARD_STYLE}>
        <p style={SECTION_TITLE}>Daily Duration · This Month</p>
        {barData.length === 0 ? (
          <p style={{ color: '#374151', fontFamily: "'DM Mono', monospace", fontSize: '0.8rem' }}>No data this month.</p>
        ) : (
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={barData} barSize={8} margin={{ top: 0, right: 0, bottom: 0, left: -20 }}>
              <XAxis
                dataKey="date"
                tick={{ fill: '#4b5563', fontFamily: 'DM Mono, monospace', fontSize: 10 }}
                axisLine={false}
                tickLine={false}
              />
              <YAxis
                tick={{ fill: '#4b5563', fontFamily: 'DM Mono, monospace', fontSize: 10 }}
                axisLine={false}
                tickLine={false}
              />
              <Tooltip content={<CustomTooltip />} cursor={{ fill: '#ffffff08' }} />
              <Bar dataKey="minutes" radius={[4, 4, 0, 0]}>
                {barData.map((entry, index) => (
                  <Cell
                    key={index}
                    fill={STATUS_COLORS[entry.status] ?? '#374151'}
                    opacity={0.85}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>

      {/* Pie Chart */}
      <div style={CARD_STYLE}>
        <p style={SECTION_TITLE}>Status Distribution · All Time</p>
        {pieData.length === 0 ? (
          <p style={{ color: '#374151', fontFamily: "'DM Mono', monospace", fontSize: '0.8rem' }}>No data yet.</p>
        ) : (
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie
                data={pieData}
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={80}
                paddingAngle={3}
                dataKey="value"
              >
                {pieData.map((entry, index) => (
                  <Cell
                    key={index}
                    fill={STATUS_COLORS[entry.name] ?? '#374151'}
                    stroke="transparent"
                  />
                ))}
              </Pie>
              <Legend
                iconType="circle"
                iconSize={8}
                formatter={(value) => (
                  <span style={{
                    fontFamily: "'DM Mono', monospace",
                    fontSize: '0.72rem',
                    color: '#9ca3af',
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em',
                  }}>
                    {value.toLowerCase()}
                  </span>
                )}
              />
              <Tooltip
                formatter={(value) => [value, 'Days']}
                contentStyle={{
                  background: '#111827',
                  border: '1px solid #1f2937',
                  borderRadius: 8,
                  fontFamily: 'DM Mono, monospace',
                  fontSize: '0.75rem',
                  color: '#d1d5db',
                }}
              />
            </PieChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}

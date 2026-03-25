import { describe, expect, it, vi, afterEach } from 'vitest';
import {
  fetchHistory,
  fetchMonth,
  fetchTodayStatus,
} from './api';

describe('api utilities', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('fetches today status from the attendance API', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ employeeId: 'EMP001', status: 'NO_DATA', lastInTime: null, duration: null }), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      })
    );

    const result = await fetchTodayStatus('EMP001');

    expect(result.status).toBe('NO_DATA');
  });

  it('fetches history from the attendance API', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify([{ attendanceDate: '2026-03-24', status: 'PRESENT', finalInOfficeDuration: '4 hours 0 minutes', invalidReason: null }]), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      })
    );

    const result = await fetchHistory('EMP001');

    expect(result).toHaveLength(1);
    expect(result[0].status).toBe('PRESENT');
  });

  it('builds the month query correctly', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify([]), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      })
    );

    await fetchMonth('EMP001', 2026, 3);

    expect(fetchMock).toHaveBeenCalledWith('/api/attendance/month/EMP001?year=2026&month=3');
  });
});

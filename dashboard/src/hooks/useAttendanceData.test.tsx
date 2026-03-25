import { act, renderHook, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { useAttendanceData } from './useAttendanceData';

vi.mock('../utils/api', () => ({
  fetchTodayStatus: vi.fn(),
  fetchHistory: vi.fn(),
  fetchMonth: vi.fn(),
}));

import { fetchTodayStatus, fetchHistory, fetchMonth } from '../utils/api';

describe('useAttendanceData', () => {
  afterEach(() => {
    vi.useRealTimers();
    vi.clearAllMocks();
  });

  it('loads attendance data successfully', async () => {
    vi.mocked(fetchTodayStatus).mockResolvedValue({
      employeeId: 'EMP001',
      status: 'PRESENT',
      lastInTime: null,
      duration: '4 hours 0 minutes',
    });
    vi.mocked(fetchHistory).mockResolvedValue([]);
    vi.mocked(fetchMonth).mockResolvedValue([]);

    const { result } = renderHook(() => useAttendanceData('EMP001'));

    await waitFor(() => expect(result.current.loading).toBe(false));

    expect(result.current.todayStatus?.status).toBe('PRESENT');
    expect(result.current.error).toBeNull();
  });

  it('surfaces an error when one of the API calls fails', async () => {
    vi.mocked(fetchTodayStatus).mockRejectedValue(new Error('boom'));
    vi.mocked(fetchHistory).mockResolvedValue([]);
    vi.mocked(fetchMonth).mockResolvedValue([]);

    const { result } = renderHook(() => useAttendanceData('EMP001'));

    await waitFor(() => expect(result.current.loading).toBe(false));

    expect(result.current.error).toContain('Could not connect to backend');
  });

  it('polls for fresh data every 15 seconds', async () => {
    vi.useFakeTimers();

    vi.mocked(fetchTodayStatus).mockResolvedValue({
      employeeId: 'EMP001',
      status: 'NO_DATA',
      lastInTime: null,
      duration: null,
    });
    vi.mocked(fetchHistory).mockResolvedValue([]);
    vi.mocked(fetchMonth).mockResolvedValue([]);

    renderHook(() => useAttendanceData('EMP001'));

    await act(async () => {
      await Promise.resolve();
    });

    expect(fetchTodayStatus).toHaveBeenCalledTimes(1);

    await act(async () => {
      vi.advanceTimersByTime(15000);
      await Promise.resolve();
    });

    expect(fetchTodayStatus).toHaveBeenCalledTimes(2);
  });
});

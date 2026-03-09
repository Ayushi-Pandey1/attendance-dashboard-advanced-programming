import { useState, useEffect, useCallback } from 'react';
import { fetchTodayStatus, fetchHistory, fetchMonth } from '../utils/api';
import type { TodayStatus, DailyRecord } from '../types';

export function useAttendanceData(employeeId: string) {
  const [todayStatus, setTodayStatus] = useState<TodayStatus | null>(null);
  const [history, setHistory] = useState<DailyRecord[]>([]);
  const [monthData, setMonthData] = useState<DailyRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1;

  const refresh = useCallback(async () => {
    try {
      const [today, hist, month] = await Promise.all([
        fetchTodayStatus(employeeId),
        fetchHistory(employeeId),
        fetchMonth(employeeId, currentYear, currentMonth),
      ]);
      setTodayStatus(today);
      setHistory(hist);
      setMonthData(month);
      setError(null);
    } catch (e) {
      setError('Could not connect to backend. Make sure the Spring Boot server is running on port 8080.');
    } finally {
      setLoading(false);
    }
  }, [employeeId, currentYear, currentMonth]);

  useEffect(() => {
    refresh();
    // Poll every 15 seconds to pick up newly processed events
    const id = setInterval(refresh, 15000);
    return () => clearInterval(id);
  }, [refresh]);

  return { todayStatus, history, monthData, loading, error, refresh };
}

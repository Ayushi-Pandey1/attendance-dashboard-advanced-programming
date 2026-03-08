import type { TodayStatus, DailyRecord } from '../types';

const BASE = '/api/attendance';

export async function fetchTodayStatus(employeeId: string): Promise<TodayStatus> {
  const res = await fetch(`${BASE}/today/${employeeId}`);
  if (!res.ok) throw new Error('Failed to fetch today status');
  return res.json();
}

export async function fetchHistory(employeeId: string): Promise<DailyRecord[]> {
  const res = await fetch(`${BASE}/history/${employeeId}`);
  if (!res.ok) throw new Error('Failed to fetch history');
  return res.json();
}

export async function fetchMonth(
  employeeId: string,
  year: number,
  month: number
): Promise<DailyRecord[]> {
  const res = await fetch(`${BASE}/month/${employeeId}?year=${year}&month=${month}`);
  if (!res.ok) throw new Error('Failed to fetch month data');
  return res.json();
}

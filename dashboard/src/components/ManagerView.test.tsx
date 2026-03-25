import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ManagerView from './ManagerView';

function renderManagerView() {
  return render(
    <MemoryRouter initialEntries={['/manager/MGR001']}>
      <Routes>
        <Route path="/manager/:managerId" element={<ManagerView />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('ManagerView', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders team summary data from fetched attendance records', async () => {
    vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(new Response(JSON.stringify({ employeeId: 'EMP001', status: 'IN_OFFICE', lastInTime: null, duration: null }), { status: 200, headers: { 'Content-Type': 'application/json' } }))
      .mockResolvedValueOnce(new Response(JSON.stringify([
        { attendanceDate: '2026-03-23', status: 'PRESENT', finalInOfficeDuration: '4 hours 30 minutes', invalidReason: null },
      ]), { status: 200, headers: { 'Content-Type': 'application/json' } }))
      .mockResolvedValueOnce(new Response(JSON.stringify({ employeeId: 'EMP002', status: 'PRESENT', lastInTime: null, duration: '4 hours 0 minutes' }), { status: 200, headers: { 'Content-Type': 'application/json' } }))
      .mockResolvedValueOnce(new Response(JSON.stringify([
        { attendanceDate: '2026-03-22', status: 'PARTIAL', finalInOfficeDuration: '2 hours 0 minutes', invalidReason: null },
      ]), { status: 200, headers: { 'Content-Type': 'application/json' } }))
      .mockResolvedValueOnce(new Response(JSON.stringify({ employeeId: 'EMP003', status: 'NO_DATA', lastInTime: null, duration: null }), { status: 200, headers: { 'Content-Type': 'application/json' } }))
      .mockResolvedValueOnce(new Response(JSON.stringify([]), { status: 200, headers: { 'Content-Type': 'application/json' } }));

    renderManagerView();

    await waitFor(() => expect(screen.getByText('2 / 3')).toBeInTheDocument());
    expect(screen.getByText('EMP001')).toBeInTheDocument();
    expect(screen.getByText('EMP002')).toBeInTheDocument();
    expect(screen.getByText('EMP003')).toBeInTheDocument();
  });

  it('falls back to no data when employee API calls fail', async () => {
    vi.spyOn(globalThis, 'fetch').mockRejectedValue(new Error('down'));

    renderManagerView();

    await waitFor(() => expect(screen.getByText('0 / 3')).toBeInTheDocument());
    expect(screen.getAllByText('NO_DATA').length).toBeGreaterThan(0);
  });
});

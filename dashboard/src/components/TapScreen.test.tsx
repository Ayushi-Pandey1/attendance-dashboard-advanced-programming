import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import TapScreen from './TapScreen';

function renderTapScreen() {
  return render(
    <MemoryRouter initialEntries={['/tap']}>
      <Routes>
        <Route path="/tap" element={<TapScreen />} />
        <Route path="/" element={<div>Login Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('TapScreen', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows validation when the staff ID is missing', async () => {
    renderTapScreen();

    fireEvent.click(screen.getByRole('button', { name: /tap in/i }));

    expect(await screen.findByText(/Please enter your Staff ID/i)).toBeInTheDocument();
  });

  it('submits a tap-in event successfully', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 200 }));

    renderTapScreen();

    fireEvent.change(screen.getByPlaceholderText('e.g. EMP001'), { target: { value: 'emp001' } });
    fireEvent.click(screen.getByRole('button', { name: /tap in/i }));

    await waitFor(() => expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/attendance/tap', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ employeeId: 'EMP001', eventType: 'IN' }),
    })));
    expect(screen.getByText(/Tap IN recorded for EMP001 at/i)).toBeInTheDocument();
  });

  it('shows an error when tap submission fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 500 }));

    renderTapScreen();

    fireEvent.change(screen.getByPlaceholderText('e.g. EMP001'), { target: { value: 'EMP001' } });
    fireEvent.click(screen.getByRole('button', { name: /tap out/i }));

    expect(await screen.findByText(/Failed to record tap\. Try again\./i)).toBeInTheDocument();
  });
});

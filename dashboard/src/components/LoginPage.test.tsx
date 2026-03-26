import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import LoginPage from './LoginPage';

function renderLoginPage() {
  return render(
    <MemoryRouter initialEntries={['/']}>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/dashboard/:employeeId" element={<div>Employee Dashboard</div>} />
        <Route path="/manager/:managerId" element={<div>Manager Dashboard</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows validation when the staff ID is missing', async () => {
    renderLoginPage();

    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/Please enter your Staff ID/i)).toBeInTheDocument();
  });

  it('logs in an employee and navigates to the dashboard', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 200 }));

    renderLoginPage();

    fireEvent.change(screen.getByPlaceholderText('e.g. EMP001'), { target: { value: 'emp001' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password' } });
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    await screen.findByText('Employee Dashboard');
    expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/auth/login', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ staffNumber: 'EMP001', password: 'password' }),
    }));
  });

  it('logs in a manager and navigates to the manager dashboard', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 200 }));

    renderLoginPage();

    fireEvent.change(screen.getByPlaceholderText('e.g. EMP001'), { target: { value: 'mgr001' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'password' } });
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    await screen.findByText('Manager Dashboard');
  });

  it('shows an error when login fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 401 }));

    renderLoginPage();

    fireEvent.change(screen.getByPlaceholderText('e.g. EMP001'), { target: { value: 'EMP001' } });
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'bad-password' } });
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() =>
      expect(screen.getByText(/Invalid Staff ID or password\./i)).toBeInTheDocument()
    );
  });
});

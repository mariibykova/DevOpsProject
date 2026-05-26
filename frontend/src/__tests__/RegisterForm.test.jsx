import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import RegisterForm from '../components/RegisterForm';

describe('RegisterForm', () => {
  it('renders form with username, email and password fields', () => {
    render(<RegisterForm onRegister={vi.fn()} onSwitchToLogin={vi.fn()} />);

    expect(screen.getByLabelText('Username')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByText('Register', { selector: 'button[type="submit"]' })).toBeInTheDocument();
  });

  it('renders Back to Login button', () => {
    render(<RegisterForm onRegister={vi.fn()} onSwitchToLogin={vi.fn()} />);

    expect(screen.getByText('Back to Login')).toBeInTheDocument();
  });

  it('calls onSwitchToLogin when Back to Login button is clicked', () => {
    const onSwitchToLogin = vi.fn();
    render(<RegisterForm onRegister={vi.fn()} onSwitchToLogin={onSwitchToLogin} />);

    fireEvent.click(screen.getByText('Back to Login'));
    expect(onSwitchToLogin).toHaveBeenCalledTimes(1);
  });

  it('calls onRegister with username, email and password on submit', async () => {
    const onRegister = vi.fn().mockResolvedValue(undefined);
    render(<RegisterForm onRegister={onRegister} onSwitchToLogin={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'newuser' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'new@test.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password123' } });
    fireEvent.submit(screen.getByText('Register', { selector: 'button[type="submit"]' }));

    expect(onRegister).toHaveBeenCalledWith('newuser', 'new@test.com', 'password123');
  });

  it('displays error message on registration failure', async () => {
    const onRegister = vi.fn().mockRejectedValue(new Error('Email already exists'));
    render(<RegisterForm onRegister={onRegister} onSwitchToLogin={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Username'), { target: { value: 'newuser' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'existing@test.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password123' } });
    fireEvent.submit(screen.getByText('Register', { selector: 'button[type="submit"]' }));

    const errorMessage = await screen.findByText('Email already exists');
    expect(errorMessage).toBeInTheDocument();
  });

  it('renders heading with text Register', () => {
    render(<RegisterForm onRegister={vi.fn()} onSwitchToLogin={vi.fn()} />);

    expect(screen.getByText('Register', { selector: 'h2' })).toBeInTheDocument();
  });
});

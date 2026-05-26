import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import LoginForm from '../components/LoginForm';

describe('LoginForm', () => {
  it('renders login form with email and password fields', () => {
    render(<LoginForm onLogin={vi.fn()} onSwitchToRegister={vi.fn()} />);

    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByText('Login', { selector: 'button[type="submit"]' })).toBeInTheDocument();
  });

  it('renders Register button', () => {
    render(<LoginForm onLogin={vi.fn()} onSwitchToRegister={vi.fn()} />);

    expect(screen.getByText('Register')).toBeInTheDocument();
  });

  it('calls onSwitchToRegister when Register button is clicked', () => {
    const onSwitchToRegister = vi.fn();
    render(<LoginForm onLogin={vi.fn()} onSwitchToRegister={onSwitchToRegister} />);

    fireEvent.click(screen.getByText('Register'));
    expect(onSwitchToRegister).toHaveBeenCalledTimes(1);
  });

  it('calls onLogin with email and password on submit', async () => {
    const onLogin = vi.fn().mockResolvedValue(undefined);
    render(<LoginForm onLogin={onLogin} onSwitchToRegister={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'password123' } });
    fireEvent.submit(screen.getByText('Login', { selector: 'button[type="submit"]' }));

    expect(onLogin).toHaveBeenCalledWith('test@test.com', 'password123');
  });

  it('displays error message on login failure', async () => {
    const onLogin = vi.fn().mockRejectedValue(new Error('Invalid credentials'));
    render(<LoginForm onLogin={onLogin} onSwitchToRegister={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'wrong' } });
    fireEvent.submit(screen.getByText('Login', { selector: 'button[type="submit"]' }));

    const errorMessage = await screen.findByText('Invalid credentials');
    expect(errorMessage).toBeInTheDocument();
  });
});

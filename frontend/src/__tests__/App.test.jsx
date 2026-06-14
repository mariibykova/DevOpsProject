import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';
import * as apiClient from '../api/apiClient';

vi.mock('../api/apiClient');

const mockUser = { id: 1, userName: 'testuser', email: 'test@test.com', roles: ['ROLE_USER'] };
const mockProducts = [{ id: 1, name: 'Product 1', description: 'Desc 1', price: 10.0 }];
const mockReviews = [{ id: 1, title: 'Review 1', body: 'Body 1', rating: 5, productId: 1, userId: 1 }];

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
  apiClient.getProducts.mockResolvedValue(mockProducts);
  apiClient.getReviews.mockResolvedValue(mockReviews);
});

describe('App', () => {
  it('renders products page by default', () => {
    render(<App />);
    expect(screen.getByRole('heading', { name: /products/i })).toBeTruthy();
  });

  it('shows login form when clicking login button', () => {
    render(<App />);
    const loginButton = screen.getAllByRole('button', { name: /login/i })[0];
    fireEvent.click(loginButton);
    expect(screen.getByRole('heading', { name: /login/i })).toBeTruthy();
  });

  it('shows register page when switching from login', () => {
    render(<App />);
    const loginButton = screen.getAllByRole('button', { name: /login/i })[0];
    fireEvent.click(loginButton);
    const registerButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(registerButton);
    expect(screen.getByRole('heading', { name: /register/i })).toBeTruthy();
  });

  it('navigates to reviews page', () => {
    render(<App />);
    const reviewsButton = screen.getByRole('button', { name: /reviews/i });
    fireEvent.click(reviewsButton);
    expect(screen.getByRole('heading', { name: /reviews/i })).toBeTruthy();
  });

  it('logs out user', async () => {
    localStorage.setItem('token', 'abc123');
    localStorage.setItem('user', JSON.stringify(mockUser));
    
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByText('testuser')).toBeTruthy();
    });
    
    const logoutButton = screen.getByRole('button', { name: /logout/i });
    fireEvent.click(logoutButton);
    
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('handles login and stores sanitized data', async () => {
    const loginResponse = { token: 'jwt.token.here', user: mockUser };
    apiClient.login.mockResolvedValue(loginResponse);

    render(<App />);
    const loginButton = screen.getAllByRole('button', { name: /login/i })[0];
    fireEvent.click(loginButton);

    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    fireEvent.change(emailInput, { target: { value: 'test@test.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    const submitButton = screen.getAllByRole('button', { name: /login/i }).find(
      (btn) => btn.getAttribute('type') === 'submit'
    );
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('jwt.token.here');
      expect(screen.getByText('testuser')).toBeTruthy();
    });
  });

  it('handles register and stores sanitized data', async () => {
    const registerResponse = { token: 'reg.token.here', user: { ...mockUser, userName: 'newuser' } };
    apiClient.register.mockResolvedValue(registerResponse);

    render(<App />);
    const loginButton = screen.getAllByRole('button', { name: /login/i })[0];
    fireEvent.click(loginButton);
    const registerSwitch = screen.getByRole('button', { name: /register/i });
    fireEvent.click(registerSwitch);

    const usernameInput = screen.getByLabelText(/username/i);
    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    fireEvent.change(usernameInput, { target: { value: 'newuser' } });
    fireEvent.change(emailInput, { target: { value: 'new@test.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    const submitButton = screen.getByRole('button', { name: /^register$/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('reg.token.here');
      expect(screen.getByText('newuser')).toBeTruthy();
    });
  });

  it('clears storage on invalid saved user JSON', () => {
    localStorage.setItem('token', 'abc123');
    localStorage.setItem('user', 'invalid-json');

    render(<App />);

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});

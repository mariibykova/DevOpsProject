import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';

vi.mock('../api/apiClient', () => ({
  login: vi.fn(),
  register: vi.fn(),
  getProducts: vi.fn().mockResolvedValue([]),
  getReviews: vi.fn().mockResolvedValue([]),
}));

beforeEach(() => {
  localStorage.clear();
});

describe('App', () => {
  it('renders header with title', () => {
    render(<App />);
    expect(screen.getByText('Product Catalog')).toBeInTheDocument();
  });

  it('renders Products and Reviews navigation buttons', () => {
    render(<App />);
    expect(screen.getByRole('button', { name: 'Products' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Reviews' })).toBeInTheDocument();
  });

  it('shows Login button when not authenticated', () => {
    render(<App />);
    expect(screen.getByText('Login')).toBeInTheDocument();
  });

  it('switches to reviews page when Reviews button is clicked', async () => {
    render(<App />);
    fireEvent.click(screen.getByText('Reviews'));
    await waitFor(() => {
      expect(screen.getByText('Reviews', { selector: 'h2' })).toBeInTheDocument();
    });
  });

  it('shows login form when Login button is clicked', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Login'));
    expect(screen.getByText('Login', { selector: 'h2' })).toBeInTheDocument();
  });
});

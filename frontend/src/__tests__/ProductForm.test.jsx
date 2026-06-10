import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ProductForm from '../components/ProductForm';
import * as apiClient from '../api/apiClient';

vi.mock('../api/apiClient');

describe('ProductForm', () => {
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders empty form for new product', () => {
    render(<ProductForm onClose={mockOnClose} />);
    expect(screen.getByText('New Product')).toBeTruthy();
  });

  it('renders form with product data for editing', () => {
    const product = { id: 1, name: 'Test Product', description: 'Test description', price: 10.0 };
    render(<ProductForm product={product} onClose={mockOnClose} />);
    expect(screen.getByText('Edit Product')).toBeTruthy();
    expect(screen.getByDisplayValue('Test Product')).toBeTruthy();
  });

  it('calls onClose when cancel is clicked', () => {
    render(<ProductForm onClose={mockOnClose} />);
    const cancelButton = screen.getByRole('button', { name: /cancel/i });
    fireEvent.click(cancelButton);
    expect(mockOnClose).toHaveBeenCalled();
  });
});

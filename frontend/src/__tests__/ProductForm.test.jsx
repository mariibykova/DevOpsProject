import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
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

  it('calls createProduct and onClose on submit when creating', async () => {
    apiClient.createProduct.mockResolvedValue({ id: 1 });
    const onClose = vi.fn();
    render(<ProductForm onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'New Laptop' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'A brand new laptop with great specs and features' } });
    fireEvent.change(screen.getByLabelText('Picture URL'), { target: { value: 'laptop.jpg' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: '999.99' } });
    fireEvent.submit(screen.getByText('Create'));

    await waitFor(() => {
      expect(apiClient.createProduct).toHaveBeenCalledWith({
        name: 'New Laptop',
        description: 'A brand new laptop with great specs and features',
        picture: 'laptop.jpg',
        price: 999.99,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('calls updateProduct and onClose on submit when editing', async () => {
    apiClient.updateProduct.mockResolvedValue({ id: 1 });
    const onClose = vi.fn();
    const product = { id: 1, name: 'Old Name', description: 'Old description for the product item', picture: 'old.jpg', price: 50.0 };
    render(<ProductForm product={product} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Updated Name' } });
    fireEvent.submit(screen.getByText('Update'));

    await waitFor(() => {
      expect(apiClient.updateProduct).toHaveBeenCalledWith(1, {
        name: 'Updated Name',
        description: 'Old description for the product item',
        picture: 'old.jpg',
        price: 50.0,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('displays error message on submit failure', async () => {
    apiClient.createProduct.mockRejectedValue(new Error('Validation failed'));
    render(<ProductForm onClose={mockOnClose} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'Test description for the product' } });
    fireEvent.change(screen.getByLabelText('Picture URL'), { target: { value: 'pic.jpg' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: '10' } });
    fireEvent.submit(screen.getByText('Create'));

    const errorMessage = await screen.findByText('Validation failed');
    expect(errorMessage).toBeInTheDocument();
  });
});

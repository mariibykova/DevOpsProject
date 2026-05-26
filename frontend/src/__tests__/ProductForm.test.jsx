import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ProductForm from '../components/ProductForm';
import * as api from '../api/apiClient';

vi.mock('../api/apiClient');

beforeEach(() => {
  vi.clearAllMocks();
});

describe('ProductForm', () => {
  it('renders New Product heading when creating', () => {
    render(<ProductForm product={null} onClose={vi.fn()} />);

    expect(screen.getByText('New Product')).toBeInTheDocument();
  });

  it('renders Edit Product heading when editing', () => {
    const existingProduct = { id: 1, name: 'Laptop', description: 'A powerful laptop for development', picture: 'laptop.jpg', price: 999.99 };
    render(<ProductForm product={existingProduct} onClose={vi.fn()} />);

    expect(screen.getByText('Edit Product')).toBeInTheDocument();
  });

  it('renders form fields: name, description, picture URL, price', () => {
    render(<ProductForm product={null} onClose={vi.fn()} />);

    expect(screen.getByLabelText('Name')).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).toBeInTheDocument();
    expect(screen.getByLabelText('Picture URL')).toBeInTheDocument();
    expect(screen.getByLabelText('Price')).toBeInTheDocument();
  });

  it('pre-fills form fields when editing', () => {
    const existingProduct = { id: 1, name: 'Laptop', description: 'A powerful laptop for development', picture: 'laptop.jpg', price: 999.99 };
    render(<ProductForm product={existingProduct} onClose={vi.fn()} />);

    expect(screen.getByLabelText('Name')).toHaveValue('Laptop');
    expect(screen.getByLabelText('Description')).toHaveValue('A powerful laptop for development');
    expect(screen.getByLabelText('Picture URL')).toHaveValue('laptop.jpg');
    expect(screen.getByLabelText('Price')).toHaveValue(999.99);
  });

  it('calls createProduct and onClose on submit when creating', async () => {
    api.createProduct.mockResolvedValue({ id: 3 });
    const onClose = vi.fn();
    render(<ProductForm product={null} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Keyboard' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'Mechanical keyboard with RGB backlighting and cherry switches' } });
    fireEvent.change(screen.getByLabelText('Picture URL'), { target: { value: 'keyboard.jpg' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: '149.99' } });
    fireEvent.submit(screen.getByText('Create'));

    await waitFor(() => {
      expect(api.createProduct).toHaveBeenCalledWith({
        name: 'Keyboard',
        description: 'Mechanical keyboard with RGB backlighting and cherry switches',
        picture: 'keyboard.jpg',
        price: 149.99,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('calls updateProduct and onClose on submit when editing', async () => {
    api.updateProduct.mockResolvedValue({ id: 1 });
    const onClose = vi.fn();
    const existingProduct = { id: 1, name: 'Laptop', description: 'A powerful laptop for development', picture: 'laptop.jpg', price: 999.99 };
    render(<ProductForm product={existingProduct} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Gaming Laptop' } });
    fireEvent.submit(screen.getByText('Update'));

    await waitFor(() => {
      expect(api.updateProduct).toHaveBeenCalledWith(1, {
        name: 'Gaming Laptop',
        description: 'A powerful laptop for development',
        picture: 'laptop.jpg',
        price: 999.99,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('displays error message on submit failure', async () => {
    api.createProduct.mockRejectedValue(new Error('Validation failed'));
    render(<ProductForm product={null} onClose={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Name'), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'Test description that is long enough for validation purposes' } });
    fireEvent.change(screen.getByLabelText('Picture URL'), { target: { value: 'test.jpg' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: '10.00' } });
    fireEvent.submit(screen.getByText('Create'));

    const errorMessage = await screen.findByText('Validation failed');
    expect(errorMessage).toBeInTheDocument();
  });

  it('calls onClose when Cancel button is clicked', () => {
    const onClose = vi.fn();
    render(<ProductForm product={null} onClose={onClose} />);

    fireEvent.click(screen.getByText('Cancel'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('shows Create button when creating and Update button when editing', () => {
    const { unmount } = render(<ProductForm product={null} onClose={vi.fn()} />);
    expect(screen.getByText('Create')).toBeInTheDocument();
    unmount();

    const existingProduct = { id: 1, name: 'Laptop', description: 'A powerful laptop', picture: 'laptop.jpg', price: 999.99 };
    render(<ProductForm product={existingProduct} onClose={vi.fn()} />);
    expect(screen.getByText('Update')).toBeInTheDocument();
  });
});

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import ProductList from '../components/ProductList';
import * as api from '../api/apiClient';

vi.mock('../api/apiClient');

const mockProducts = [
  { id: 1, name: 'Laptop', description: 'A powerful laptop for development', picture: 'laptop.jpg', price: 999.99 },
  { id: 2, name: 'Mouse', description: 'Ergonomic wireless mouse for comfort', picture: 'mouse.jpg', price: 49.99 },
];

beforeEach(() => {
  vi.clearAllMocks();
});

describe('ProductList', () => {
  it('renders products after loading', async () => {
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ProductList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Laptop')).toBeInTheDocument();
      expect(screen.getByText('Mouse')).toBeInTheDocument();
    });
  });

  it('shows empty state when no products', async () => {
    api.getProducts.mockResolvedValue([]);

    render(<ProductList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('No products found')).toBeInTheDocument();
    });
  });

  it('shows Add Product button for admin users', async () => {
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ProductList user={{ id: 1, userName: 'admin', roles: ['ROLE_ADMIN'] }} />);

    await waitFor(() => {
      expect(screen.getByText('Add Product')).toBeInTheDocument();
    });
  });

  it('does not show Add Product button for non-admin users', async () => {
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ProductList user={{ id: 2, userName: 'user', roles: ['ROLE_USER'] }} />);

    await waitFor(() => {
      expect(screen.queryByText('Add Product')).not.toBeInTheDocument();
    });
  });

  it('shows Edit and Delete buttons for admin users', async () => {
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ProductList user={{ id: 1, userName: 'admin', roles: ['ROLE_ADMIN'] }} />);

    await waitFor(() => {
      const editButtons = screen.getAllByText('Edit');
      const deleteButtons = screen.getAllByText('Delete');
      expect(editButtons).toHaveLength(2);
      expect(deleteButtons).toHaveLength(2);
    });
  });

  it('displays error message on fetch failure', async () => {
    api.getProducts.mockRejectedValue(new Error('Network error'));

    render(<ProductList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Network error')).toBeInTheDocument();
    });
  });

  it('displays product prices', async () => {
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ProductList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('$999.99')).toBeInTheDocument();
      expect(screen.getByText('$49.99')).toBeInTheDocument();
    });
  });
});

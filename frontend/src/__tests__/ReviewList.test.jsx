import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import ReviewList from '../components/ReviewList';
import * as api from '../api/apiClient';

vi.mock('../api/apiClient');

const mockProducts = [
  { id: 1, name: 'Laptop', description: 'A powerful laptop', picture: 'laptop.jpg', price: 999.99 },
  { id: 2, name: 'Mouse', description: 'Ergonomic mouse', picture: 'mouse.jpg', price: 49.99 },
];

const mockReviews = [
  { id: 1, title: 'Great product', body: 'Loved it so much', rating: 5, productId: 1, userId: 1, userName: 'alice' },
  { id: 2, title: 'Decent', body: 'Could be better', rating: 3, productId: 2, userId: 2, userName: 'bob' },
];

beforeEach(() => {
  vi.clearAllMocks();
});

describe('ReviewList', () => {
  it('renders reviews after loading', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Great product')).toBeInTheDocument();
      expect(screen.getByText('Decent')).toBeInTheDocument();
    });
  });

  it('shows empty state when no reviews', async () => {
    api.getReviews.mockResolvedValue([]);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('No reviews found')).toBeInTheDocument();
    });
  });

  it('shows Add Review button for authenticated users', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={{ id: 1, userName: 'alice', roles: ['ROLE_USER'] }} />);

    await waitFor(() => {
      expect(screen.getByText('Add Review')).toBeInTheDocument();
    });
  });

  it('does not show Add Review button for anonymous users', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Great product')).toBeInTheDocument();
    });
    expect(screen.queryByText('Add Review')).not.toBeInTheDocument();
  });

  it('displays review ratings as stars', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Rating: ★★★★★')).toBeInTheDocument();
      expect(screen.getByText('Rating: ★★★☆☆')).toBeInTheDocument();
    });
  });

  it('displays author names', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('alice')).toBeInTheDocument();
      expect(screen.getByText('bob')).toBeInTheDocument();
    });
  });

  it('displays product names for reviews', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Laptop')).toBeInTheDocument();
      expect(screen.getByText('Mouse')).toBeInTheDocument();
    });
  });

  it('shows Edit and Delete buttons for authenticated users', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={{ id: 1, userName: 'alice', roles: ['ROLE_USER'] }} />);

    await waitFor(() => {
      const editButtons = screen.getAllByText('Edit');
      const deleteButtons = screen.getAllByText('Delete');
      expect(editButtons).toHaveLength(2);
      expect(deleteButtons).toHaveLength(2);
    });
  });

  it('does not show Edit and Delete buttons for anonymous users', async () => {
    api.getReviews.mockResolvedValue(mockReviews);
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Great product')).toBeInTheDocument();
    });
    expect(screen.queryByText('Edit')).not.toBeInTheDocument();
    expect(screen.queryByText('Delete')).not.toBeInTheDocument();
  });

  it('displays error message on fetch failure', async () => {
    api.getReviews.mockRejectedValue(new Error('Network error'));
    api.getProducts.mockResolvedValue(mockProducts);

    render(<ReviewList user={null} />);

    await waitFor(() => {
      expect(screen.getByText('Network error')).toBeInTheDocument();
    });
  });
});

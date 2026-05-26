import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReviewForm from '../components/ReviewForm';
import * as api from '../api/apiClient';

vi.mock('../api/apiClient');

const mockProducts = [
  { id: 1, name: 'Laptop', description: 'A powerful laptop', picture: 'laptop.jpg', price: 999.99 },
  { id: 2, name: 'Mouse', description: 'Ergonomic mouse', picture: 'mouse.jpg', price: 49.99 },
];

beforeEach(() => {
  vi.clearAllMocks();
});

describe('ReviewForm', () => {
  it('renders New Review heading when creating', () => {
    render(<ReviewForm review={null} products={mockProducts} onClose={vi.fn()} />);

    expect(screen.getByText('New Review')).toBeInTheDocument();
  });

  it('renders Edit Review heading when editing', () => {
    const existingReview = { id: 1, title: 'Great', body: 'Loved it', rating: 5, productId: 1 };
    render(<ReviewForm review={existingReview} products={mockProducts} onClose={vi.fn()} />);

    expect(screen.getByText('Edit Review')).toBeInTheDocument();
  });

  it('renders form fields: title, body, rating, product', () => {
    render(<ReviewForm review={null} products={mockProducts} onClose={vi.fn()} />);

    expect(screen.getByLabelText('Title')).toBeInTheDocument();
    expect(screen.getByLabelText('Body')).toBeInTheDocument();
    expect(screen.getByLabelText('Rating (1-5)')).toBeInTheDocument();
    expect(screen.getByLabelText('Product')).toBeInTheDocument();
  });

  it('pre-fills form fields when editing', () => {
    const existingReview = { id: 1, title: 'Great', body: 'Loved it', rating: 4, productId: 2 };
    render(<ReviewForm review={existingReview} products={mockProducts} onClose={vi.fn()} />);

    expect(screen.getByLabelText('Title')).toHaveValue('Great');
    expect(screen.getByLabelText('Body')).toHaveValue('Loved it');
    expect(screen.getByLabelText('Rating (1-5)')).toHaveValue('4');
    expect(screen.getByLabelText('Product')).toHaveValue('2');
  });

  it('renders product options in the select dropdown', () => {
    render(<ReviewForm review={null} products={mockProducts} onClose={vi.fn()} />);

    expect(screen.getByText('Laptop')).toBeInTheDocument();
    expect(screen.getByText('Mouse')).toBeInTheDocument();
  });

  it('calls createReview and onClose on submit when creating', async () => {
    api.createReview.mockResolvedValue({ id: 3 });
    const onClose = vi.fn();
    render(<ReviewForm review={null} products={mockProducts} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Title'), { target: { value: 'Nice product' } });
    fireEvent.change(screen.getByLabelText('Body'), { target: { value: 'Really enjoyed using it' } });
    fireEvent.change(screen.getByLabelText('Rating (1-5)'), { target: { value: '4' } });
    fireEvent.change(screen.getByLabelText('Product'), { target: { value: '2' } });
    fireEvent.submit(screen.getByText('Create'));

    await waitFor(() => {
      expect(api.createReview).toHaveBeenCalledWith({
        title: 'Nice product',
        body: 'Really enjoyed using it',
        rating: 4,
        productId: 2,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('calls updateReview and onClose on submit when editing', async () => {
    api.updateReview.mockResolvedValue({ id: 1 });
    const onClose = vi.fn();
    const existingReview = { id: 1, title: 'Old title', body: 'Old body', rating: 3, productId: 1 };
    render(<ReviewForm review={existingReview} products={mockProducts} onClose={onClose} />);

    fireEvent.change(screen.getByLabelText('Title'), { target: { value: 'Updated title' } });
    fireEvent.submit(screen.getByText('Update'));

    await waitFor(() => {
      expect(api.updateReview).toHaveBeenCalledWith(1, {
        title: 'Updated title',
        body: 'Old body',
        rating: 3,
        productId: 1,
      });
      expect(onClose).toHaveBeenCalled();
    });
  });

  it('displays error message on submit failure', async () => {
    api.createReview.mockRejectedValue(new Error('Validation failed'));
    render(<ReviewForm review={null} products={mockProducts} onClose={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Title'), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText('Body'), { target: { value: 'Test body' } });
    fireEvent.submit(screen.getByText('Create'));

    const errorMessage = await screen.findByText('Validation failed');
    expect(errorMessage).toBeInTheDocument();
  });

  it('calls onClose when Cancel button is clicked', () => {
    const onClose = vi.fn();
    render(<ReviewForm review={null} products={mockProducts} onClose={onClose} />);

    fireEvent.click(screen.getByText('Cancel'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });
});

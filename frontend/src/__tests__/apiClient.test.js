import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  login,
  register,
  getProducts,
  getProduct,
  createProduct,
  updateProduct,
  deleteProduct,
  getReviews,
  createReview,
  updateReview,
  deleteReview,
} from '../api/apiClient';

const mockFetch = vi.fn();
global.fetch = mockFetch;

beforeEach(() => {
  mockFetch.mockReset();
  localStorage.clear();
});

function jsonResponse(data, status = 200) {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: () => Promise.resolve(data),
  });
}

function noContentResponse() {
  return Promise.resolve({
    ok: true,
    status: 204,
    json: () => Promise.reject(new Error('No content')),
  });
}

describe('Auth API', () => {
  it('login sends correct request and returns data', async () => {
    const mockData = { token: 'abc123', user: { id: 1, userName: 'test', email: 'test@test.com' } };
    mockFetch.mockReturnValueOnce(jsonResponse(mockData));

    const result = await login('test@test.com', 'password123');

    expect(mockFetch).toHaveBeenCalledWith('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'test@test.com', password: 'password123' }),
    });
    expect(result).toEqual(mockData);
  });

  it('register sends correct request and returns data', async () => {
    const mockData = { token: 'abc123', user: { id: 1, userName: 'newuser', email: 'new@test.com' } };
    mockFetch.mockReturnValueOnce(jsonResponse(mockData));

    const result = await register('newuser', 'new@test.com', 'password123');

    expect(mockFetch).toHaveBeenCalledWith('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userName: 'newuser', email: 'new@test.com', password: 'password123' }),
    });
    expect(result).toEqual(mockData);
  });

  it('login throws error on failure', async () => {
    mockFetch.mockReturnValueOnce(
      Promise.resolve({
        ok: false,
        status: 401,
        json: () => Promise.resolve({ message: 'Invalid email or password' }),
      })
    );

    await expect(login('bad@test.com', 'wrong')).rejects.toThrow('Invalid email or password');
  });
});

describe('Products API', () => {
  it('getProducts fetches all products', async () => {
    const mockProducts = [
      { id: 1, name: 'Product 1', description: 'Desc 1', picture: 'pic1.jpg', price: 10.0 },
      { id: 2, name: 'Product 2', description: 'Desc 2', picture: 'pic2.jpg', price: 20.0 },
    ];
    mockFetch.mockReturnValueOnce(jsonResponse(mockProducts));

    const result = await getProducts();

    expect(mockFetch).toHaveBeenCalledWith('/api/products', {
      headers: { 'Content-Type': 'application/json' },
    });
    expect(result).toEqual(mockProducts);
  });

  it('getProduct fetches a single product', async () => {
    const mockProduct = { id: 1, name: 'Product 1', description: 'Desc 1', picture: 'pic1.jpg', price: 10.0 };
    mockFetch.mockReturnValueOnce(jsonResponse(mockProduct));

    const result = await getProduct(1);

    expect(mockFetch).toHaveBeenCalledWith('/api/products/1', {
      headers: { 'Content-Type': 'application/json' },
    });
    expect(result).toEqual(mockProduct);
  });

  it('createProduct sends POST with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    const newProduct = { name: 'New', description: 'A new product description that is long enough', picture: 'pic.jpg', price: 15.0 };
    const mockResponse = { id: 3, ...newProduct };
    mockFetch.mockReturnValueOnce(jsonResponse(mockResponse, 201));

    const result = await createProduct(newProduct);

    expect(mockFetch).toHaveBeenCalledWith('/api/products', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
      body: JSON.stringify(newProduct),
    });
    expect(result).toEqual(mockResponse);
  });

  it('updateProduct sends PUT with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    const updatedProduct = { name: 'Updated', description: 'Updated description that is long enough for validation', picture: 'pic.jpg', price: 25.0 };
    const mockResponse = { id: 1, ...updatedProduct };
    mockFetch.mockReturnValueOnce(jsonResponse(mockResponse));

    const result = await updateProduct(1, updatedProduct);

    expect(mockFetch).toHaveBeenCalledWith('/api/products/1', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
      body: JSON.stringify(updatedProduct),
    });
    expect(result).toEqual(mockResponse);
  });

  it('deleteProduct sends DELETE with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    mockFetch.mockReturnValueOnce(noContentResponse());

    const result = await deleteProduct(1);

    expect(mockFetch).toHaveBeenCalledWith('/api/products/1', {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
    });
    expect(result).toBeNull();
  });
});

describe('Reviews API', () => {
  it('getReviews fetches all reviews', async () => {
    const mockReviews = [
      { id: 1, title: 'Great', body: 'Loved it', rating: 5, productId: 1, userId: 1 },
    ];
    mockFetch.mockReturnValueOnce(jsonResponse(mockReviews));

    const result = await getReviews();

    expect(mockFetch).toHaveBeenCalledWith('/api/reviews', {
      headers: { 'Content-Type': 'application/json' },
    });
    expect(result).toEqual(mockReviews);
  });

  it('createReview sends POST with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    const newReview = { title: 'Nice', body: 'Good product', rating: 4, productId: 1 };
    const mockResponse = { id: 2, ...newReview, userId: 1 };
    mockFetch.mockReturnValueOnce(jsonResponse(mockResponse, 201));

    const result = await createReview(newReview);

    expect(mockFetch).toHaveBeenCalledWith('/api/reviews', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
      body: JSON.stringify(newReview),
    });
    expect(result).toEqual(mockResponse);
  });

  it('updateReview sends PUT with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    const updatedReview = { title: 'Updated', body: 'Updated body', rating: 3, productId: 1 };
    const mockResponse = { id: 1, ...updatedReview, userId: 1 };
    mockFetch.mockReturnValueOnce(jsonResponse(mockResponse));

    const result = await updateReview(1, updatedReview);

    expect(mockFetch).toHaveBeenCalledWith('/api/reviews/1', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
      body: JSON.stringify(updatedReview),
    });
    expect(result).toEqual(mockResponse);
  });

  it('deleteReview sends DELETE with auth header', async () => {
    localStorage.setItem('token', 'mytoken');
    mockFetch.mockReturnValueOnce(noContentResponse());

    const result = await deleteReview(1);

    expect(mockFetch).toHaveBeenCalledWith('/api/reviews/1', {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json', Authorization: 'Bearer mytoken' },
    });
    expect(result).toBeNull();
  });
});

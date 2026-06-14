const API_BASE = '/api';

function getAuthHeaders() {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function handleResponse(response) {
  if (!response.ok) {
    const body = await response.json().catch(() => null);
    const message = body?.message || `HTTP error ${response.status}`;
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}

function sanitizeId(id) {
  const numId = Number(id);
  if (!Number.isFinite(numId) || numId <= 0 || Math.floor(numId) !== numId) {
    throw new Error(`Invalid id: ${id}`);
  }
  return numId;
}

// Auth
export async function login(email, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  return handleResponse(res);
}

export async function register(userName, email, password) {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userName, email, password }),
  });
  return handleResponse(res);
}

// Products
export async function getProducts() {
  const res = await fetch(`${API_BASE}/products`, {
    headers: getAuthHeaders(),
  });
  return handleResponse(res);
}

export async function getProduct(id) {
  const safeId = sanitizeId(id);
  const res = await fetch(`${API_BASE}/products/${safeId}`, {
    headers: getAuthHeaders(),
  });
  return handleResponse(res);
}

export async function createProduct(product) {
  const res = await fetch(`${API_BASE}/products`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(product),
  });
  return handleResponse(res);
}

export async function updateProduct(id, product) {
  const safeId = sanitizeId(id);
  const res = await fetch(`${API_BASE}/products/${safeId}`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify(product),
  });
  return handleResponse(res);
}

export async function deleteProduct(id) {
  const safeId = sanitizeId(id);
  const res = await fetch(`${API_BASE}/products/${safeId}`, {
    method: 'DELETE',
    headers: getAuthHeaders(),
  });
  return handleResponse(res);
}

// Reviews
export async function getReviews() {
  const res = await fetch(`${API_BASE}/reviews`, {
    headers: getAuthHeaders(),
  });
  return handleResponse(res);
}

export async function createReview(review) {
  const res = await fetch(`${API_BASE}/reviews`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(review),
  });
  return handleResponse(res);
}

export async function updateReview(id, review) {
  const safeId = sanitizeId(id);
  const res = await fetch(`${API_BASE}/reviews/${safeId}`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify(review),
  });
  return handleResponse(res);
}

export async function deleteReview(id) {
  const safeId = sanitizeId(id);
  const res = await fetch(`${API_BASE}/reviews/${safeId}`, {
    method: 'DELETE',
    headers: getAuthHeaders(),
  });
  return handleResponse(res);
}

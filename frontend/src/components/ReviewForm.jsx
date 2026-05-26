import React, { useState } from 'react';
import { createReview, updateReview } from '../api/apiClient';

export default function ReviewForm({ review, products, onClose }) {
  const isEdit = !!review;
  const [title, setTitle] = useState(review?.title || '');
  const [body, setBody] = useState(review?.body || '');
  const [rating, setRating] = useState(review?.rating || 5);
  const [productId, setProductId] = useState(review?.productId || (products[0]?.id || ''));
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const payload = { title, body, rating: parseInt(rating, 10), productId: parseInt(productId, 10) };
    try {
      if (isEdit) {
        await updateReview(review.id, payload);
      } else {
        await createReview(payload);
      }
      onClose();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="card">
      <h2>{isEdit ? 'Edit Review' : 'New Review'}</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="review-title">Title</label>
          <input
            id="review-title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="review-body">Body</label>
          <textarea
            id="review-body"
            value={body}
            onChange={(e) => setBody(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="review-rating">Rating (1-5)</label>
          <select
            id="review-rating"
            value={rating}
            onChange={(e) => setRating(e.target.value)}
          >
            {[1, 2, 3, 4, 5].map((v) => (
              <option key={v} value={v}>{v} {'★'.repeat(v)}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="review-product">Product</label>
          <select
            id="review-product"
            value={productId}
            onChange={(e) => setProductId(e.target.value)}
            required
          >
            {products.map((p) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn btn-primary">
          {isEdit ? 'Update' : 'Create'}
        </button>
        <button type="button" className="btn btn-secondary" onClick={onClose} style={{ marginLeft: 8 }}>
          Cancel
        </button>
      </form>
    </div>
  );
}

import React, { useState, useEffect } from 'react';
import { getReviews, deleteReview, getProducts } from '../api/apiClient';
import ReviewForm from './ReviewForm';

export default function ReviewList({ user }) {
  const [reviews, setReviews] = useState([]);
  const [products, setProducts] = useState([]);
  const [error, setError] = useState('');
  const [editingReview, setEditingReview] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const loadData = async () => {
    try {
      const [reviewsData, productsData] = await Promise.all([getReviews(), getProducts()]);
      setReviews(reviewsData);
      setProducts(productsData);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this review?')) return;
    try {
      await deleteReview(id);
      await loadData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (review) => {
    setEditingReview(review);
    setShowForm(true);
  };

  const handleFormClose = () => {
    setEditingReview(null);
    setShowForm(false);
    loadData();
  };

  const getProductName = (productId) => {
    const product = products.find((p) => p.id === productId);
    return product ? product.name : `Product #${productId}`;
  };

  if (showForm) {
    return (
      <ReviewForm
        review={editingReview}
        products={products}
        onClose={handleFormClose}
      />
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2>Reviews</h2>
        {user && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            Add Review
          </button>
        )}
      </div>
      {error && <p className="error">{error}</p>}
      {reviews.length === 0 ? (
        <div className="empty-state">No reviews found</div>
      ) : (
        reviews.map((review) => (
          <div key={review.id} className="card">
            <h3>{review.title}</h3>
            <p>{review.body}</p>
            <p className="rating">
              Rating: {'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}
            </p>
            <p><strong>Author:</strong> {review.userName || `User #${review.userId}`}</p>
            <p><strong>Product:</strong> {getProductName(review.productId)}</p>
            {user && (
              <div className="actions">
                <button className="btn btn-primary" onClick={() => handleEdit(review)}>
                  Edit
                </button>
                <button className="btn btn-danger" onClick={() => handleDelete(review.id)}>
                  Delete
                </button>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}

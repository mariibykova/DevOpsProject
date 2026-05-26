import React, { useState } from 'react';
import { createProduct, updateProduct } from '../api/apiClient';

export default function ProductForm({ product, onClose }) {
  const isEdit = !!product;
  const [name, setName] = useState(product?.name || '');
  const [description, setDescription] = useState(product?.description || '');
  const [picture, setPicture] = useState(product?.picture || '');
  const [price, setPrice] = useState(product?.price || '');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const payload = { name, description, picture, price: parseFloat(price) };
    try {
      if (isEdit) {
        await updateProduct(product.id, payload);
      } else {
        await createProduct(payload);
      }
      onClose();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="card">
      <h2>{isEdit ? 'Edit Product' : 'New Product'}</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="product-name">Name</label>
          <input
            id="product-name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            minLength={3}
            maxLength={100}
          />
        </div>
        <div className="form-group">
          <label htmlFor="product-description">Description</label>
          <textarea
            id="product-description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            minLength={32}
            maxLength={500}
          />
        </div>
        <div className="form-group">
          <label htmlFor="product-picture">Picture URL</label>
          <input
            id="product-picture"
            type="text"
            value={picture}
            onChange={(e) => setPicture(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="product-price">Price</label>
          <input
            id="product-price"
            type="number"
            step="0.01"
            min="0.01"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            required
          />
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

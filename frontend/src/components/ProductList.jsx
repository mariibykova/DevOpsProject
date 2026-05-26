import React, { useState, useEffect } from 'react';
import { getProducts, deleteProduct } from '../api/apiClient';
import ProductForm from './ProductForm';

export default function ProductList({ user }) {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState('');
  const [editingProduct, setEditingProduct] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  const loadProducts = async () => {
    try {
      const data = await getProducts();
      setProducts(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this product?')) return;
    try {
      await deleteProduct(id);
      await loadProducts();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setShowForm(true);
  };

  const handleFormClose = () => {
    setEditingProduct(null);
    setShowForm(false);
    loadProducts();
  };

  if (showForm) {
    return (
      <ProductForm
        product={editingProduct}
        onClose={handleFormClose}
      />
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2>Products</h2>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            Add Product
          </button>
        )}
      </div>
      {error && <p className="error">{error}</p>}
      {products.length === 0 ? (
        <div className="empty-state">No products found</div>
      ) : (
        products.map((product) => (
          <div key={product.id} className="card">
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p><strong>Price:</strong> ${product.price}</p>
            {product.picture && (
              <p><strong>Image:</strong> {product.picture}</p>
            )}
            {isAdmin && (
              <div className="actions">
                <button className="btn btn-primary" onClick={() => handleEdit(product)}>Edit</button>
                <button className="btn btn-danger" onClick={() => handleDelete(product.id)}>Delete</button>
              </div>
            )}
          </div>
        ))
      )}
    </div>
  );
}

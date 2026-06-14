import React, { useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from './api/apiClient';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import ProductList from './components/ProductList';
import ReviewList from './components/ReviewList';

export default function App() {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [page, setPage] = useState('products');
  const [authPage, setAuthPage] = useState('login');

  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser && token) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse saved user, logging out', e);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
      }
    }
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleLogin = async (email, password) => {
    const data = await apiLogin(email, password);
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data.user));
    setToken(data.token);
    setUser(data.user);
  };

  const handleRegister = async (userName, email, password) => {
    const data = await apiRegister(userName, email, password);
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data.user));
    setToken(data.token);
    setUser(data.user);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  return (
    <div className="app">
      <header>
        <h1>Product Catalog</h1>
        <nav>
          <button
            className={page === 'products' ? 'active' : ''}
            onClick={() => setPage('products')}
          >
            Products
          </button>
          <button
            className={page === 'reviews' ? 'active' : ''}
            onClick={() => setPage('reviews')}
          >
            Reviews
          </button>
        </nav>
        <div className="auth-section">
          {user ? (
            <>
              <span>{user.userName}</span>
              <button onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <button onClick={() => setPage('auth')}>Login</button>
          )}
        </div>
      </header>

      {page === 'auth' && !user && (
        authPage === 'login' ? (
          <LoginForm onLogin={handleLogin} onSwitchToRegister={() => setAuthPage('register')} />
        ) : (
          <RegisterForm onRegister={handleRegister} onSwitchToLogin={() => setAuthPage('login')} />
        )
      )}

      {page === 'products' && <ProductList user={user} />}
      {page === 'reviews' && <ReviewList user={user} />}
    </div>
  );
}

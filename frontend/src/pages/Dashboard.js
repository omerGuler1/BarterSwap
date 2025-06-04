import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';

const categories = [
  '', 'BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS'
];

function Dashboard() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [keyword, setKeyword] = useState('');
  const [category, setCategory] = useState('');
  const [searching, setSearching] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      setLoading(true);
      setSearching(false);
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }
      const response = await api.get('/items/active');
      setItems(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch items.');
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    setSearching(true);
    try {
      const params = {};
      if (keyword) params.keyword = keyword;
      if (category) params.category = category;
      const response = await api.get('/items/search', { params });
      setItems(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to search items.');
      setLoading(false);
    }
  };

  const handleClearSearch = () => {
    setKeyword('');
    setCategory('');
    fetchItems();
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) return (
    <div className="page-container text-center">
      <p>Loading...</p>
    </div>
  );

  if (error) return (
    <div className="page-container">
      <p className="text-error">{error}</p>
    </div>
  );

  return (
    <div className="app-container">
      <nav>
        <div className="nav-container">
          <h2>BarterSwap</h2>
          <div className="nav-links">
            <button className="btn btn-secondary" onClick={() => navigate('/items/new')}>+ New Item</button>
            <button className="btn btn-secondary" onClick={() => navigate('/my-items')}>My Items</button>
            <button className="btn btn-secondary" onClick={() => navigate('/my-purchases')}>My Purchases</button>
            <button className="btn btn-secondary" onClick={() => navigate('/items/status')}>Items by Status</button>
            <button className="btn btn-secondary" onClick={() => navigate('/virtual-currency')}>Virtual Currency</button>
            <button className="btn btn-secondary" onClick={() => navigate('/analytics')}>Analytics</button>
            <button className="btn btn-secondary" onClick={() => navigate('/profile')}>Profile</button>
            <button className="btn btn-secondary" onClick={handleLogout}>Logout</button>
          </div>
        </div>
      </nav>

      <div className="card mb-4">
        <h1 className="text-center mb-4">Marketplace</h1>
        
        <form onSubmit={handleSearch} className="grid grid-2 gap-4 mb-4">
          <div className="form-group">
            <input
              type="text"
              placeholder="Search by keyword..."
              value={keyword}
              onChange={e => setKeyword(e.target.value)}
            />
          </div>
          <div className="form-group">
            <select value={category} onChange={e => setCategory(e.target.value)}>
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat ? cat : 'All Categories'}</option>
              ))}
            </select>
          </div>
          <div className="grid grid-2 gap-4">
            <button type="submit" className="btn btn-primary">Search</button>
            {searching && (
              <button type="button" className="btn btn-secondary" onClick={handleClearSearch}>
                Clear
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="grid grid-3 gap-4">
        {items.length === 0 ? (
          <div className="card text-center">
            <p>No items available at the moment.</p>
          </div>
        ) : (
          items.map(item => (
            <div key={item.itemId} className="card">
              {item.primaryImageUrl && (
                <img 
                  src={getImageUrl(item.primaryImageUrl)} 
                  alt={item.title}
                  className="mb-3"
                  style={{ width: '100%', height: '200px', objectFit: 'cover', borderRadius: 'var(--border-radius)' }}
                />
              )}
              <h3>{item.title}</h3>
              <p className="text-light mb-2">{item.description}</p>
              <div className="grid grid-2 gap-2 mb-3">
                <div>
                  <strong>Price:</strong>
                  <p>${item.currentPrice}</p>
                </div>
                <div>
                  <strong>Category:</strong>
                  <p>{item.category}</p>
                </div>
              </div>
              {item.auctionEndTime && (
                <div className="mb-3">
                  <strong>Auction Ends:</strong>
                  <p>{new Date(item.auctionEndTime).toLocaleString()}</p>
                </div>
              )}
              <button 
                className="btn btn-primary"
                onClick={() => navigate(`/items/view/${item.itemId}`)}
              >
                View Details
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default Dashboard; 
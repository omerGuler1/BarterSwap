import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getActiveItems, searchItems } from '../api';

const categories = [
  '', 'BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS'
];

export default function Marketplace() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [keyword, setKeyword] = useState('');
  const [category, setCategory] = useState('');

  const fetchItems = async (params = {}) => {
    setLoading(true);
    setError('');
    try {
      let res;
      if (params.keyword || params.category) {
        res = await searchItems(params);
      } else {
        res = await getActiveItems();
      }
      setItems(res.data);
    } catch (err) {
      setError('Failed to load items');
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchItems();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchItems({ keyword, category: category || undefined });
  };

  return (
    <div style={{ maxWidth: 800, margin: '32px auto' }}>
      <h2>Marketplace</h2>
      <form onSubmit={handleSearch} style={{ marginBottom: 24, display: 'flex', gap: 8 }}>
        <input
          placeholder="Search keyword..."
          value={keyword}
          onChange={e => setKeyword(e.target.value)}
          style={{ flex: 1 }}
        />
        <select value={category} onChange={e => setCategory(e.target.value)}>
          {categories.map(cat => (
            <option key={cat} value={cat}>{cat || 'All Categories'}</option>
          ))}
        </select>
        <button type="submit">Search</button>
      </form>
      {loading ? (
        <div>Loading...</div>
      ) : error ? (
        <div style={{ color: 'red' }}>{error}</div>
      ) : items.length === 0 ? (
        <div>No items found.</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 16 }}>
          {items.map(item => (
            <Link
              to={`/items/${item.itemId}`}
              key={item.itemId}
              style={{
                display: 'block',
                border: '1px solid #ddd',
                borderRadius: 8,
                padding: 16,
                background: '#fff',
                textDecoration: 'none',
                color: '#222',
                boxShadow: '0 2px 8px #0001',
              }}
            >
              <div style={{ fontWeight: 'bold', fontSize: 18 }}>{item.title}</div>
              <div style={{ color: '#666', fontSize: 14 }}>{item.category}</div>
              <div style={{ margin: '8px 0' }}>Price: <b>{item.currentPrice} VC</b></div>
              <div style={{ fontSize: 13, color: '#888' }}>Seller: {item.sellerUsername}</div>
              {item.auctionEndTime && (
                <div style={{ fontSize: 13, color: '#888', marginTop: '4px' }}>
                  Ends: {new Date(item.auctionEndTime).toLocaleString()}
                </div>
              )}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
} 
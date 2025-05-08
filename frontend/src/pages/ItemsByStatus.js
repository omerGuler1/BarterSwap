import React, { useState } from 'react';
import api from '../utils/axios';
import { useNavigate } from 'react-router-dom';

const statuses = ['ACTIVE', 'PENDING', 'SOLD', 'CANCELLED'];

function ItemsByStatus() {
  const [status, setStatus] = useState('ACTIVE');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const fetchItems = async (selectedStatus) => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/items/status/${selectedStatus}`);
      setItems(response.data);
    } catch (err) {
      setError('Failed to fetch items.');
    }
    setLoading(false);
  };

  const handleStatusChange = (e) => {
    setStatus(e.target.value);
    fetchItems(e.target.value);
  };

  React.useEffect(() => {
    fetchItems(status);
    // eslint-disable-next-line
  }, []);

  return (
    <div className="app-container">
      <nav>
        <div className="nav-container">
          <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
            ← Back to Marketplace
          </button>
        </div>
      </nav>
      <div className="page-container">
        <div className="text-center mb-4">
          <h1>Items by Status</h1>
          <p className="text-light">Filter and view items by their current status</p>
        </div>
        <div className="card mb-4" style={{ maxWidth: 500, margin: '0 auto' }}>
          <div className="form-group">
            <label htmlFor="status">Status:</label>
            <select id="status" value={status} onChange={handleStatusChange}>
              {statuses.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
        </div>
        {loading ? (
          <div className="text-center">Loading...</div>
        ) : error ? (
          <div className="text-error text-center">{error}</div>
        ) : (
          <div className="card">
            <table className="table" style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Current Price</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.itemId} style={{ borderBottom: '1px solid #eee' }}>
                    <td>{item.title}</td>
                    <td>{item.status}</td>
                    <td>${item.currentPrice}</td>
                    <td>
                      <button className="btn btn-primary" onClick={() => navigate(`/items/${item.itemId}`)}>
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {items.length === 0 && (
              <div className="text-center text-light mt-4">No items found for this status.</div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default ItemsByStatus; 
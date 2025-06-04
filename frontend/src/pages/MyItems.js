import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';

const statuses = ['ACTIVE', 'PENDING', 'SOLD', 'CANCELLED'];

const formatCurrency = (value) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value);
};

function MyItems() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusUpdating, setStatusUpdating] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchMyItems();
  }, []);

  const fetchMyItems = async () => {
    try {
      const response = await api.get('/items/my-items');
      console.log('Received items:', response.data);
      console.log('First item auction end time:', response.data[0]?.auctionEndTime);
      console.log('First item auction end time type:', typeof response.data[0]?.auctionEndTime);
      setItems(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch your items.');
      setLoading(false);
    }
  };

  const handleStatusChange = async (itemId, newStatus) => {
    setStatusUpdating(itemId);
    try {
      await api.put(`/items/${itemId}/status`, { status: newStatus });
      await fetchMyItems();
    } catch (err) {
      alert('Failed to update status.');
    }
    setStatusUpdating(null);
  };

  const handleDelete = async (itemId) => {
    if (!window.confirm('Are you sure you want to delete this item?')) return;
    setDeleting(itemId);
    try {
      await api.delete(`/items/${itemId}`);
      await fetchMyItems();
    } catch (err) {
      alert('Failed to delete item.');
    }
    setDeleting(null);
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
          <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
            ← Back to Marketplace
          </button>
        </div>
      </nav>
      <div className="page-container">
        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h1>My Items</h1>
            <button
              className="btn btn-primary"
              onClick={() => navigate('/items/new')}
            >
              + List New Item
            </button>
          </div>

          {error && <p className="text-error mb-4">{error}</p>}

          <div className="mt-4" style={{ overflowX: 'auto' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Image</th>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Current Price</th>
                  <th>Auction Ends</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.itemId}>
                    <td>
                      {item.primaryImageUrl ? (
                        <img
                          src={getImageUrl(item.primaryImageUrl)}
                          alt={item.title}
                          style={{ 
                            width: '40px', 
                            height: '40px', 
                            objectFit: 'cover', 
                            borderRadius: '4px' 
                          }}
                        />
                      ) : (
                        <span className="text-light">No image</span>
                      )}
                    </td>
                    <td>{item.title}</td>
                    <td>
                      <select
                        value={item.status}
                        onChange={e => handleStatusChange(item.itemId, e.target.value)}
                        disabled={statusUpdating === item.itemId}
                        className="form-control"
                        style={{ minWidth: '120px' }}
                      >
                        {statuses.map(status => (
                          <option key={status} value={status}>{status}</option>
                        ))}
                      </select>
                    </td>
                    <td>{formatCurrency(item.currentPrice)}</td>
                    <td>
                      {item.auctionEndTime ? (
                        new Date(item.auctionEndTime).toLocaleString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })
                      ) : (
                        <span className="text-light">No end time set</span>
                      )}
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                          className="btn btn-sm btn-secondary"
                          onClick={() => navigate(`/items/view/${item.itemId}`)}
                        >
                          View
                        </button>
                        {item.status === 'ACTIVE' && (
                          <button
                            className="btn btn-sm btn-primary"
                            onClick={() => navigate(`/items/edit/${item.itemId}`)}
                          >
                            Edit
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {items.length === 0 && (
            <div className="text-center text-light p-4">
              You haven't listed any items yet.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default MyItems; 
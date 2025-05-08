import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';

const statuses = ['ACTIVE', 'PENDING', 'SOLD', 'CANCELLED'];

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
        <div className="text-center mb-4">
          <h1>My Items</h1>
          <p className="text-light">Manage your listed items</p>
        </div>
        <div className="card">
          {items.length === 0 ? (
            <p className="text-center">You have not listed any items yet.</p>
          ) : (
            <table className="table" style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th>Image</th>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Current Price</th>
                  <th>Auction End Time</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.itemId} style={{ borderBottom: '1px solid #eee' }}>
                    <td>
                      {item.primaryImageUrl ? (
                        <img
                          src={getImageUrl(item.primaryImageUrl)}
                          alt={item.title}
                          style={{ width: 60, height: 60, objectFit: 'cover', borderRadius: 8 }}
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
                        className="form-group"
                      >
                        {statuses.map(status => (
                          <option key={status} value={status}>{status}</option>
                        ))}
                      </select>
                    </td>
                    <td>${item.currentPrice}</td>
                    <td>
                      {item.auctionEndTime ? (
                        new Date(item.auctionEndTime).toLocaleString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                          second: '2-digit'
                        })
                      ) : 'No end time set'}
                    </td>
                    <td>
                      <button className="btn btn-primary myitems-action-btn" onClick={() => navigate(`/items/${item.itemId}`)}>View</button>
                      <button className="btn btn-secondary myitems-action-btn ml-2" onClick={() => navigate(`/items/edit/${item.itemId}`)}>Edit</button>
                      <button
                        className="btn btn-secondary myitems-action-btn ml-2"
                        style={{ color: 'red' }}
                        onClick={() => handleDelete(item.itemId)}
                        disabled={deleting === item.itemId}
                      >
                        {deleting === item.itemId ? 'Deleting...' : 'Delete'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}

export default MyItems; 
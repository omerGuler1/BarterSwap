import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';
import { useAuth } from '../context/AuthContext';
import ItemChat from '../components/ItemChat';

const categories = [
  'BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS'
];
const conditions = [
  'NEW', 'LIKE_NEW', 'USED', 'VERY_USED', 'DAMAGED'
];

function ViewItem() {
  const { itemId } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    fetchItemDetails();
  }, [itemId]);

  const fetchItemDetails = async () => {
    try {
      const response = await api.get(`/items/${itemId}`);
      setItem(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch item details.');
      setLoading(false);
    }
  };

  if (loading) return (
    <div className="page-container text-center">
      <p>Loading...</p>
    </div>
  );

  if (error || !item) return (
    <div className="page-container">
      <p className="text-error">{error || 'Item not found'}</p>
    </div>
  );

  const isOwner = user && item.user && user.userId === item.user.userId;
  const backDestination = isOwner ? '/my-items' : '/dashboard';
  const backText = isOwner ? '← Back to My Items' : '← Back to Marketplace';

  return (
    <div className="app-container">
      <nav>
        <div className="nav-container">
          <button className="btn btn-secondary" onClick={() => navigate(backDestination)}>
            {backText}
          </button>
        </div>
      </nav>

      <div className="page-container">
        <div className="text-center mb-4">
          <h1>View Item</h1>
          <p className="text-light">Item Details - {item.status}</p>
        </div>

        <form>
          <div className="grid grid-2 gap-4">
            <div className="form-group">
              <label htmlFor="title">Title</label>
              <input
                id="title"
                type="text"
                value={item.title}
                readOnly
                className="readonly-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="category">Category</label>
              <input
                id="category"
                type="text"
                value={item.category}
                readOnly
                className="readonly-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="startingPrice">Starting Price ($)</label>
              <input
                id="startingPrice"
                type="text"
                value={item.startingPrice}
                readOnly
                className="readonly-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="currentPrice">Current Price ($)</label>
              <input
                id="currentPrice"
                type="text"
                value={item.currentPrice}
                readOnly
                className="readonly-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="condition">Condition</label>
              <input
                id="condition"
                type="text"
                value={item.condition}
                readOnly
                className="readonly-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="status">Status</label>
              <input
                id="status"
                type="text"
                value={item.status}
                readOnly
                className="readonly-input"
                style={{
                  color: item.status === 'SOLD' ? '#28a745' : 
                         item.status === 'ACTIVE' ? '#007bff' : 
                         item.status === 'CANCELLED' ? '#dc3545' : '#6c757d'
                }}
              />
            </div>

            {item.auctionEndTime && (
              <div className="form-group">
                <label htmlFor="auctionEndTime">Auction End Time</label>
                <input
                  id="auctionEndTime"
                  type="text"
                  value={new Date(item.auctionEndTime).toLocaleString()}
                  readOnly
                  className="readonly-input"
                />
              </div>
            )}

            {item.buyoutPrice && (
              <div className="form-group">
                <label htmlFor="buyoutPrice">Buyout Price ($)</label>
                <input
                  id="buyoutPrice"
                  type="text"
                  value={item.buyoutPrice}
                  readOnly
                  className="readonly-input"
                />
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              value={item.description}
              readOnly
              className="readonly-input"
              rows="4"
            />
          </div>

          {item.imageUrls && item.imageUrls.length > 0 && (
            <div className="card mb-4">
              <h2 className="mb-3">Images</h2>
              <div className="grid grid-4 gap-2">
                {item.imageUrls.map((url, idx) => (
                  <div key={idx} className="card" style={{ position: 'relative' }}>
                    <img
                      src={getImageUrl(url)}
                      alt="item"
                      style={{
                        width: '100%',
                        height: '120px',
                        objectFit: 'cover',
                        borderRadius: 'var(--border-radius)',
                        border: url === item.primaryImageUrl ? '2px solid var(--primary-color)' : '1px solid var(--border-color)'
                      }}
                    />
                    {url === item.primaryImageUrl && (
                      <div className="mt-2 text-center">
                        <span className="btn btn-primary btn-sm">Primary Image</span>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="text-center">
            {item.status === 'ACTIVE' && isOwner && (
              <button
                type="button"
                className="btn btn-primary mr-2"
                onClick={() => navigate(`/items/edit/${itemId}`)}
              >
                Edit Item
              </button>
            )}
            {item.status === 'ACTIVE' && !isOwner && (
              <button
                type="button"
                className="btn btn-primary mr-2"
                onClick={() => navigate(`/items/${itemId}`)}
              >
                Place Bid
              </button>
            )}
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate(backDestination)}
            >
              {backText}
            </button>
          </div>
        </form>
      </div>
      
      {user && item.user && (
        <ItemChat itemId={itemId} sellerId={item.user.userId} />
      )}
    </div>
  );
}

export default ViewItem; 
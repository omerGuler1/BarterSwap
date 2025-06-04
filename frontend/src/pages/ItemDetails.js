import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';
import ItemChat from '../components/ItemChat';
import { useAuth } from '../context/AuthContext';

function ItemDetail() {
  const { itemId } = useParams();
  const navigate = useNavigate();
  const { user, loading: authLoading } = useAuth();
  const [item, setItem] = useState(null);
  const [bidAmount, setBidAmount] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [highestBid, setHighestBid] = useState(null);
  const [noBids, setNoBids] = useState(false);
  const [justBought, setJustBought] = useState(false);

  useEffect(() => {
    fetchItemDetails();
    fetchHighestBid();
  }, [itemId]);

  const fetchItemDetails = async (afterBid = false) => {
    try {
      const response = await api.get(`/items/${itemId}`);
      const itemData = response.data;
      setItem(itemData);
      
      // Check if the item was just sold and we are the buyer
      if (afterBid && itemData.status === 'SOLD' && user && user.userId !== itemData.user?.userId) {
        setJustBought(true);
      }
      
      setLoading(false);
    } catch (err) {
      console.error('Error fetching item details:', err);
      if (err.response?.status === 401) {
        localStorage.removeItem('token');
        navigate('/login');
      } else if (err.response?.status === 404 && afterBid) {
        setJustBought(true);
        setItem(null);
        setError('');
      } else {
        setError('Failed to fetch item details. Please try again later.');
      }
      setLoading(false);
    }
  };

  const fetchHighestBid = async () => {
    try {
      const response = await api.get(`/bids/highest?itemId=${itemId}`);
      setHighestBid(response.data);
      setNoBids(false);
    } catch (err) {
      if (err.response && err.response.status === 404) {
        setHighestBid(null);
        setNoBids(true);
      } else {
        setHighestBid(null);
        setNoBids(false);
      }
    }
  };

  const handleBid = async (e) => {
    e.preventDefault();
    try {
      const bidResponse = await api.post('/bids', { 
        itemId: parseInt(itemId), 
        bidAmount: parseFloat(bidAmount) 
      });
      
      // Check if this bid triggered a buyout
      const bidAmountValue = parseFloat(bidAmount);
      if (item.buyoutPrice && bidAmountValue >= item.buyoutPrice) {
        // Buyout triggered - show success message
        setJustBought(true);
        setError('');
        return;
      }
      
      setJustBought(false);
      await fetchItemDetails(true);
      fetchHighestBid();
      setBidAmount('');
      setError('');
    } catch (err) {
      console.error('Error placing bid:', err);
      if (err.response?.status === 401) {
        localStorage.removeItem('token');
        navigate('/login');
      } else {
        setError(err.response?.data?.message || 'Failed to place bid. Please try again.');
      }
    }
  };

  if (authLoading || loading) return (
    <div className="page-container text-center">
      <p>Loading...</p>
    </div>
  );

  if (justBought) return (
    <div className="page-container text-center">
      <div className="alert alert-success" style={{ marginTop: 40 }}>
        <h2>🎉 Congratulations!</h2>
        <p>You have successfully purchased this item!</p>
        <p className="text-light">The item is now yours and your virtual currency has been updated.</p>
        <div style={{ 
          background: '#f8f9fa', 
          borderRadius: '8px', 
          padding: '1rem', 
          margin: '1rem 0',
          border: '1px solid #e9ecef'
        }}>
          <h4 style={{ margin: '0 0 0.5rem 0', color: '#495057' }}>What's Next?</h4>
          <p style={{ margin: '0 0 0.5rem 0', fontWeight: 'bold' }}>⭐ Share your experience and reward great sellers!</p>
          <p style={{ margin: '0', fontSize: '0.9em', color: '#6c757d' }}>
            Your feedback matters: 4-5 stars gives sellers a +50 VC bonus, while 1-2 stars applies a -25 VC penalty.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', marginTop: '1rem' }}>
          <button className="btn btn-primary" onClick={() => navigate('/my-purchases')}>Leave Feedback</button>
          <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>Back to Marketplace</button>
        </div>
      </div>
    </div>
  );

  if (!item) return (
    <div className="page-container">
      <p className="text-error">Item not found</p>
    </div>
  );

  const canPlaceBid = item && user && 
    item.status === 'ACTIVE' && 
    item.user?.userId !== user.userId && 
    (!highestBid || highestBid.userId !== user.userId);

  return (
    <div className="app-container">
      <nav>
        <div className="nav-container">
          <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
            ← Back to Marketplace
          </button>
        </div>
      </nav>

      <div className="grid grid-2 gap-4">
        {/* Item Images */}
        <div className="card">
          {item.primaryImageUrl && (
            <img 
              src={getImageUrl(item.primaryImageUrl)} 
              alt={item.title}
              style={{ width: '100%', height: '400px', objectFit: 'cover', borderRadius: 'var(--border-radius)' }}
            />
          )}
          {item.imageUrls && item.imageUrls.length > 1 && (
            <div className="grid grid-4 gap-2 mt-3">
              {item.imageUrls.map((url, index) => (
                url ? (
                  <img 
                    key={index}
                    src={getImageUrl(url)}
                    alt={`${item.title} - Image ${index + 1}`}
                    style={{ width: '100%', height: '80px', objectFit: 'cover', borderRadius: 'var(--border-radius)' }}
                  />
                ) : null
              ))}
            </div>
          )}
        </div>

        {/* Item Details */}
        <div className="card">
          <h1>{item.title}</h1>
          <p className="text-light mb-4">{item.description}</p>

          <div className="grid grid-2 gap-4 mb-4">
            <div>
              <strong>Category:</strong>
              <p>{item.category}</p>
            </div>
            <div>
              <strong>Condition:</strong>
              <p>{item.condition}</p>
            </div>
            <div>
              <strong>Current Price:</strong>
              <p>${item.currentPrice}</p>
            </div>
            {item.buyoutPrice && (
              <div>
                <strong>Buyout Price:</strong>
                <p>${item.buyoutPrice}</p>
              </div>
            )}
          </div>

          {noBids ? (
            <div className="card mb-4" style={{ background: 'var(--background)' }}>
              <strong>No bids yet for this item.</strong>
            </div>
          ) : highestBid && (
            <div className="card mb-4" style={{ background: 'var(--background)' }}>
              <strong>Highest Bid:</strong>
              <p>${highestBid.bidAmount} by User #{highestBid.userId}</p>
            </div>
          )}

          {item.auctionEndTime && (
            <div className="mb-4">
              <strong>Auction Ends:</strong>
              <p>{new Date(item.auctionEndTime).toLocaleString()}</p>
            </div>
          )}

          {item.status === 'ACTIVE' && (
            <div className="card">
              <h2 className="mb-3">Place a Bid</h2>
              {error && <p className="text-error mb-3">{error}</p>}
              <form onSubmit={handleBid}>
                <div className="form-group">
                  <label htmlFor="bidAmount">Bid Amount ($)</label>
                  <input
                    id="bidAmount"
                    type="number"
                    step="0.01"
                    value={bidAmount}
                    onChange={(e) => setBidAmount(e.target.value)}
                    required
                    min={item.currentPrice + 0.01}
                  />
                </div>
                <div className="item-actions">
                  <button 
                    className="bid-button"
                    type="submit"
                    disabled={!canPlaceBid}
                  >
                    Place a Bid
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      </div>
      {user && item.user && (
        <ItemChat itemId={itemId} sellerId={item.user.userId} />
      )}
    </div>
  );
}

export default ItemDetail; 
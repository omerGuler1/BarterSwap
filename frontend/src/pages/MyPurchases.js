import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';

function MyPurchases() {
  const [purchasedItems, setPurchasedItems] = useState([]);
  const [eligibleTransactions, setEligibleTransactions] = useState([]);
  const [givenFeedback, setGivenFeedback] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('all'); // 'all', 'pending', 'completed'
  const [feedbackForm, setFeedbackForm] = useState({
    transactionId: null,
    score: '',
    comment: ''
  });
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const scoreMap = {
    1: 'ONE_STAR',
    2: 'TWO_STARS',
    3: 'THREE_STARS',
    4: 'FOUR_STARS',
    5: 'FIVE_STARS'
  };

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      // Fetch all completed transactions
      const purchasesResponse = await api.get('/transactions/my-purchases');
      setPurchasedItems(purchasesResponse.data);

      // Fetch transactions eligible for feedback
      const eligibleResponse = await api.get('/feedback/eligible-transactions');
      setEligibleTransactions(eligibleResponse.data);

      // Fetch feedback I've already given
      const givenResponse = await api.get('/feedback/my-given');
      setGivenFeedback(givenResponse.data);

      setLoading(false);
    } catch (err) {
      console.error('Error fetching purchase data:', err);
      setError(err.response?.data?.message || 'Failed to fetch purchase data.');
      setPurchasedItems([]);
      setEligibleTransactions([]);
      setGivenFeedback([]);
      setLoading(false);
    }
  };

  const handleLeaveFeedback = (transaction) => {
    setFeedbackForm({
      transactionId: transaction.transactionId,
      score: '',
      comment: ''
    });
    setShowFeedbackForm(true);
  };

  const handleFeedbackSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await api.post('/feedback', {
        transactionId: feedbackForm.transactionId,
        score: feedbackForm.score,
        comment: feedbackForm.comment
      });

      // Refresh data after successful feedback submission
      await fetchData();
      setShowFeedbackForm(false);
      setFeedbackForm({ transactionId: null, score: '', comment: '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit feedback.');
    }
    setSubmitting(false);
  };

  const handleStarClick = (stars) => {
    setFeedbackForm({ ...feedbackForm, score: scoreMap[stars] });
  };

  const formatFeedbackScore = (score) => {
    const scoreMap = {
      'ONE_STAR': 1,
      'TWO_STARS': 2,
      'THREE_STARS': 3,
      'FOUR_STARS': 4,
      'FIVE_STARS': 5
    };
    const numStars = scoreMap[score] || 0;
    return `${'⭐'.repeat(numStars)} (${numStars} stars)`;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) return (
    <div className="page-container text-center">
      <p>Loading...</p>
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
          <h1>My Purchases</h1>
          <p className="text-light">View your purchased items and manage feedback</p>
        </div>

        {error && <p className="text-error mb-3">{error}</p>}

        {/* Tab Navigation */}
        <div className="card mb-4">
          <div style={{ display: 'flex', borderBottom: '1px solid #eee' }}>
            <button
              className={`btn ${activeTab === 'all' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setActiveTab('all')}
              style={{ borderRadius: '0', border: 'none', marginRight: '1px' }}
            >
              All Purchases ({purchasedItems.length})
            </button>
            <button
              className={`btn ${activeTab === 'pending' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setActiveTab('pending')}
              style={{ borderRadius: '0', border: 'none', marginRight: '1px' }}
            >
              Pending Feedback ({eligibleTransactions.length})
            </button>
            <button
              className={`btn ${activeTab === 'completed' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setActiveTab('completed')}
              style={{ borderRadius: '0', border: 'none' }}
            >
              Given Feedback ({givenFeedback.length})
            </button>
          </div>
        </div>

        {/* All Purchases Tab */}
        {activeTab === 'all' && (
          <div className="card">
            <h2>All Purchased Items</h2>
            {purchasedItems.length === 0 ? (
              <div className="text-center text-light p-4">
                You haven't made any purchases yet. Check out the marketplace!
              </div>
            ) : (
              <div className="grid grid-1 gap-4">
                {purchasedItems.map((transaction) => (
                  <div 
                    key={transaction.transactionId} 
                    className="card"
                    style={{ border: '1px solid #eee', padding: '1rem' }}
                  >
                    <div className="grid grid-2 gap-4">
                      <div>
                        {transaction.item?.primaryImageUrl && (
                          <img 
                            src={getImageUrl(transaction.item.primaryImageUrl)} 
                            alt={transaction.item.title}
                            style={{ 
                              width: '100%', 
                              height: '120px', 
                              objectFit: 'cover', 
                              borderRadius: 'var(--border-radius)' 
                            }}
                          />
                        )}
                      </div>
                      <div>
                        <h3>{transaction.item?.title}</h3>
                        <div className="mb-2">
                          <strong>Seller:</strong> {transaction.seller?.username}
                        </div>
                        <div className="mb-2">
                          <strong>Price Paid:</strong> ${transaction.price}
                        </div>
                        <div className="mb-2">
                          <strong>Purchase Date:</strong> {formatDate(transaction.transactionDate)}
                        </div>
                        {!transaction.hasFeedback && (
                          <button 
                            className="btn btn-primary"
                            onClick={() => handleLeaveFeedback(transaction)}
                          >
                            Leave Feedback
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Pending Feedback Tab */}
        {activeTab === 'pending' && (
          <div className="card">
            <h2>Items Awaiting Your Feedback</h2>
            {eligibleTransactions.length === 0 ? (
              <div className="text-center text-light p-4">
                No purchases awaiting feedback. All done! 🎉
              </div>
            ) : (
              <div className="grid grid-1 gap-4">
                {eligibleTransactions.map((transaction) => (
                  <div 
                    key={transaction.transactionId} 
                    className="card"
                    style={{ border: '1px solid #eee', padding: '1rem' }}
                  >
                    <div className="grid grid-2 gap-4">
                      <div>
                        {transaction.item?.primaryImageUrl && (
                          <img 
                            src={getImageUrl(transaction.item.primaryImageUrl)} 
                            alt={transaction.item.title}
                            style={{ 
                              width: '100%', 
                              height: '120px', 
                              objectFit: 'cover', 
                              borderRadius: 'var(--border-radius)' 
                            }}
                          />
                        )}
                      </div>
                      <div>
                        <h3>{transaction.item?.title}</h3>
                        <div className="mb-2">
                          <strong>Seller:</strong> {transaction.seller?.username}
                        </div>
                        <div className="mb-2">
                          <strong>Price Paid:</strong> ${transaction.price}
                        </div>
                        <div className="mb-2">
                          <strong>Purchase Date:</strong> {formatDate(transaction.transactionDate)}
                        </div>
                        <button 
                          className="btn btn-primary"
                          onClick={() => handleLeaveFeedback(transaction)}
                        >
                          Leave Feedback
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Completed Feedback Tab */}
        {activeTab === 'completed' && (
          <div className="card">
            <h2>Feedback You've Given</h2>
            {givenFeedback.length === 0 ? (
              <div className="text-center text-light p-4">
                You haven't given any feedback yet.
              </div>
            ) : (
              <div className="grid grid-1 gap-4">
                {givenFeedback.map((feedback) => (
                  <div 
                    key={feedback.feedbackId} 
                    className="card"
                    style={{ 
                      border: '1px solid #eee', 
                      padding: '1rem',
                      background: feedback.score.includes('FOUR') || feedback.score.includes('FIVE') ? '#f8fff8' : 
                                feedback.score.includes('THREE') ? '#fff' :
                                '#fff8f8'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                      <div>
                        <h3>{feedback.itemTitle}</h3>
                        <div className="text-light">to {feedback.receiverUsername}</div>
                      </div>
                      <div className="text-right">
                        <div style={{ fontSize: '1.2em', marginBottom: '0.25rem' }}>
                          {formatFeedbackScore(feedback.score)}
                        </div>
                        <small className="text-light">
                          {formatDate(feedback.timestamp)}
                        </small>
                      </div>
                    </div>
                    
                    {feedback.comment && (
                      <div>
                        <strong>Your Comment:</strong>
                        <p style={{ margin: '0.5rem 0 0 0', fontStyle: 'italic' }}>
                          "{feedback.comment}"
                        </p>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Feedback Form Modal */}
        {showFeedbackForm && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000
          }}>
            <div className="card" style={{ width: '500px', maxWidth: '90vw' }}>
              <h2>Leave Feedback</h2>
              <form onSubmit={handleFeedbackSubmit}>
                <div className="form-group">
                  <h2 style={{ marginBottom: '1rem' }}>We value your opinion.</h2>
                  <div className="alert alert-info mb-3">
                    <p><strong>🎁 Seller Reward System</strong></p>
                    <p>Your feedback helps reward great sellers:</p>
                    <ul style={{ marginBottom: 0 }}>
                      <li>4-5 stars: Seller receives +50 VC bonus</li>
                      <li>1-2 stars: Seller receives -25 VC penalty</li>
                      <li>3 stars: No VC adjustment</li>
                    </ul>
                  </div>
                  <label style={{ display: 'block', marginBottom: '1rem', fontSize: '1.1em' }}>
                    How would you rate your overall experience?
                  </label>
                  <div style={{ 
                    display: 'flex', 
                    justifyContent: 'center', 
                    gap: '0.5rem',
                    marginBottom: '2rem'
                  }}>
                    {[1, 2, 3, 4, 5].map((stars) => (
                      <button
                        key={stars}
                        type="button"
                        onClick={() => handleStarClick(stars)}
                        style={{
                          background: 'none',
                          border: 'none',
                          cursor: 'pointer',
                          fontSize: '2rem',
                          color: feedbackForm.score === scoreMap[stars] ? '#ffc107' : '#e4e5e9',
                          transition: 'color 0.2s ease'
                        }}
                        onMouseEnter={(e) => {
                          const parent = e.target.parentElement;
                          const stars = Array.from(parent.children);
                          const index = stars.indexOf(e.target);
                          stars.forEach((star, i) => {
                            star.style.color = i <= index ? '#ffc107' : '#e4e5e9';
                          });
                        }}
                        onMouseLeave={(e) => {
                          const parent = e.target.parentElement;
                          const stars = Array.from(parent.children);
                          const selectedStars = Number(Object.entries(scoreMap).find(([_, val]) => val === feedbackForm.score)?.[0] || 0);
                          stars.forEach((star, i) => {
                            star.style.color = i < selectedStars ? '#ffc107' : '#e4e5e9';
                          });
                        }}
                      >
                        ★
                      </button>
                    ))}
                  </div>
                </div>
                
                <div className="form-group">
                  <label htmlFor="comment" style={{ display: 'block', marginBottom: '0.5rem' }}>
                    Kindly take a moment to tell us what you think.
                  </label>
                  <textarea
                    id="comment"
                    value={feedbackForm.comment}
                    onChange={(e) => setFeedbackForm({ ...feedbackForm, comment: e.target.value })}
                    rows="4"
                    placeholder="Your feedback helps us improve..."
                    maxLength="1000"
                    style={{
                      width: '100%',
                      padding: '0.75rem',
                      borderRadius: '8px',
                      border: '1px solid #ddd',
                      marginBottom: '1rem',
                      resize: 'vertical'
                    }}
                  />
                </div>

                <div className="text-center">
                  <button 
                    type="submit" 
                    className="btn btn-primary"
                    disabled={submitting || !feedbackForm.score}
                    style={{
                      padding: '0.75rem 2rem',
                      fontSize: '1.1em'
                    }}
                  >
                    {submitting ? 'Submitting...' : 'Share my feedback'}
                  </button>
                  <button 
                    type="button" 
                    className="btn btn-secondary ml-2"
                    onClick={() => {
                      setShowFeedbackForm(false);
                      setFeedbackForm({ transactionId: null, score: '', comment: '' });
                      setError('');
                    }}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default MyPurchases; 
import React, { useState, useEffect } from 'react';
import api from '../utils/axios';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState({ username: '', email: '', studentId: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [reputationDetails, setReputationDetails] = useState(null);
  const [receivedFeedback, setReceivedFeedback] = useState([]);
  const [activeTab, setActiveTab] = useState('profile'); // 'profile', 'reputation'
  const navigate = useNavigate();

  useEffect(() => {
    fetchProfile();
    // eslint-disable-next-line
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get('/profile');
      setProfile(response.data);
      setForm({
        username: response.data.username,
        email: response.data.email,
        studentId: response.data.studentId || ''
      });
      
      // Fetch reputation details and feedback
      await fetchReputationDetails(response.data.userId);
      await fetchReceivedFeedback(response.data.userId);
      
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch profile.');
      setLoading(false);
    }
  };

  const fetchReputationDetails = async (userId) => {
    try {
      const response = await api.get(`/feedback/reputation/${userId}`);
      setReputationDetails(response.data);
    } catch (err) {
      console.error('Failed to fetch reputation details:', err);
    }
  };

  const fetchReceivedFeedback = async (userId) => {
    try {
      const response = await api.get(`/feedback/user/${userId}`);
      setReceivedFeedback(response.data);
    } catch (err) {
      console.error('Failed to fetch received feedback:', err);
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await api.put('/profile', form);
      setSuccess('Profile updated successfully!');
      setEditMode(false);
      if (form.username !== profile.username || form.email !== profile.email) {
        localStorage.removeItem('token');
        navigate('/login');
        return;
      }
      fetchProfile();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile.');
    }
  };

  const getReputationColor = (reputation) => {
    if (reputation > 5) return '#28a745'; // Green for good reputation
    if (reputation > 0) return '#ffc107'; // Yellow for neutral reputation
    if (reputation < 0) return '#dc3545'; // Red for poor reputation
    return '#6c757d'; // Gray for no reputation
  };

  const getScoreBackgroundColor = (score) => {
    const starMap = {
      'ONE_STARS': 1,
      'TWO_STARS': 2,
      'THREE_STARS': 3,
      'FOUR_STARS': 4,
      'FIVE_STARS': 5
    };
    
    const stars = starMap[score] || 0;
    return stars === 5 ? '#e8f5e9' : // Light green background for 5 stars
           stars === 4 ? '#f1f8e9' : // Very light green for 4 stars
           stars === 3 ? '#fff8e1' : // Light yellow for 3 stars
           stars === 2 ? '#fff3e0' : // Light orange for 2 stars
                        '#ffebee';   // Light red for 1 star
  };

  const formatFeedbackScore = (score) => {
    const starMap = {
      'ONE_STARS': 1,
      'TWO_STARS': 2,
      'THREE_STARS': 3,
      'FOUR_STARS': 4,
      'FIVE_STARS': 5
    };
    
    const stars = starMap[score] || 0;
    return (
      <span style={{ fontSize: '1.2em', marginRight: '8px' }}>
        {'⭐'.repeat(stars)}
      </span>
    );
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
          <h1>My Profile</h1>
          <p className="text-light">View and edit your account details</p>
        </div>
        
        {success && <p className="text-success mb-3">{success}</p>}
        
        {/* Tab Navigation */}
        <div className="card mb-4">
          <div style={{ display: 'flex', borderBottom: '1px solid #eee' }}>
            <button
              className={`btn ${activeTab === 'profile' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setActiveTab('profile')}
              style={{ borderRadius: '0', border: 'none', marginRight: '1px' }}
            >
              Profile Info
            </button>
            <button
              className={`btn ${activeTab === 'reputation' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setActiveTab('reputation')}
              style={{ borderRadius: '0', border: 'none' }}
            >
              Reputation & Feedback
            </button>
          </div>
        </div>

        {/* Profile Tab */}
        {activeTab === 'profile' && (
          <>
        {!editMode ? (
          <div className="card" style={{ maxWidth: 500, margin: '0 auto' }}>
            <div className="grid grid-2 gap-4 mb-4">
              <div>
                <strong>Username:</strong>
                <p>{profile.username}</p>
              </div>
              <div>
                <strong>Email:</strong>
                <p>{profile.email}</p>
              </div>
              <div>
                <strong>Student ID:</strong>
                <p>{profile.studentId || '-'}</p>
              </div>
                  <div>
                    <strong>Reputation:</strong>
                    <p style={{ color: getReputationColor(profile.reputation), fontWeight: 'bold', fontSize: '1.2em' }}>
                      {profile.reputation}
                    </p>
              </div>
            </div>
            <div className="text-center">
              <button className="btn btn-primary" onClick={() => setEditMode(true)}>Edit Profile</button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="card" style={{ maxWidth: 500, margin: '0 auto' }}>
            <div className="grid grid-2 gap-4 mb-4">
              <div className="form-group">
                <label htmlFor="username">Username</label>
                <input
                  id="username"
                  name="username"
                  type="text"
                  value={form.username}
                  onChange={handleChange}
                  required
                  placeholder="Enter username"
                />
              </div>
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  required
                  placeholder="Enter email"
                />
              </div>
              <div className="form-group">
                <label htmlFor="studentId">Student ID</label>
                <input
                  id="studentId"
                  name="studentId"
                  type="text"
                  value={form.studentId}
                  onChange={handleChange}
                  placeholder="Enter student ID (optional)"
                />
              </div>
            </div>
            <div className="text-center">
              <button type="submit" className="btn btn-primary">Save</button>
              <button type="button" className="btn btn-secondary ml-2" onClick={() => setEditMode(false)}>Cancel</button>
            </div>
          </form>
            )}
          </>
        )}

        {/* Reputation Tab */}
        {activeTab === 'reputation' && (
          <>
            {/* Reputation Overview */}
            {reputationDetails && (
              <div className="card mb-4">
                <h2>Reputation Overview</h2>
                <div className="grid grid-3 gap-4 text-center">
                  <div>
                    <h3 style={{ color: getReputationColor(reputationDetails.reputation), margin: '0' }}>
                      {reputationDetails.reputation}
                    </h3>
                    <p className="text-light">Overall Reputation</p>
                  </div>
                  <div>
                    <h3 style={{ color: '#28a745', margin: '0' }}>
                      {reputationDetails.positiveFeedback}
                    </h3>
                    <p className="text-light">Positive Feedback</p>
                  </div>
                  <div>
                    <h3 style={{ color: '#dc3545', margin: '0' }}>
                      {reputationDetails.negativeFeedback}
                    </h3>
                    <p className="text-light">Negative Feedback</p>
                  </div>
                </div>
                
                {reputationDetails.totalFeedback > 0 && (
                  <div className="mt-4 text-center">
                    <div style={{ 
                      background: '#f8f9fa', 
                      borderRadius: '8px', 
                      padding: '1rem',
                      display: 'inline-block'
                    }}>
                      <strong>Success Rate: </strong>
                      <span style={{ 
                        color: reputationDetails.reputationPercentage >= 80 ? '#28a745' : 
                               reputationDetails.reputationPercentage >= 60 ? '#ffc107' : '#dc3545',
                        fontWeight: 'bold'
                      }}>
                        {reputationDetails.reputationPercentage.toFixed(1)}%
                      </span>
                      <span className="text-light"> ({reputationDetails.totalFeedback} total reviews)</span>
                    </div>
                  </div>
                )}

                {reputationDetails.totalReviews > 0 && (
                  <div className="mt-4">
                    <div style={{ 
                      background: '#f8f9fa', 
                      borderRadius: '8px', 
                      padding: '1.5rem'
                    }}>
                      <div className="text-center mb-3">
                        <h3 style={{ marginBottom: '0.5rem' }}>
                          {reputationDetails.averageRating?.toFixed(1)} 
                          <span style={{ color: '#ffc107' }}> ⭐</span>
                        </h3>
                        <div className="text-light">
                          {reputationDetails.totalReviews} reviews
                        </div>
                      </div>

                      {/* Star Distribution */}
                      <div style={{ maxWidth: '300px', margin: '0 auto' }}>
                        {[5, 4, 3, 2, 1].map(stars => {
                          const count = reputationDetails[`${stars === 1 ? 'one' : stars === 2 ? 'two' : stars === 3 ? 'three' : stars === 4 ? 'four' : 'five'}StarCount`];
                          const percentage = (count / reputationDetails.totalReviews) * 100;
                          
                          return (
                            <div key={stars} style={{ display: 'flex', alignItems: 'center', marginBottom: '0.5rem' }}>
                              <div style={{ width: '60px' }}>
                                {stars} {'⭐'}
                              </div>
                              <div style={{ flex: 1, marginLeft: '0.5rem', marginRight: '0.5rem' }}>
                                <div style={{ 
                                  height: '8px', 
                                  background: '#e9ecef',
                                  borderRadius: '4px',
                                  overflow: 'hidden'
                                }}>
                                  <div style={{
                                    width: `${percentage}%`,
                                    height: '100%',
                                    background: '#ffc107',
                                    transition: 'width 0.3s ease'
                                  }} />
                                </div>
                              </div>
                              <div style={{ width: '40px', textAlign: 'right' }}>
                                {count}
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Received Feedback */}
            <div className="card">
              <h2>Received Feedback</h2>
              {receivedFeedback.length === 0 ? (
                <div className="text-center text-light p-4">
                  No feedback received yet. Complete transactions as a seller to receive feedback!
                </div>
              ) : (
                <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
                  {receivedFeedback.map((feedback) => (
                    <div 
                      key={feedback.feedbackId} 
                      className="mb-3 p-3" 
                      style={{ 
                        border: '1px solid #eee', 
                        borderRadius: '8px',
                        background: getScoreBackgroundColor(feedback.score)
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                        <div>
                          {formatFeedbackScore(feedback.score)}
                          <span className="text-light ml-2">from {feedback.giverUsername}</span>
                        </div>
                        <small className="text-light">
                          {new Date(feedback.timestamp).toLocaleDateString()}
                        </small>
                      </div>
                      
                      <div className="mb-2">
                        <strong>Item:</strong> {feedback.itemTitle}
                      </div>
                      
                      {feedback.comment && (
                        <div>
                          <strong>Comment:</strong>
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
          </>
        )}
      </div>
    </div>
  );
}

export default Profile; 
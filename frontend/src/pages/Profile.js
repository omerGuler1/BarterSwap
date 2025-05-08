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
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch profile.');
      setLoading(false);
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
      </div>
    </div>
  );
}

export default Profile; 
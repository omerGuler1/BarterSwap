import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [studentNo, setStudentNo] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/v1/auth/register', { username, password, email, studentId: studentNo });
      navigate('/login');
    } catch (err) {
      setError('Registration failed. Please try again.');
    }
  };

  return (
    <div className="page-container">
      <div className="text-center mb-4">
        <h1>Create Account</h1>
        <p className="text-light">Join our marketplace community</p>
      </div>
      
      {error && <p className="text-error mb-3">{error}</p>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            placeholder="Choose a username"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="Enter your email"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            placeholder="Create a password"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="studentNo">Student Number</label>
          <input
            id="studentNo"
            type="text"
            value={studentNo}
            onChange={(e) => setStudentNo(e.target.value)}
            required
            placeholder="Enter your student number"
          />
        </div>
        
        <div className="text-center mt-4">
          <button type="submit" className="btn btn-primary">Create Account</button>
        </div>
        
        <p className="text-center mt-4">
          Already have an account?{' '}
          <a href="/login" className="text-primary">Sign in here</a>
        </p>
      </form>
    </div>
  );
}

export default Register; 
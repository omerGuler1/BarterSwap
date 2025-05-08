import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Marketplace from './pages/Marketplace';
import ItemDetails from './pages/ItemDetails';
import Profile from './pages/Profile';
import ProtectedRoute from './ProtectedRoute';

export default function App() {
  return (
    <div>
      <nav style={{ padding: 16, borderBottom: '1px solid #eee' }}>
        <Link to="/" style={{ marginRight: 16 }}>Marketplace</Link>
        <Link to="/profile" style={{ marginRight: 16 }}>Profile</Link>
        <Link to="/login" style={{ marginRight: 16 }}>Login</Link>
        <Link to="/register">Register</Link>
      </nav>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Marketplace />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
        <Route
          path="/items/:itemId"
          element={
            <ProtectedRoute>
              <ItemDetails />
            </ProtectedRoute>
          }
        />
      </Routes>
    </div>
  );
} 
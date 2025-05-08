import React from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ItemDetail from './pages/ItemDetail';
import CreateItem from './pages/CreateItem';
import MyItems from './pages/MyItems';
import EditItem from './pages/EditItem';
import ItemsByStatus from './pages/ItemsByStatus';
import Profile from './pages/Profile';
import ProtectedRoute from './components/ProtectedRoute';
import VirtualCurrency from './pages/VirtualCurrency';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } />
        <Route path="/my-items" element={
          <ProtectedRoute>
            <MyItems />
          </ProtectedRoute>
        } />
        <Route path="/items/new" element={
          <ProtectedRoute>
            <CreateItem />
          </ProtectedRoute>
        } />
        <Route path="/items/edit/:itemId" element={
          <ProtectedRoute>
            <EditItem />
          </ProtectedRoute>
        } />
        <Route path="/items/status" element={
          <ProtectedRoute>
            <ItemsByStatus />
          </ProtectedRoute>
        } />
        <Route path="/profile" element={
          <ProtectedRoute>
            <Profile />
          </ProtectedRoute>
        } />
        <Route path="/items/:itemId" element={
          <ProtectedRoute>
            <ItemDetail />
          </ProtectedRoute>
        } />
        <Route path="/virtual-currency" element={
          <ProtectedRoute>
            <VirtualCurrency />
          </ProtectedRoute>
        } />
        <Route path="/" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App; 
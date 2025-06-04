import React from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Analytics from './pages/Analytics';
import ItemDetails from './pages/ItemDetails';
import CreateItem from './pages/CreateItem';
import MyItems from './pages/MyItems';
import EditItem from './pages/EditItem';
import ViewItem from './pages/ViewItem';
import ItemsByStatus from './pages/ItemsByStatus';
import Profile from './pages/Profile';
import ProtectedRoute from './components/ProtectedRoute';
import VirtualCurrency from './pages/VirtualCurrency';
import MyPurchases from './pages/MyPurchases';
import { AuthProvider } from './context/AuthContext';

function App() {
  return (
    <AuthProvider>
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } />
        <Route path="/analytics" element={
          <ProtectedRoute>
            <Analytics />
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
        <Route path="/items/view/:itemId" element={
          <ProtectedRoute>
            <ViewItem />
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
              <ItemDetails />
          </ProtectedRoute>
        } />
        <Route path="/virtual-currency" element={
          <ProtectedRoute>
            <VirtualCurrency />
          </ProtectedRoute>
        } />
        <Route path="/my-purchases" element={
          <ProtectedRoute>
            <MyPurchases />
          </ProtectedRoute>
        } />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Router>
    </AuthProvider>
  );
}

export default App; 
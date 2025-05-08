import React, { useState } from 'react';
import { Link } from 'react-router-dom';

function Navigation() {
  const [isLoggedIn, setIsLoggedIn] = useState(true);

  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  return (
    <nav>
      <div className="nav-container">
        <div className="nav-links">
          <Link to="/dashboard" className="nav-link">Marketplace</Link>
          <Link to="/my-items" className="nav-link">My Items</Link>
          <Link to="/items/new" className="nav-link">Create Item</Link>
          <Link to="/virtual-currency" className="nav-link">Virtual Currency</Link>
          <Link to="/profile" className="nav-link">Profile</Link>
          <button onClick={handleLogout} className="btn btn-secondary">Logout</button>
        </div>
      </div>
    </nav>
  );
}

export default Navigation; 
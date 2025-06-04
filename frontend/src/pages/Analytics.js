import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';

function Analytics() {
  const [categoryReport, setCategoryReport] = useState([]);
  const [topSellers, setTopSellers] = useState([]);
  const [mostBiddedItems, setMostBiddedItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Colors for pie chart
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    try {
      setLoading(true);
      const [categoryRes, sellersRes, biddedRes] = await Promise.all([
        api.get('/reports/categories'),
        api.get('/reports/top-sellers'),
        api.get('/reports/most-bidded')
      ]);
      setCategoryReport(categoryRes.data);
      setTopSellers(sellersRes.data);
      setMostBiddedItems(biddedRes.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch reports');
      setLoading(false);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  };

  // Prepare data for pie chart
  const preparePieChartData = () => {
    return categoryReport.map(cat => ({
      name: cat.category,
      value: cat.itemsSold
    }));
  };

  if (loading) return (
    <div className="page-container text-center">
      <p>Loading analytics...</p>
    </div>
  );

  if (error) return (
    <div className="page-container text-error">
      <p>{error}</p>
    </div>
  );

  return (
    <div className="app-container">
      <nav>
        <div className="nav-container">
          <button className="btn btn-secondary" onClick={() => navigate('/dashboard')}>
            ← Back to Dashboard
          </button>
          <h1 style={{ marginLeft: '1rem' }}>Analytics & Reports</h1>
        </div>
      </nav>

      <div className="page-container">
        {/* Summary Cards */}
        <div className="grid grid-3 gap-4 mb-4">
          <div className="card text-center">
            <h3>Total Active Listings</h3>
            <p className="text-xl">
              {categoryReport.reduce((sum, cat) => sum + cat.activeListings, 0)}
            </p>
          </div>
          <div className="card text-center">
            <h3>Total Items Sold</h3>
            <p className="text-xl">
              {categoryReport.reduce((sum, cat) => sum + cat.itemsSold, 0)}
            </p>
          </div>
          <div className="card text-center">
            <h3>Active Sellers</h3>
            <p className="text-xl">{topSellers.length}</p>
          </div>
        </div>

        {/* Category Analysis */}
        <div className="card mb-4">
          <h2>Category Performance</h2>
          <div className="grid grid-2 gap-4">
            {/* Bar Chart */}
            <div style={{ height: '400px' }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={categoryReport}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="category" />
                  <YAxis yAxisId="left" />
                  <YAxis yAxisId="right" orientation="right" />
                  <Tooltip />
                  <Legend />
                  <Bar yAxisId="left" dataKey="activeListings" fill="#8884d8" name="Active Listings" />
                  <Bar yAxisId="left" dataKey="itemsSold" fill="#82ca9d" name="Items Sold" />
                  <Bar yAxisId="right" dataKey="averageSellingPrice" fill="#ffc658" name="Avg. Price" />
                </BarChart>
              </ResponsiveContainer>
            </div>

            {/* Pie Chart */}
            <div style={{ height: '400px' }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={preparePieChartData()}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={150}
                    fill="#8884d8"
                    label
                  >
                    {preparePieChartData().map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Detailed Category Table */}
          <div className="mt-4" style={{ overflowX: 'auto' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Category</th>
                  <th>Active Listings</th>
                  <th>Items Sold</th>
                  <th>Total Items</th>
                  <th>Avg. Starting Price</th>
                  <th>Avg. Sold Price</th>
                  <th>Conversion Rate</th>
                </tr>
              </thead>
              <tbody>
                {categoryReport.map(category => (
                  <tr key={category.category}>
                    <td>{category.category}</td>
                    <td>{category.activeListings}</td>
                    <td>{category.itemsSold}</td>
                    <td>{category.totalItems}</td>
                    <td>{formatCurrency(category.averageStartingPrice)}</td>
                    <td>{formatCurrency(category.averageSoldPrice)}</td>
                    <td>{category.conversionRate}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Most Bid-On Items */}
        <div className="card mb-4">
          <h2>Most Popular Items (By Bids)</h2>
          <div className="mt-4" style={{ overflowX: 'auto' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Item</th>
                  <th>Image</th>
                  <th>Seller</th>
                  <th>Bids</th>
                  <th>Current Price</th>
                  <th>Auction Ends</th>
                </tr>
              </thead>
              <tbody>
                {mostBiddedItems
                  .filter(item => item.status === 'ACTIVE')
                  .slice(0, 10)
                  .map((item, index) => (
                    <tr key={item.itemId}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <span>{item.itemTitle}</span>
                          {index < 3 && (
                            <span className="badge badge-warning">
                              {index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉'}
                            </span>
                          )}
                        </div>
                      </td>
                      <td>
                        {item.primaryImageUrl && (
                          <img
                            src={item.primaryImageUrl}
                            alt={item.itemTitle}
                            style={{
                              width: '40px',
                              height: '40px',
                              objectFit: 'cover',
                              borderRadius: '4px'
                            }}
                          />
                        )}
                      </td>
                      <td>{item.sellerUsername}</td>
                      <td>
                        <span className="badge badge-info">
                          {item.numberOfBids} bids
                        </span>
                      </td>
                      <td>{formatCurrency(item.finalPrice)}</td>
                      <td>
                        {item.auctionEndTime ? (
                          new Date(item.auctionEndTime).toLocaleString('en-US', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })
                        ) : (
                          <span className="text-light">No end time set</span>
                        )}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Top Sellers Leaderboard */}
        <div className="card">
          <h2>Top Sellers Leaderboard</h2>
          <div className="mt-4" style={{ overflowX: 'auto' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Seller</th>
                  <th>Items Sold</th>
                  <th>Total Income</th>
                  <th>Avg. Rating</th>
                  <th>Reputation</th>
                </tr>
              </thead>
              <tbody>
                {topSellers.map((seller, index) => (
                  <tr key={seller.userId}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        #{index + 1}
                        {index < 3 && (
                          <span className="badge badge-warning">
                            {index === 0 ? '🥇' : index === 1 ? '🥈' : '🥉'}
                          </span>
                        )}
                      </div>
                    </td>
                    <td>{seller.username}</td>
                    <td>{seller.itemsSold}</td>
                    <td>{formatCurrency(seller.totalIncome)}</td>
                    <td>
                      {seller.averageFeedbackScore ? (
                        <span>
                          {seller.averageFeedbackScore.toFixed(1)} ⭐
                          <small className="text-light ml-1">
                            ({seller.totalFeedbacks} reviews)
                          </small>
                        </span>
                      ) : (
                        <span className="text-light">No ratings</span>
                      )}
                    </td>
                    <td>
                      <span className={`badge badge-${
                        seller.reputation === 'Excellent' ? 'success' :
                        seller.reputation === 'Very Good' ? 'primary' :
                        seller.reputation === 'Good' ? 'info' :
                        seller.reputation === 'Average' ? 'warning' :
                        'danger'
                      }`}>
                        {seller.reputation}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Analytics; 
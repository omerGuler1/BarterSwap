import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/axios';

function VirtualCurrency() {
  const [balance, setBalance] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchBalanceAndTransactions();
  }, []);

  const fetchBalanceAndTransactions = async () => {
    try {
      const [balanceRes, transactionsRes] = await Promise.all([
        api.get('/virtual-currency/balance'),
        api.get('/virtual-currency/transactions')
      ]);
      setBalance(balanceRes.data);
      setTransactions(transactionsRes.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch virtual currency data');
      setLoading(false);
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
          <h1 style={{ marginLeft: '1rem' }}>Virtual Currency</h1>
        </div>
      </nav>
      <div className="page-container">
        {/* Balance Card */}
        <div className="card mb-4">
          <h2>Current Balance</h2>
          <div className="text-center">
            <h3 className="text-success">${balance?.balance.toFixed(2)}</h3>
            <p className="text-light">Last updated: {new Date(balance?.lastUpdated).toLocaleString()}</p>
          </div>
        </div>

        {/* Transaction History */}
        <div className="card" style={{ width: '100%', margin: '0 auto' }}>
          <h2>Transaction History</h2>
          {transactions.length === 0 ? (
            <p className="text-center">No transactions yet</p>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="table" style={{ width: '100%', minWidth: 600 }}>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Description</th>
                    <th>Item</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map(transaction => {
                    const userId = balance?.userId;
                    const isSeller = transaction.sellerId === userId;
                    return (
                      <tr key={transaction.transactionId}>
                        <td>{new Date(transaction.timestamp).toLocaleString()}</td>
                        <td>
                          <span className={
                            transaction.type === 'COMPLETED'
                              ? 'badge badge-success'
                              : transaction.amount > 0
                                ? 'badge badge-success'
                                : 'badge badge-danger'
                          }>
                            {transaction.type === 'COMPLETED' ? 'Sale' : 
                             transaction.type === 'FEEDBACK_REWARD' ? 'Feedback Reward' :
                             transaction.type === 'FEEDBACK_PENALTY' ? 'Feedback Penalty' :
                             transaction.type}
                          </span>
                          {transaction.type === 'COMPLETED' && (
                            <span className="badge badge-light ml-2">Item Sale</span>
                          )}
                          {transaction.type === 'FEEDBACK_REWARD' && (
                            <span className="badge badge-info ml-2">4-5 ⭐</span>
                          )}
                          {transaction.type === 'FEEDBACK_PENALTY' && (
                            <span className="badge badge-warning ml-2">1-2 ⭐</span>
                          )}
                        </td>
                        <td
                          style={{ color: isSeller ? '#28a745' : '#dc3545' }}
                        >
                          {isSeller ? '+' : '-'}${transaction.amount.toFixed(2)}
                          {transaction.type === 'COMPLETED' && isSeller && (
                            <small className="text-light ml-2">(Sale Price)</small>
                          )}
                          {(transaction.type === 'FEEDBACK_REWARD' || transaction.type === 'FEEDBACK_PENALTY') && (
                            <small className="text-light ml-2">(Feedback Bonus)</small>
                          )}
                        </td>
                        <td>{transaction.description}</td>
                        <td>{transaction.relatedItemTitle || '-'}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default VirtualCurrency; 
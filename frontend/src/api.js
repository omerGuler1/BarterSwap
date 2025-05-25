import axios from 'axios';

const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
});

API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth
export const login = (data) => API.post('/auth/login', data);
export const register = (data) => API.post('/auth/register', data);
export const getProfile = () => API.get('/profile');
export const updateProfile = (data) => API.put('/profile', data);

// Items
export const getActiveItems = () => API.get('/items/active');
export const searchItems = (params) => API.get('/items/search', { params });
export const getItemDetails = (itemId) => API.get(`/items/${itemId}`);
export const getOwnItems = () => API.get('/items/my-items');
export const createItem = (data) => API.post('/items', data);
export const updateItem = (itemId, data) => API.put(`/items/${itemId}`, data);
export const updateItemStatus = (itemId, data) => API.put(`/items/${itemId}/status`, data);
export const deleteItem = (itemId) => API.delete(`/items/${itemId}`);

// Bids
export const placeBid = (data) => API.post('/bids', data);
export const getHighestBid = (itemId) => API.get('/bids/highest', { params: { itemId } }); 

// Message related API calls
export const getItemMessages = async (itemId) => {
    return await API.get(`/messages/item/${itemId}`);
};

export const sendMessage = async (messageData) => {
    return await API.post('/messages', messageData);
}; 
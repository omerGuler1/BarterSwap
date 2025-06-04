import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../utils/axios';
import { getImageUrl } from '../utils/getImageUrl';

const categories = [
  'BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS'
];
const conditions = [
  'NEW', 'LIKE_NEW', 'USED', 'VERY_USED', 'DAMAGED'
];

function EditItem() {
  const { itemId } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('');
  const [startingPrice, setStartingPrice] = useState('');
  const [condition, setCondition] = useState('');
  const [auctionEndTime, setAuctionEndTime] = useState('');
  const [buyoutPrice, setBuyoutPrice] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [imageFiles, setImageFiles] = useState([]);
  const [imageObjs, setImageObjs] = useState([]);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    fetchItemDetails();
  }, [itemId]);

  const fetchItemDetails = async () => {
    try {
      const response = await api.get(`/items/${itemId}`);
      const item = response.data;
      setTitle(item.title);
      setDescription(item.description);
      setCategory(item.category);
      setStartingPrice(item.startingPrice.toString());
      setCondition(item.condition);
      setAuctionEndTime(item.auctionEndTime ? new Date(item.auctionEndTime).toISOString().slice(0, 16) : '');
      setBuyoutPrice(item.buyoutPrice ? item.buyoutPrice.toString() : '');
      
      // Set existing images
      if (item.imageUrls && item.imageUrls.length > 0) {
        setImageObjs(item.imageUrls.map(url => ({
          url,
          isPrimary: url === item.primaryImageUrl
        })));
      }
      
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch item details.');
      setLoading(false);
    }
  };

  const handleImageChange = (e) => {
    setImageFiles(Array.from(e.target.files));
  };

  const handleImageUpload = async () => {
    if (imageFiles.length === 0) return;
    setUploading(true);
    const formData = new FormData();
    imageFiles.forEach(file => formData.append('files', file));
    try {
      const response = await api.post('/upload/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setImageObjs(prev => {
        const newObjs = response.data
          .filter(url => !prev.some(img => img.url === url))
          .map((url, idx) => ({
            url,
            isPrimary: prev.length === 0 && idx === 0
          }));
        return [...prev, ...newObjs];
      });
      setUploading(false);
      setImageFiles([]);
      if (fileInputRef.current) fileInputRef.current.value = '';
    } catch (err) {
      setError('Failed to upload images.');
      setUploading(false);
    }
  };

  const handleSetPrimary = (idx) => {
    setImageObjs(imageObjs.map((img, i) => ({ ...img, isPrimary: i === idx })));
  };

  const handleRemoveImage = (idx) => {
    setImageObjs(imageObjs.filter((_, i) => i !== idx));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.put(`/items/${itemId}`, {
        title,
        description,
        category,
        startingPrice: parseFloat(startingPrice),
        condition,
        auctionEndTime: auctionEndTime ? new Date(auctionEndTime).toISOString() : null,
        buyoutPrice: buyoutPrice ? parseFloat(buyoutPrice) : null,
        imageUrls: imageObjs.map(img => img.url),
        primaryImageUrl: imageObjs.find(img => img.isPrimary)?.url
      });
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update item.');
    }
  };

  if (loading) return (
    <div className="page-container text-center">
      <p>Loading...</p>
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
          <h1>Edit Item</h1>
          <p className="text-light">Update your item details</p>
        </div>

        {error && <p className="text-error mb-3">{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="grid grid-2 gap-4">
            <div className="form-group">
              <label htmlFor="title">Title</label>
              <input
                id="title"
                type="text"
                value={title}
                onChange={e => setTitle(e.target.value)}
                required
                placeholder="Enter item title"
              />
            </div>

            <div className="form-group">
              <label htmlFor="category">Category</label>
              <select
                id="category"
                value={category}
                onChange={e => setCategory(e.target.value)}
              >
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="startingPrice">Starting Price ($)</label>
              <input
                id="startingPrice"
                type="number"
                step="0.01"
                value={startingPrice}
                onChange={e => setStartingPrice(e.target.value)}
                required
                min="0"
                placeholder="Enter starting price"
              />
            </div>

            <div className="form-group">
              <label htmlFor="condition">Condition</label>
              <select
                id="condition"
                value={condition}
                onChange={e => setCondition(e.target.value)}
              >
                {conditions.map(cond => (
                  <option key={cond} value={cond}>{cond}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="auctionEndTime">Auction End Time</label>
              <input
                id="auctionEndTime"
                type="datetime-local"
                value={auctionEndTime}
                onChange={e => setAuctionEndTime(e.target.value)}
                placeholder="Select end time"
              />
            </div>

            <div className="form-group">
              <label htmlFor="buyoutPrice">Buyout Price ($)</label>
              <input
                id="buyoutPrice"
                type="number"
                step="0.01"
                value={buyoutPrice}
                onChange={e => setBuyoutPrice(e.target.value)}
                min="0"
                placeholder="Enter buyout price (optional)"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              value={description}
              onChange={e => setDescription(e.target.value)}
              required
              placeholder="Describe your item"
              rows="4"
            />
          </div>

          <div className="card mb-4">
            <h2 className="mb-3">Images</h2>
            <div className="form-group">
              <input
                type="file"
                multiple
                accept="image/*"
                onChange={handleImageChange}
                ref={fileInputRef}
              />
              <button
                type="button"
                className="btn btn-secondary mt-2"
                onClick={handleImageUpload}
                disabled={uploading || imageFiles.length === 0}
              >
                {uploading ? 'Uploading...' : 'Upload Images'}
              </button>
            </div>

            {imageObjs.length > 0 && (
              <div className="mt-4">
                <h3 className="mb-3">Uploaded Images</h3>
                <div className="grid grid-4 gap-2">
                  {imageObjs.map((img, idx) => (
                    <div key={idx} className="card" style={{ position: 'relative' }}>
                      <img
                        src={getImageUrl(img.url)}
                        alt="item"
                        style={{
                          width: '100%',
                          height: '120px',
                          objectFit: 'cover',
                          borderRadius: 'var(--border-radius)',
                          border: img.isPrimary ? '2px solid var(--primary-color)' : '1px solid var(--border-color)'
                        }}
                      />
                      <div className="grid grid-2 gap-2 mt-2">
                        <button
                          type="button"
                          className={`btn ${img.isPrimary ? 'btn-primary' : 'btn-secondary'}`}
                          onClick={() => handleSetPrimary(idx)}
                        >
                          {img.isPrimary ? 'Primary' : 'Set Primary'}
                        </button>
                        <button
                          type="button"
                          className="btn btn-secondary"
                          onClick={() => handleRemoveImage(idx)}
                        >
                          Remove
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div className="text-center">
            <button type="submit" className="btn btn-primary">Update Item</button>
            <button
              type="button"
              className="btn btn-secondary ml-2"
              onClick={() => navigate('/dashboard')}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EditItem; 
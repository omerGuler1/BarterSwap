# BarterSwap Frontend

This is the React frontend for the BarterSwap marketplace.

## Setup

1. **Install Node.js (use nvm):**
   ```sh
   nvm install
   nvm use
   ```
2. **Install dependencies:**
   ```sh
   npm install
   ```
3. **Configure backend URL:**
   - Copy `.env.example` to `.env` and set your backend API URL.

4. **Start the app:**
   ```sh
   npm start
   ```
   The app will run at [http://localhost:3000](http://localhost:3000) (or 5173 if using Vite default).

## Features
- Register & Login (JWT auth)
- Browse & search items
- View item details
- Place bids
- View profile

## Environment Variables
See `.env.example` for configuration. 
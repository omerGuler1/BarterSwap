# BarterSwap 

A modern student e-commerce platform with virtual currency system, built for educational institutions to facilitate safe and secure trading among students.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Installation & Setup](#installation--setup)
- [API Documentation](#api-documentation)
- [Frontend Features](#frontend-features)
- [Database Performance](#database-performance)
- [Security Features](#security-features)
- [Testing](#testing)


## Overview

BarterSwap is a comprehensive marketplace platform designed specifically for students to buy, sell, and auction items using a virtual currency system. The platform promotes safe trading within educational communities while providing a rich feature set for managing auctions, bids, and transactions.

### Key Highlights
- **Virtual Currency System**: Secure internal economy using Virtual Coins (VC)
- **Real-time Auctions**: Time-based auctions with automatic bid processing
- **Smart Bidding**: Automatic refund system for outbid users
- **Performance Optimized**: Advanced database indexing and query optimization
- **Modern UI/UX**: Responsive design with real-time updates

## Features

### Marketplace Features
- **Item Browsing**: Paginated marketplace with advanced filtering
- **Search & Filter**: Keyword search with category-based filtering
- **Item Categories**: Books, Electronics, Clothes, Furniture, Stationery, Sports, Others
- **Item Management**: Create, edit, delete, and manage item listings
- **Image Support**: Multiple image upload with primary image selection

### Auction System
- **Timed Auctions**: Set auction end times with automatic processing
- **Real-time Bidding**: Live bid updates with instant validation
- **Buyout Options**: Set immediate purchase prices
- **Automatic Settlement**: Scheduled job processes expired auctions
- **Bid History**: Complete bidding history for all items

### Virtual Currency
- **VC Wallet**: Individual virtual currency balance management
- **Transaction History**: Complete transaction tracking
- **Automatic Transfers**: Seamless VC transfers during transactions
- **Refund System**: Automatic refunds for outbid users
- **Balance Validation**: Real-time balance checking

### User Management
- **JWT Authentication**: Secure token-based authentication
- **User Profiles**: Comprehensive user profile management
- **Reputation System**: Star-based rating with VC rewards/penalties
- **Student Verification**: Student ID validation system
- **Role-based Access**: User and admin role management

### Analytics & Reporting
- **Dashboard Analytics**: Real-time marketplace statistics
- **Category Performance**: Category-wise sales and performance metrics
- **User Activity**: Comprehensive user activity tracking
- **Most Bidded Items**: Popular items tracking
- **Transaction Reports**: Detailed transaction analytics

### Communication
- **Messaging System**: Direct messaging between users
- **Feedback System**: Post-transaction feedback with ratings
- **Notifications**: Real-time updates for bids and transactions

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Security**: Spring Security 6.x with JWT
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Validation**: Bean Validation (Hibernate Validator)
- **Monitoring**: Spring Boot Actuator

### Frontend
- **Framework**: React 18.2.0
- **Routing**: React Router DOM 6.10.0
- **HTTP Client**: Axios 1.3.5
- **Icons**: React Icons 5.5.0
- **Charts**: Recharts 2.15.3
- **Build Tool**: Vite / React Scripts

### Database & Performance
- **Database**: PostgreSQL with advanced indexing
- **Connection Pooling**: HikariCP
- **Query Optimization**: Custom performance monitoring
- **Caching**: HTTP caching headers
- **Migrations**: Flyway (optional)

### DevOps & Tools
- **Version Control**: Git
- **API Testing**: Postman collections
- **IDE**: IntelliJ IDEA
- **Database Management**: pgAdmin

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend │    │  Spring Boot    │    │   PostgreSQL    │
│                 │    │     Backend     │    │    Database     │
│  - Components   │◄──►│                 │◄──►│                 │
│  - State Mgmt   │    │  - REST APIs    │    │  - Tables       │
│  - Routing      │    │  - Services     │    │  - Indexes      │
│  - Styling      │    │  - Security     │    │  - Constraints  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Key Components

#### Backend Services
- **AuthenticationService**: JWT token management and user authentication
- **ItemService**: Item CRUD operations and marketplace logic
- **BidService**: Bidding system with automatic refunds
- **AuctionService**: Scheduled auction processing
- **VirtualCurrencyService**: VC balance management
- **FeedbackService**: Rating and review system
- **ReportingService**: Analytics and reporting

#### Frontend Pages
- **Dashboard**: Main marketplace with item browsing
- **ItemDetails**: Detailed item view with bidding
- **Profile**: User profile and settings management
- **MyItems**: User's item management
- **Analytics**: Marketplace statistics
- **VirtualCurrency**: VC balance and transaction history

## 🗄 Database Schema

### Core Tables

#### Users
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    reputation DECIMAL(3,2) DEFAULT 0.00,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Items
```sql
CREATE TABLE item (
    item_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL,
    starting_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    buyout_price DECIMAL(10,2),
    auction_end_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    is_active BOOLEAN DEFAULT true,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Bids
```sql
CREATE TABLE bid (
    bid_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    item_id INTEGER NOT NULL REFERENCES item(item_id),
    bid_amount DECIMAL(10,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Performance Indexes
- `idx_item_status_active_deleted` on items(status, is_active, is_deleted)
- `idx_item_category` on items(category)
- `idx_bid_item_id` on bid(item_id)
- `idx_users_email` on users(email)
- `idx_transaction_buyer_seller` on transaction(buyer_id, seller_id)

## Installation & Setup

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.8+

### Backend Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd barterswap2
```

2. **Database Setup**
```bash
# Create PostgreSQL database
createdb barterswap

# Update application.properties with your database credentials
```

3. **Configure Application**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/barterswap
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Build and Run Backend**
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
```bash
cd frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Configure Environment**
```bash
# Create .env file
echo "REACT_APP_API_URL=http://localhost:8080/api/v1" > .env
```

4. **Start Frontend**
```bash
npm start
```

The frontend will start at `http://localhost:3000`

## API Documentation

### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Token refresh

### Items
- `GET /api/v1/items/active` - Get active items (paginated)
- `GET /api/v1/items/search` - Search items with filters
- `POST /api/v1/items` - Create new item
- `PUT /api/v1/items/{id}` - Update item
- `DELETE /api/v1/items/{id}` - Delete item

### Bidding
- `POST /api/v1/bids` - Place bid
- `GET /api/v1/bids/item/{itemId}` - Get item bids
- `GET /api/v1/bids/user` - Get user's bids

### Virtual Currency
- `GET /api/v1/virtual-currency/balance` - Get user balance
- `GET /api/v1/virtual-currency/history` - Transaction history
- `POST /api/v1/virtual-currency/transfer` - Transfer VC

### Feedback
- `POST /api/v1/feedback` - Submit feedback
- `GET /api/v1/feedback/user/{userId}` - Get user feedback

## Frontend Features

### Dashboard
- **Marketplace Grid**: Responsive item cards with pagination
- **Search Bar**: Real-time search with keyword filtering
- **Category Filter**: Dropdown filter for item categories
- **Load More**: Progressive loading for better performance

### Item Management
- **Item Creation**: Multi-step form with image upload
- **Image Management**: Multiple image upload with drag-and-drop
- **Auction Settings**: Flexible auction timing and buyout options
- **Status Management**: Active/Inactive item toggling

### Bidding Interface
- **Real-time Updates**: Live bid updates without page refresh
- **Bid Validation**: Client-side validation before submission
- **Bid History**: Complete bidding timeline
- **Auto-refresh**: Automatic updates during active auctions

### Analytics Dashboard
- **Performance Metrics**: Category-wise performance charts
- **Transaction Graphs**: Visual transaction history
- **User Statistics**: Comprehensive user activity metrics
- **Export Options**: Data export functionality

## Database Performance

### Query Optimizations
- **JOIN FETCH**: Prevents N+1 query problems
- **Strategic Indexing**: Optimized indexes for common queries
- **Connection Pooling**: HikariCP with optimized settings
- **Query Monitoring**: Custom performance interceptors

### Performance Features
```java
// Example: Optimized item fetching with images
@Query("SELECT i FROM Item i LEFT JOIN FETCH i.images WHERE i.status = :status AND i.isActive = true")
List<Item> findActiveItemsWithImages(@Param("status") ItemStatus status);
```

### Caching Strategy
- HTTP caching headers for static content
- Query result caching for frequently accessed data
- Connection pool optimization for high concurrency

## Security Features

### Authentication & Authorization
- **JWT Tokens**: Secure stateless authentication
- **Password Encryption**: BCrypt hashing
- **Role-based Access**: User and admin roles
- **Token Expiration**: Configurable token lifetime

### Data Protection
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Input sanitization
- **CORS Configuration**: Secure cross-origin requests
- **Soft Deletes**: Data preservation with logical deletion

### Business Logic Security
- **Balance Validation**: Prevents negative balances
- **Bid Validation**: Ensures valid bidding rules
- **Transaction Integrity**: Atomic transaction processing
- **Auction Security**: Prevents manipulation of ended auctions

## Testing

### Testing Strategy
- **Unit Tests**: Service layer testing with JUnit 5
- **Integration Tests**: Repository and controller testing
- **Security Tests**: Authentication and authorization testing
- **Performance Tests**: Load testing for critical endpoints

### Test Coverage Areas
- Authentication flows
- Bidding system edge cases
- Virtual currency transactions
- Auction processing
- Data validation
- Security vulnerabilities

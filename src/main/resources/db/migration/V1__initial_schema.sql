-- ===========================
-- ENUM TYPES
-- ===========================
DO $$ BEGIN
    CREATE TYPE item_category AS ENUM ('BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE item_condition AS ENUM ('NEW', 'LIKE_NEW', 'USED', 'VERY_USED', 'DAMAGED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'DISPUTED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ===========================
-- DROP TABLES (in reverse order)
-- ===========================
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS bid;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS virtual_currency;
DROP TABLE IF EXISTS users;

-- ===========================
-- USERS
-- ===========================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    student_id VARCHAR(50) UNIQUE,
    reputation INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_student_id ON users(student_id);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);

-- ===========================
-- VIRTUAL CURRENCY
-- ===========================
CREATE TABLE virtual_currency (
    virtual_currency_id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    balance DECIMAL(10, 2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_virtual_currency_updated_at ON virtual_currency(updated_at);

-- ===========================
-- ITEM
-- ===========================
CREATE TABLE item (
    item_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category item_category,
    starting_price DECIMAL(10, 2) NOT NULL,
    current_price DECIMAL(10, 2),
    image_url TEXT,
    condition item_condition,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_item_user_id ON item(user_id);
CREATE INDEX idx_item_category ON item(category);
CREATE INDEX idx_item_is_active ON item(is_active);
CREATE INDEX idx_item_is_deleted ON item(is_deleted);

-- ===========================
-- BID
-- ===========================
CREATE TABLE bid (
    bid_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    item_id INTEGER NOT NULL REFERENCES item(item_id) ON DELETE CASCADE,
    bid_amount DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bid_item_id ON bid(item_id);
CREATE INDEX idx_bid_user_id ON bid(user_id);

-- ===========================
-- TRANSACTION
-- ===========================
CREATE TABLE transaction (
    transaction_id SERIAL PRIMARY KEY,
    buyer_id INTEGER NOT NULL REFERENCES users(user_id),
    seller_id INTEGER NOT NULL REFERENCES users(user_id),
    item_id INTEGER UNIQUE NOT NULL REFERENCES item(item_id),
    price DECIMAL(10, 2) NOT NULL,
    status transaction_status DEFAULT 'PENDING',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_buyer_id ON transaction(buyer_id);
CREATE INDEX idx_transaction_seller_id ON transaction(seller_id);
CREATE INDEX idx_transaction_status ON transaction(status);

-- ===========================
-- MESSAGE
-- ===========================
CREATE TABLE message (
    message_id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL REFERENCES users(user_id),
    receiver_id INTEGER NOT NULL REFERENCES users(user_id),
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_message_sender_id ON message(sender_id);
CREATE INDEX idx_message_receiver_id ON message(receiver_id);
CREATE INDEX idx_message_is_read ON message(is_read);

-- ===========================
-- FEEDBACK
-- ===========================
CREATE TABLE feedback (
    feedback_id SERIAL PRIMARY KEY,
    giver_id INTEGER NOT NULL REFERENCES users(user_id),
    receiver_id INTEGER NOT NULL REFERENCES users(user_id),
    transaction_id INTEGER NOT NULL REFERENCES transaction(transaction_id),
    score INTEGER NOT NULL CHECK (score IN (-1, 0, 1)), -- FeedbackScore enum-mapped value
    comment TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedback_receiver_id ON feedback(receiver_id);
CREATE INDEX idx_feedback_transaction_id ON feedback(transaction_id);

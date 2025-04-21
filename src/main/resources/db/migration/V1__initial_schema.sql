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
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
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
    category VARCHAR(20) CHECK (category IN ('BOOKS', 'ELECTRONICS', 'CLOTHES', 'FURNITURE', 'STATIONERY', 'SPORTS', 'OTHERS')),
    starting_price DECIMAL(10, 2) NOT NULL,
    current_price DECIMAL(10, 2),
    condition VARCHAR(20) CHECK (condition IN ('NEW', 'LIKE_NEW', 'USED', 'VERY_USED', 'DAMAGED')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PENDING', 'SOLD', 'CANCELLED')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_item_user_id ON item(user_id);
CREATE INDEX idx_item_category ON item(category);
CREATE INDEX idx_item_is_active ON item(is_active);
CREATE INDEX idx_item_is_deleted ON item(is_deleted);
CREATE INDEX idx_item_status ON item(status);

-- ===========================
-- ITEM IMAGES
-- ===========================
CREATE TABLE item_images (
    image_id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL REFERENCES item(item_id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_item_images_item_id ON item_images(item_id);
CREATE INDEX idx_item_images_is_primary ON item_images(is_primary);

-- ===========================
-- BID
-- ===========================
CREATE TABLE bid (
    bid_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    item_id INTEGER NOT NULL REFERENCES item(item_id) ON DELETE CASCADE,
    bid_amount DECIMAL(10, 2) NOT NULL CHECK (bid_amount > 0),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bid_item_id ON bid(item_id);
CREATE INDEX idx_bid_user_id ON bid(user_id);

-- ===========================
-- TRANSACTION
-- ===========================
CREATE TABLE "transaction" (
    transaction_id SERIAL PRIMARY KEY,
    buyer_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    seller_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    item_id INTEGER UNIQUE NOT NULL REFERENCES item(item_id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'DISPUTED')),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_buyer_id ON "transaction"(buyer_id);
CREATE INDEX idx_transaction_seller_id ON "transaction"(seller_id);
CREATE INDEX idx_transaction_status ON "transaction"(status);

-- ===========================
-- MESSAGE
-- ===========================
CREATE TABLE message (
    message_id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    receiver_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_message_sender_id ON message(sender_id);
CREATE INDEX idx_message_receiver_id ON message(receiver_id);
CREATE INDEX idx_message_is_read ON message(is_read);

-- ===========================
-- FEEDBACK
-- ===========================
CREATE TABLE feedback (
    feedback_id SERIAL PRIMARY KEY,
    giver_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    receiver_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    transaction_id INTEGER UNIQUE NOT NULL REFERENCES "transaction"(transaction_id) ON DELETE CASCADE,
    score INTEGER NOT NULL CHECK (score IN (-1, 0, 1)),  -- FeedbackScore mapped value
    comment TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedback_receiver_id ON feedback(receiver_id);
CREATE INDEX idx_feedback_transaction_id ON feedback(transaction_id);

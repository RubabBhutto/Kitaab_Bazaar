-- ============================================================
--  KITAAB BAZAR — MySQL Database (FIXED VERSION)
--  Run this file in MySQL Workbench or phpMyAdmin
--  Database name: kitaabbazaar
-- ============================================================

-- Step 1: Create and select the database
DROP DATABASE IF EXISTS kitaabbazaar;
CREATE DATABASE kitaabbazaar;
USE kitaabbazaar;

-- ============================================================
-- TABLE 1: users
-- ============================================================
CREATE TABLE users (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    phone    VARCHAR(20)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(10)  NOT NULL    -- 'buyer', 'seller', or 'admin'
);

-- ============================================================
-- TABLE 2: categories
-- ============================================================
CREATE TABLE categories (
    id   INT         AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- ============================================================
-- TABLE 3: books
-- NOTE: column is named condition_ because 'condition'
--       is a reserved word in MySQL
-- ============================================================
CREATE TABLE books (
    id          INT            AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(150)   NOT NULL,
    grade       INT            DEFAULT 0,
    publisher   VARCHAR(100)   DEFAULT 'N/A',
    price       DECIMAL(10,2)  NOT NULL,
    condition_  VARCHAR(10)    NOT NULL,
    status      VARCHAR(15)    DEFAULT 'available',
    seller_id   INT            NOT NULL,
    category_id INT            NOT NULL,
    FOREIGN KEY (seller_id)   REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 4: orders
-- ============================================================
CREATE TABLE orders (
    id        INT         AUTO_INCREMENT PRIMARY KEY,
    buyer_id  INT         NOT NULL,
    book_id   INT         NOT NULL,
    status    VARCHAR(20) DEFAULT 'pending',
    placed_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (book_id)  REFERENCES books(id)  ON DELETE CASCADE
);

-- ============================================================
-- TABLE 5: messages
-- ============================================================
CREATE TABLE messages (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    sender_id   INT          NOT NULL,
    receiver_id INT          NOT NULL,
    message     TEXT         NOT NULL,
    sent_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id)   REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 6: reviews
-- ============================================================
CREATE TABLE reviews (
    id          INT       AUTO_INCREMENT PRIMARY KEY,
    buyer_id    INT       NOT NULL,
    seller_id   INT       NOT NULL,
    rating      INT       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id)  REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE 7: payments
-- FIXED: columns match OrderService.java exactly
--   (total_amount, commission, seller_amount, status)
-- ============================================================
CREATE TABLE payments (
    id            INT           AUTO_INCREMENT PRIMARY KEY,
    order_id      INT           NOT NULL,
    total_amount  DECIMAL(10,2) NOT NULL,
    commission    DECIMAL(10,2) NOT NULL,
    seller_amount DECIMAL(10,2) NOT NULL,
    status        VARCHAR(20)   DEFAULT 'paid',
    paid_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Users
-- Admin  login: 03000000000 / admin123
-- Seller login: 03001111111 / seller123
-- Buyer  login: 03002222222 / buyer123
INSERT INTO users (name, phone, password, role) VALUES
('Admin',        '03000000000', 'admin123',  'admin'),
('Ali Seller',   '03001111111', 'seller123', 'seller'),
('Sara Buyer',   '03002222222', 'buyer123',  'buyer'),
('Ahmed Seller', '03003333333', 'seller456', 'seller'),
('Zara Buyer',   '03004444444', 'buyer456',  'buyer');

-- Categories
INSERT INTO categories (name) VALUES
('School Books'),
('Novels'),
('Islamic Books'),
('Science & Tech'),
('Urdu Literature'),
('English Literature');

-- Sample Books
INSERT INTO books (title, grade, publisher, price, condition_, status, seller_id, category_id) VALUES
('Mathematics Grade 9',   9,  'Punjab Textbook Board', 250.00, 'good', 'available', 2, 1),
('Physics Grade 10',      10, 'Punjab Textbook Board', 300.00, 'new',  'available', 2, 1),
('English Grammar',       8,  'Oxford',                180.00, 'fair', 'available', 2, 1),
('Alchemist Novel',       0,  'Harper Collins',        450.00, 'good', 'available', 4, 2),
('Quran Translation',     0,  'Maktaba',               350.00, 'new',  'available', 4, 3),
('Biology Grade 11',      11, 'Punjab Textbook Board', 280.00, 'good', 'available', 2, 1),
('Chemistry Grade 12',    12, 'Punjab Textbook Board', 320.00, 'new',  'available', 4, 1),
('Urdu Adab',             0,  'Sang-e-Meel',           200.00, 'fair', 'available', 2, 5);

-- Sample Orders
INSERT INTO orders (buyer_id, book_id, status) VALUES
(3, 1, 'pending'),
(5, 4, 'pending');

-- Sample Messages
INSERT INTO messages (sender_id, receiver_id, message) VALUES
(3, 2, 'Assalam o Alaikum! Is the Mathematics book still available?'),
(2, 3, 'Walaikum Assalam! Yes it is available. Please place the order.');

-- Sample Review
INSERT INTO reviews (buyer_id, seller_id, rating, comment) VALUES
(3, 2, 5, 'Great seller! Book was exactly as described. Fast response.');

-- ============================================================
-- DONE! Database is ready.
-- Open MySQL Workbench -> File -> Run SQL Script -> select this file
-- OR paste into MySQL query window and click Execute (lightning bolt)
-- ============================================================

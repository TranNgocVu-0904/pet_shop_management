DROP DATABASE IF EXISTS petshop_db;
CREATE DATABASE petshop_db;
USE petshop_db;

-- === STAFF & MANAGER USERS ===
CREATE TABLE staff (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash CHAR(60) NOT NULL
);

CREATE TABLE managers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash CHAR(60) NOT NULL
);

-- === CUSTOMERS ===
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    loyalty_points INT DEFAULT 0
);

-- === PETS ===
CREATE TABLE pets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('DOG', 'CAT') NOT NULL,
    breed VARCHAR(100),
    age INT,
    price DECIMAL(10, 2),
    status TINYINT DEFAULT 1 -- 1 = available, 0 = sold
);

-- === PRODUCTS ===
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    type ENUM('FOOD', 'MEDICINE', 'TOY') NOT NULL,
    
    -- type-specific columns
    material VARCHAR(100),
    expiration_date DATE,
    nutritional_info TEXT,
    manufacture_date DATE,
    dosage VARCHAR(100),

    status TINYINT DEFAULT 1 -- 1 = available, 0 = out of stock
);

-- === BILLS ===
CREATE TABLE bills (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    staff_id INT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    transaction_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- === BILL ITEMS ===
CREATE TABLE bill_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    item_type ENUM('PRODUCT', 'PET') NOT NULL,
    pet_id INT,
    product_id INT,
    quantity INT DEFAULT 1,
    unit_price DECIMAL(10, 2),

    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

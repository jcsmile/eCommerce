-- Flyway Migration Script: V1__create_products_table.sql
-- This script creates the "products" table for the Product Service.

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER DEFAULT 0,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional index for faster searches by category
CREATE INDEX IF NOT EXISTS idx_products_category ON products (category);

-- Optional index for name search
CREATE INDEX IF NOT EXISTS idx_products_name ON products (name);

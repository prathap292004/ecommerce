INSERT INTO users (full_name, email, password_hash) VALUES
('Prathap Kumar', 'prathap292004@gmail.com', 'demo_hash_123'),
('Sangeetha R', 'sangeetha.demo@example.com', 'demo_hash_456')
ON CONFLICT (email) DO NOTHING;

INSERT INTO products (name, category, price, stock_qty, description) VALUES
('Urban Runner Sneakers', 'Footwear', 2499.00, 15, 'Lightweight breathable daily running shoes'),
('Classic Leather Loafers', 'Footwear', 1899.00, 10, 'Smart casual slip-on shoes for everyday wear'),
('Everyday Canvas Backpack', 'Accessories', 1299.00, 20, 'Water-resistant commuter backpack with laptop sleeve'),
('Minimalist Analog Watch', 'Accessories', 1599.00, 12, 'Clean dial watch with durable strap');
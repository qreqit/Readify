-- Insert user
INSERT INTO users (id, first_name, last_name, email, password, shipping_address)
VALUES (1, 'John', 'Doe', 'john.doe@example.com', 'password123', '123 Main St, Springfield');

-- Insert shopping cart
INSERT INTO shopping_carts (id, user_id) VALUES (1, 1);

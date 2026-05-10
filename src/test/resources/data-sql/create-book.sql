INSERT INTO books (id, title, author, isbn, price, description, cover_image)
VALUES (1, 'wallet', 'John', '9283234577892', 60.99, 'Updated description', 'https://example.com/updated-cover-.jpg');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);

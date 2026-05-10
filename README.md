# 🎯 Project  Overview
This application provides functionalities for managing users, books, categories, orders, and shopping carts. The system ensures secure access with role-based authorization (USER and ADMIN). The app is designed for book sales, allowing users to:

Create accounts and log in.
Purchase books conveniently and quickly from home.
Track the status of orders and know when they arrive at the post office.
This is my first large-scale application, and I faced numerous challenges during its development. Each part was difficult but immensely educational, and I gained a lot of knowledge that will be invaluable for my future projects.

# 🚀 Technologies Used
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- Swagger
- Liquibase
- MapStruct
- Lombok
- Hibernate Validator
- MySQL

# 📖 API Endpoints
1. AuthenticationController
- ```POST /auth/registration``` – Register a new user.
- ```POST /auth/login``` – User authentication (login).
2. BookController
- ```GET /books``` – Retrieve all books with pagination (ADMIN access only).
- ```GET /books/{id}``` – Retrieve a book by its ID.
- ```POST /books``` – Create a new book (ADMIN access only).
- ```PUT /books/{id}``` – Update a book by its ID (ADMIN access only).
- ```DELETE /books/{id}``` – Delete a book by its ID (ADMIN access only).
- ```GET /books/search``` – Search books by parameters.
3. CategoryController
- ```POST /categories``` – Create a new category (ADMIN access only).
- ```GET /categories``` – Retrieve all categories.
- ```GET /categories/{id}``` – Retrieve a category by its ID.
- ```PUT /categories/{id}``` – Update a category by its ID (ADMIN access only).
- ```DELETE /categories/{id}``` – Delete a category by its ID (ADMIN access only).
- ```GET /categories/{id}/books``` – Retrieve all books in a category by its ID.
4. OrderController
- ```POST /orders``` – Create a new order (USER access only).
- ```GET /orders``` – Retrieve all orders of the logged-in user (USER access only).
- ```PATCH /orders/{id}``` – Update the status of an order (ADMIN access only).
- ```GET /orders/{orderId}/items``` – Retrieve all items from a specific order (USER access only).
- ```GET /orders/{orderId}/items/{itemId}``` – Retrieve a specific item from an order (USER access only).
5. ShoppingCartController
- ```GET /cart``` – Retrieve the current user's shopping cart.
- ```POST /cart``` – Add a book to the shopping cart.
- ```PUT /cart/items/{cartItemId}``` – Update the quantity of items in the cart.
- ```DELETE /cart/items/{cartItemId}``` – Remove an item from the cart.

# Models and relations

![Book-Store](./Book-Store.png)

# 📚 Getting Started
I have always loved books, especially those that help understand the meaning of human existence and provide fuel for thought. Inspired by this passion, I decided to create my own application, where users can purchase books from the comfort of their homes.

#  📹Video Overview of Program Functionality
You can also find a video of the program at this link: https://www.loom.com/share/0c519d24efc04b64acf9a3e9096b40cb?sid=e3750a8b-c044-4b13-b382-535497228e21

# Contacts
- Email: greqit.work@gmail.com
- [LinkedIn](https://www.linkedin.com/in/ivan-prystaia-7099a22b1/)

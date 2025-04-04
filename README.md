# MSA Project

## Project Overview

The MSA (Microservices Architecture) system is designed to manage all aspects of customer interactions, employee actions, inventory, discounts, orders, and customer support. The system is divided into two primary user groups: **Employees** and **Customers**, with distinct functionalities for each group to ensure smooth and efficient operations.

---

## Requirements

### For Staff

#### 1. Login
- **Description**: Staff can log in using the provided credentials.
- **Priority**: High
- **Acceptance Criteria**:
  - Staff can securely log in using their unique username and password.
  - Failed login attempts are tracked and logged.

#### 2. Inventory Management
- **Description**: Manage products in the inventory (add, update, and delete products).
- **Priority**: High
- **Acceptance Criteria**:
  - Staff can add new products to the inventory with details like name, description, price, quantity, etc.
  - Staff can update the product details such as pricing, description, and stock levels.
  - Staff can delete products that are no longer sold or are out of stock.
  - Stock levels are updated automatically when products are added or removed.

#### 3. Discount Code Management
- **Description**: Create, update, and delete discount codes.
- **Priority**: High
- **Acceptance Criteria**:
  - Staff can create new discount codes with specified conditions (e.g., minimum order amount, percentage discount, expiry date).
  - Staff can modify the conditions for existing discount codes (applicable dates, requirements).
  - Staff can delete discount codes that are no longer required.

#### 4. Update Discount Conditions
- **Description**: Modify conditions under which discount codes apply.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Staff can update conditions like minimum order amounts, required quantities of items, and other restrictions associated with discount codes.
  - Changes are reflected immediately in the system for customer use.

#### 5. Customer Management
- **Description**: View, search, and delete customer information.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Staff can search for customers by criteria such as name, email, or phone number.
  - Staff can view detailed customer profiles (order history, contact details, preferences).
  - Staff can delete customers from the system when necessary.

#### 6. Order Management
- **Description**: Search, view, modify, and delete orders.
- **Priority**: High
- **Acceptance Criteria**:
  - Staff can search for orders using order IDs, customer names, or product names.
  - Staff can view the status of each order (payment status, shipping status, and delivery updates).
  - Staff can modify order details (e.g., change shipping address or product quantities).
  - Staff can delete orders (e.g., canceled or erroneous orders).

---

### For Customers

#### 1. Registration and Login
- **Description**: Allow users to register a new account or log in to an existing account.
- **Priority**: High
- **Acceptance Criteria**:
  - Customers can register by providing necessary details (name, email, password).
  - Customers can log in to their accounts using email and password.
  - Forgot password functionality to reset their password via email.
  - Customers can securely manage their login credentials.

#### 2. Personal Information Management
- **Description**: Update personal details like name, shipping address, phone number, and payment methods.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Customers can update their contact information (name, email, phone number).
  - Customers can add/edit shipping addresses and set a default address.
  - Customers can add, update, or remove payment methods linked to their account.

#### 3. Product Search
- **Description**: Search products by name, category, or price.
- **Priority**: High
- **Acceptance Criteria**:
  - Customers can search for products using various filters (product name, category, price range).
  - Search results display product names, images, and basic details.

#### 4. Cart Management
- **Description**: Add products to the cart, update quantities, and proceed to checkout.
- **Priority**: High
- **Acceptance Criteria**:
  - Customers can add products to their cart with specific quantities.
  - Customers can edit product quantities or remove items from their cart.
  - The cart is saved and accessible for future purchases.

#### 5. Discount Code Usage
- **Description**: Apply discount codes during checkout and verify their eligibility.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Customers can enter discount codes during checkout.
  - The system verifies the validity of the discount code based on conditions like order amount, expiration date, and product restrictions.
  - The discount is applied, and the new total price is displayed before payment.

#### 6. Placing Orders
- **Description**: Finalize purchases by selecting products from the cart and providing payment and shipping information.
- **Priority**: High
- **Acceptance Criteria**:
  - Customers can review order details before placing an order.
  - Customers select a payment method (e.g., credit card, PayPal, cash on delivery).
  - Customers provide shipping information (delivery address and preferred shipping method).
  - The system processes the order and confirms the purchase.

#### 7. Support Requests
- **Description**: Customers can send support requests to the customer service team.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Customers can create support tickets for issues related to orders, products, or accounts.
  - The system tracks the status of support tickets (open, resolved, closed).

#### 8. Payment
- **Description**: Complete transactions using third-party payment methods or cash on delivery.
- **Priority**: High
- **Acceptance Criteria**:
  - Customers can complete payments using integrated payment methods (credit/debit cards, e-wallets).
  - The system confirms payment and updates the order status accordingly.
  - Cash on delivery is supported as a payment option.

#### 9. Product Ratings and Reviews
- **Description**: Customers can rate and review products after completing an order.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Customers can provide ratings (e.g., stars) and write reviews for products they have purchased.
  - The system displays ratings and reviews for products to help future customers make informed decisions.

#### 10. Shipping Method Selection
- **Description**: Choose a preferred shipping method based on cost and delivery time.
- **Priority**: Medium
- **Acceptance Criteria**:
  - Customers can select a shipping method (standard, express, or pickup).
  - Shipping cost and estimated delivery time are displayed before checkout.

---

## ✨ Future Enhancements
- Improve AI model with deep learning.
- Implement real-time user behavior tracking.
- Enhance dashboard UI/UX.

## Features
- Secure and transparent point transactions using **Hyperledger Fabric**.
- Earn points through purchases and in-game activities.
- Redeem points for rewards.
- Track transaction history with blockchain immutability.
- Admin panel for managing users and points via **phpMyAdmin**.

---

## Setup and Installation

1. **Clone the repository**
    ```bash
    git clone https://github.com/your-repository/msa-project.git
    ```

2. **Install dependencies**
    Follow the setup instructions specific to the services in the `msa-project` directory.

3. **Run the application**
    Start the necessary services for **staff** and **customer** functionalities.

## Running with Docker Compose

This project includes Docker Compose configuration to run the application with Redis and MySQL.

### Prerequisites

- Docker and Docker Compose installed on your machine
- Git (to clone the repository)

### Steps to Run

1. Clone the repository:
   ```
   git clone <repository-url>
   cd MSA_EV
   ```

2. Build and start the containers:
   ```
   docker-compose up -d
   ```

3. Check the status of the containers:
   ```
   docker-compose ps
   ```

4. View logs of the application:
   ```
   docker-compose logs -f app
   ```

5. Stop the containers:
   ```
   docker-compose down
   ```

### Services

- **app**: Spring Boot application running on port 1081
- **redis**: Redis server running on port 6379
- **db**: MySQL database running on port 3306

### Environment Variables

The following environment variables are set in the docker-compose.yml file:

- `SPRING_REDIS_HOST`: Redis host (default: redis)
- `SPRING_REDIS_PORT`: Redis port (default: 6379)
- `SPRING_DATASOURCE_URL`: MySQL connection URL
- `SPRING_DATASOURCE_USERNAME`: MySQL username (default: root)
- `SPRING_DATASOURCE_PASSWORD`: MySQL password (default: password)

### Volumes

- `redis-data`: Persistent storage for Redis data
- `mysql-data`: Persistent storage for MySQL data

---

## Contributing
We welcome contributions! If you'd like to contribute, please fork the repository, make changes, and submit a pull request.

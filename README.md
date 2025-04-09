# MSA Project

## Project Overview

The MSA (Microservices Architecture) system is designed to manage all aspects of a multi-branch retail business. The system implements a role-based access control system with three main user roles: **Admin**, **Manager**, and **Customer**. Each role has specific permissions and responsibilities to ensure efficient business operations.

---

## Role-Based Access Control

### Admin Role
- **Description**: Full system access with complete control over all branches and operations
- **Key Responsibilities**:
  - Manage all branches across the system
  - Create and assign managers to branches
  - Monitor overall business performance
  - Access and manage all inventory across branches
  - View and manage all orders across branches
  - Configure system-wide settings and policies

### Manager Role
- **Description**: Branch-specific management with control over assigned branch operations
- **Key Responsibilities**:
  - Manage assigned branch inventory
  - Monitor branch-specific orders
  - Update branch information
  - Manage branch staff
  - View branch-specific reports and analytics
  - Handle branch-specific customer support

### Customer Role
- **Description**: Regular user with access to shopping features for a specific branch
- **Key Responsibilities**:
  - Browse and purchase products from assigned branch
  - Manage personal information
  - View order history
  - Apply discount codes
  - Track order status
  - Provide product reviews and ratings

---

## Core Features

### Branch Management
- **Admin Features**:
  - Create and configure new branches
  - Assign managers to branches
  - Monitor branch performance
  - Manage branch inventory allocation
- **Manager Features**:
  - Update branch information
  - Manage branch inventory
  - View branch-specific reports

### Inventory Management
- **Admin Features**:
  - Global inventory overview
  - Manage product catalog
  - Configure inventory policies
- **Manager Features**:
  - Manage branch-specific inventory
  - Update product quantities
  - Handle low stock alerts
  - Process inventory adjustments

### Order Management
- **Admin Features**:
  - View all orders across branches
  - Generate system-wide reports
  - Monitor order trends
- **Manager Features**:
  - Process branch-specific orders
  - Update order status
  - Handle order cancellations
  - Generate branch-specific reports
- **Customer Features**:
  - Place orders from assigned branch
  - Track order status
  - View order history
  - Apply discount codes

### User Management
- **Admin Features**:
  - Create and manage manager accounts
  - Assign roles and permissions
  - Monitor user activities
- **Manager Features**:
  - View customer information
  - Handle customer support
  - Process customer requests
- **Customer Features**:
  - Manage personal information
  - Update shipping addresses
  - View purchase history

---

## Technical Features
- Secure authentication and authorization using JWT
- Redis caching for improved performance
- MySQL database for data persistence
- Docker containerization for easy deployment
- RESTful API architecture
- Role-based access control (RBAC)

---

## Setup and Installation

1. **Clone the repository**
    ```bash
    git clone https://github.com/your-repository/msa-project.git
    ```

2. **Install dependencies**
    Follow the setup instructions specific to the services in the `msa-project` directory.

3. **Run the application**
    Start the necessary services using Docker Compose.

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

# MSA Market API Documentation

## Table of Contents
1. [User Management](#user-management)
2. [Branch Management](#branch-management)
3. [Product Management](#product-management)
4. [Inventory Management](#inventory-management)
5. [Cart Management](#cart-management)
6. [Order Management](#order-management)
7. [Promo Code Management](#promo-code-management)

## User Management

### Register User
- **URL**: `/user/register`
- **Method**: `POST`
- **Description**: Register a new user
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string",
    "email": "string",
    "fullName": "string",
    "phoneNumber": "string",
    "role": "CUSTOMER"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "result": {
      "userId": 0,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "phoneNumber": "string",
      "role": "CUSTOMER"
    }
  }
  ```

### Get User by ID
- **URL**: `/user/{userId}`
- **Method**: `GET`
- **Description**: Get user details by ID
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "userId": 0,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "phoneNumber": "string",
      "role": "CUSTOMER"
    }
  }
  ```

### Update User
- **URL**: `/user/{userId}`
- **Method**: `PUT`
- **Description**: Update user details
- **Request Body**:
  ```json
  {
    "email": "string",
    "fullName": "string",
    "phoneNumber": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "userId": 0,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "phoneNumber": "string",
      "role": "CUSTOMER"
    }
  }
  ```

### Delete User
- **URL**: `/user/{userId}`
- **Method**: `DELETE`
- **Description**: Delete a user
- **Response**: `204 No Content`

### Get All Users
- **URL**: `/user`
- **Method**: `GET`
- **Description**: Get all users with pagination
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "userId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "userId": 0,
          "username": "string",
          "email": "string",
          "fullName": "string",
          "phoneNumber": "string",
          "role": "CUSTOMER"
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

## Branch Management

### Create Branch
- **URL**: `/branch`
- **Method**: `POST`
- **Description**: Create a new branch
- **Request Body**:
  ```json
  {
    "branchName": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "managerId": 0
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "result": {
      "branchId": 0,
      "branchName": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "manager": {
        "userId": 0,
        "username": "string",
        "email": "string",
        "fullName": "string",
        "phoneNumber": "string",
        "role": "BRANCH_MANAGER"
      }
    }
  }
  ```

### Get Branch by ID
- **URL**: `/branch/{branchId}`
- **Method**: `GET`
- **Description**: Get branch details by ID
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "branchId": 0,
      "branchName": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "manager": {
        "userId": 0,
        "username": "string",
        "email": "string",
        "fullName": "string",
        "phoneNumber": "string",
        "role": "BRANCH_MANAGER"
      }
    }
  }
  ```

### Update Branch
- **URL**: `/branch/{branchId}`
- **Method**: `PUT`
- **Description**: Update branch details
- **Request Body**:
  ```json
  {
    "branchName": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "managerId": 0
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "branchId": 0,
      "branchName": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "manager": {
        "userId": 0,
        "username": "string",
        "email": "string",
        "fullName": "string",
        "phoneNumber": "string",
        "role": "BRANCH_MANAGER"
      }
    }
  }
  ```

### Delete Branch
- **URL**: `/branch/{branchId}`
- **Method**: `DELETE`
- **Description**: Delete a branch
- **Response**: `204 No Content`

### Get All Branches
- **URL**: `/branch`
- **Method**: `GET`
- **Description**: Get all branches with pagination
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "branchId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "branchId": 0,
          "branchName": "string",
          "address": "string",
          "phoneNumber": "string",
          "email": "string",
          "manager": {
            "userId": 0,
            "username": "string",
            "email": "string",
            "fullName": "string",
            "phoneNumber": "string",
            "role": "BRANCH_MANAGER"
          }
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

### Get Branches by Manager Role
- **URL**: `/branch/manager`
- **Method**: `GET`
- **Description**: Get all branches managed by users with BRANCH_MANAGER role
- **Response**: `200 OK`
  ```json
  {
    "result": [
      {
        "branchId": 0,
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string",
        "email": "string",
        "manager": {
          "userId": 0,
          "username": "string",
          "email": "string",
          "fullName": "string",
          "phoneNumber": "string",
          "role": "BRANCH_MANAGER"
        }
      }
    ]
  }
  ```

## Product Management

### Create Product
- **URL**: `/product`
- **Method**: `POST`
- **Description**: Create a new product
- **Request Body**:
  ```json
  {
    "productName": "string",
    "description": "string",
    "price": 0,
    "categoryId": 0,
    "manufacturerId": 0,
    "color": "string",
    "size": "string"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "result": {
      "productId": 0,
      "productName": "string",
      "description": "string",
      "price": 0,
      "category": {
        "categoryId": 0,
        "categoryName": "string"
      },
      "manufacturer": {
        "manufacturerId": 0,
        "manufacturerName": "string"
      },
      "color": "string",
      "size": "string",
      "totalRevenue": 0,
      "totalStock": 0
    }
  }
  ```

### Get Product by ID
- **URL**: `/product/{productId}`
- **Method**: `GET`
- **Description**: Get product details by ID
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "productId": 0,
      "productName": "string",
      "description": "string",
      "price": 0,
      "category": {
        "categoryId": 0,
        "categoryName": "string"
      },
      "manufacturer": {
        "manufacturerId": 0,
        "manufacturerName": "string"
      },
      "color": "string",
      "size": "string",
      "totalRevenue": 0,
      "totalStock": 0
    }
  }
  ```

### Update Product
- **URL**: `/product/{productId}`
- **Method**: `PUT`
- **Description**: Update product details
- **Request Body**:
  ```json
  {
    "productName": "string",
    "description": "string",
    "price": 0,
    "categoryId": 0,
    "manufacturerId": 0,
    "color": "string",
    "size": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "productId": 0,
      "productName": "string",
      "description": "string",
      "price": 0,
      "category": {
        "categoryId": 0,
        "categoryName": "string"
      },
      "manufacturer": {
        "manufacturerId": 0,
        "manufacturerName": "string"
      },
      "color": "string",
      "size": "string",
      "totalRevenue": 0,
      "totalStock": 0
    }
  }
  ```

### Delete Product
- **URL**: `/product/{productId}`
- **Method**: `DELETE`
- **Description**: Delete a product
- **Response**: `204 No Content`

### Get All Products
- **URL**: `/product`
- **Method**: `GET`
- **Description**: Get all products with pagination and filtering
- **Query Parameters**:
  - `keyword` (optional): Search by product name
  - `minPrice` (optional): Minimum price filter
  - `maxPrice` (optional): Maximum price filter
  - `categoryId` (optional): Filter by category
  - `manufacturerId` (optional): Filter by manufacturer
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "productId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "productId": 0,
          "productName": "string",
          "description": "string",
          "price": 0,
          "category": {
            "categoryId": 0,
            "categoryName": "string"
          },
          "manufacturer": {
            "manufacturerId": 0,
            "manufacturerName": "string"
          },
          "color": "string",
          "size": "string",
          "totalRevenue": 0,
          "totalStock": 0
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

### Get Available Products by Branch
- **URL**: `/product/branch/{branchId}`
- **Method**: `GET`
- **Description**: Get available products in a specific branch with filtering
- **Query Parameters**:
  - `keyword` (optional): Search by product name
  - `minPrice` (optional): Minimum price filter
  - `maxPrice` (optional): Maximum price filter
  - `categoryId` (optional): Filter by category
  - `manufacturerId` (optional): Filter by manufacturer
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "productId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": [
      {
        "productId": 0,
        "productName": "string",
        "description": "string",
        "price": 0,
        "category": {
          "categoryId": 0,
          "categoryName": "string"
        },
        "manufacturer": {
          "manufacturerId": 0,
          "manufacturerName": "string"
        },
        "color": "string",
        "size": "string",
        "totalRevenue": 0,
        "totalStock": 0
      }
    ]
  }
  ```

## Inventory Management

### Create Inventory
- **URL**: `/inventory`
- **Method**: `POST`
- **Description**: Create a new inventory
- **Request Body**:
  ```json
  {
    "branchId": 0,
    "inventoryName": "string",
    "description": "string"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "result": {
      "inventoryId": 0,
      "inventoryName": "string",
      "description": "string",
      "branch": {
        "branchId": 0,
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  }
  ```

### Get Inventory by ID
- **URL**: `/inventory/{inventoryId}`
- **Method**: `GET`
- **Description**: Get inventory details by ID
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "inventoryId": 0,
      "inventoryName": "string",
      "description": "string",
      "branch": {
        "branchId": 0,
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  }
  ```

### Update Inventory
- **URL**: `/inventory/{inventoryId}`
- **Method**: `PUT`
- **Description**: Update inventory details
- **Request Body**:
  ```json
  {
    "inventoryName": "string",
    "description": "string"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "inventoryId": 0,
      "inventoryName": "string",
      "description": "string",
      "branch": {
        "branchId": 0,
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  }
  ```

### Delete Inventory
- **URL**: `/inventory/{inventoryId}`
- **Method**: `DELETE`
- **Description**: Delete an inventory
- **Response**: `204 No Content`

### Get All Inventories
- **URL**: `/inventory`
- **Method**: `GET`
- **Description**: Get all inventories with pagination
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "inventoryId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "inventoryId": 0,
          "inventoryName": "string",
          "description": "string",
          "branch": {
            "branchId": 0,
            "branchName": "string",
            "address": "string",
            "phoneNumber": "string"
          }
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

### Get Inventory by Branch ID
- **URL**: `/inventory/branch/{branchId}`
- **Method**: `GET`
- **Description**: Get inventory details for a specific branch
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "inventoryId": 0,
      "inventoryName": "string",
      "description": "string",
      "branch": {
        "branchId": 0,
        "branchName": "string",
        "address": "string",
        "phoneNumber": "string"
      }
    }
  }
  ```

### Add Product to Inventory
- **URL**: `/inventory/{inventoryId}/product`
- **Method**: `POST`
- **Description**: Add a product to an inventory with initial stock
- **Request Body**:
  ```json
  {
    "productId": 0,
    "stockNumber": 0
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "result": {
      "inventoryProductId": 0,
      "inventory": {
        "inventoryId": 0,
        "inventoryName": "string",
        "description": "string"
      },
      "product": {
        "productId": 0,
        "productName": "string",
        "description": "string",
        "price": 0
      },
      "stockNumber": 0
    }
  }
  ```

### Update Product Stock in Inventory
- **URL**: `/inventory/{inventoryId}/product/{productId}`
- **Method**: `PUT`
- **Description**: Update stock number for a product in an inventory
- **Request Body**:
  ```json
  {
    "stockNumber": 0
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "inventoryProductId": 0,
      "inventory": {
        "inventoryId": 0,
        "inventoryName": "string",
        "description": "string"
      },
      "product": {
        "productId": 0,
        "productName": "string",
        "description": "string",
        "price": 0
      },
      "stockNumber": 0
    }
  }
  ```

### Remove Product from Inventory
- **URL**: `/inventory/{inventoryId}/product/{productId}`
- **Method**: `DELETE`
- **Description**: Remove a product from an inventory
- **Response**: `204 No Content`

### Get Products in Inventory
- **URL**: `/inventory/{inventoryId}/products`
- **Method**: `GET`
- **Description**: Get all products in an inventory with pagination and sorting
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "productId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "inventoryProductId": 0,
          "inventory": {
            "inventoryId": 0,
            "inventoryName": "string",
            "description": "string"
          },
          "product": {
            "productId": 0,
            "productName": "string",
            "description": "string",
            "price": 0
          },
          "stockNumber": 0
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

### Search Products in Branch
- **URL**: `/inventory/branch/{branchId}/search`
- **Method**: `GET`
- **Description**: Search products in a branch's inventory with filtering and pagination
- **Query Parameters**:
  - `keyword` (optional): Search by product name
  - `minPrice` (optional): Minimum price filter
  - `maxPrice` (optional): Maximum price filter
  - `color` (optional): Filter by color
  - `size` (optional): Filter by size
  - `page` (default: 0)
  - `size` (default: 10)
  - `sortBy` (default: "productId")
  - `sortDirection` (default: "asc")
- **Response**: `200 OK`
  ```json
  {
    "result": {
      "content": [
        {
          "inventoryProductId": 0,
          "inventory": {
            "inventoryId": 0,
            "inventoryName": "string",
            "description": "string"
          },
          "product": {
            "productId": 0,
            "productName": "string",
            "description": "string",
            "price": 0,
            "category": {
              "categoryId": 0,
              "categoryName": "string"
            },
            "manufacturer": {
              "manufacturerId": 0,
              "manufacturerName": "string"
            },
            "color": "string",
            "size": "string"
          },
          "stockNumber": 0
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "totalElements": 0,
      "totalPages": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": false
    }
  }
  ```

## Cart Management

### Get User Cart
```http
GET /cart
```

**Functionality**: Retrieves the current user's shopping cart.

**Input**: None (uses authentication token)

**Output**:
```json
{
    "result": {
        "cartId": "number",
        "user": {
            "userId": "number",
            "username": "string"
        },
        "cartItems": [
            {
                "cartItemId": "number",
                "product": {
                    "productId": "number",
                    "productName": "string",
                    "price": "number"
                },
                "quantity": "number",
                "selected": "boolean"
            }
        ],
        "totalPrice": "number"
    },
    "success": true,
    "message": "Cart retrieved successfully"
}
```

### Add Item to Cart
```http
POST /cart-item/add
```

**Functionality**: Adds a product to the user's shopping cart.

**Input**:
```json
{
    "productId": "number",
    "quantity": "number"
}
```

**Output**:
```json
{
    "result": {
        "cartItemId": "number",
        "product": {
            "productId": "number",
            "productName": "string",
            "price": "number"
        },
        "quantity": "number",
        "selected": "boolean"
    },
    "success": true,
    "message": "Item added to cart successfully"
}
```

### Update Cart Item
```http
PUT /cart-item/{cartItemId}
```

**Functionality**: Updates the quantity or selection status of a cart item.

**Input**: Path parameter `cartItemId` and request body:
```json
{
    "quantity": "number",
    "selected": "boolean"
}
```

**Output**:
```json
{
    "result": {
        "cartItemId": "number",
        "product": {
            "productId": "number",
            "productName": "string",
            "price": "number"
        },
        "quantity": "number",
        "selected": "boolean"
    },
    "success": true,
    "message": "Cart item updated successfully"
}
```

### Remove Cart Item
```http
DELETE /cart-item/{cartItemId}
```

**Functionality**: Removes an item from the user's shopping cart.

**Input**: Path parameter `cartItemId`

**Output**:
```json
{
    "success": true,
    "message": "Item removed from cart successfully"
}
```

## Order Management

### Create Order
```http
POST /order
```

**Functionality**: Creates a new order from the user's shopping cart.

**Input**:
```json
{
    "branchId": "number",
    "cartId": "number",
    "promoCodes": ["string"],
    "paymentMethod": "string",
    "shippingAddress": "string"
}
```

**Output**:
```json
{
    "result": {
        "orderId": "number",
        "user": {
            "userId": "number",
            "username": "string"
        },
        "branch": {
            "branchId": "number",
            "branchName": "string"
        },
        "orderDate": "string",
        "status": "string",
        "paymentMethod": "string",
        "shippingAddress": "string",
        "orderItems": [
            {
                "orderItemId": "number",
                "product": {
                    "productId": "number",
                    "productName": "string",
                    "price": "number"
                },
                "quantity": "number",
                "price": "number"
            }
        ],
        "subtotal": "number",
        "discount": "number",
        "grandTotal": "number",
        "deliveryInfo": {
            "deliveryInfoId": "number",
            "status": "string",
            "estimatedDeliveryDate": "string"
        }
    },
    "success": true,
    "message": "Order created successfully"
}
```

### Get Order by ID
```http
GET /order/{orderId}
```

**Functionality**: Retrieves detailed information about a specific order.

**Input**: Path parameter `orderId`

**Output**:
```json
{
    "result": {
        "orderId": "number",
        "user": {
            "userId": "number",
            "username": "string"
        },
        "branch": {
            "branchId": "number",
            "branchName": "string"
        },
        "orderDate": "string",
        "status": "string",
        "paymentMethod": "string",
        "shippingAddress": "string",
        "orderItems": [
            {
                "orderItemId": "number",
                "product": {
                    "productId": "number",
                    "productName": "string",
                    "price": "number"
                },
                "quantity": "number",
                "price": "number"
            }
        ],
        "subtotal": "number",
        "discount": "number",
        "grandTotal": "number",
        "deliveryInfo": {
            "deliveryInfoId": "number",
            "status": "string",
            "estimatedDeliveryDate": "string"
        }
    },
    "success": true,
    "message": "Order retrieved successfully"
}
```

### Get Orders by Branch
```http
GET /order/branch/{branchId}
```

**Functionality**: Retrieves a list of orders for a specific branch.

**Input**: Path parameter `branchId`

**Output**:
```json
{
    "result": [
        {
            "orderId": "number",
            "user": {
                "userId": "number",
                "username": "string"
            },
            "orderDate": "string",
            "status": "string",
            "paymentMethod": "string",
            "shippingAddress": "string",
            "subtotal": "number",
            "discount": "number",
            "grandTotal": "number"
        }
    ],
    "success": true,
    "message": "Orders retrieved successfully"
}
```

### Get User Orders
```http
GET /order/user
```

**Functionality**: Retrieves a list of orders for the currently authenticated user.

**Input**: None (uses authentication token)

**Output**:
```json
{
    "result": [
        {
            "orderId": "number",
            "branch": {
                "branchId": "number",
                "branchName": "string"
            },
            "orderDate": "string",
            "status": "string",
            "paymentMethod": "string",
            "shippingAddress": "string",
            "subtotal": "number",
            "discount": "number",
            "grandTotal": "number"
        }
    ],
    "success": true,
    "message": "Orders retrieved successfully"
}
```

### Cancel Order
```http
PUT /order/{orderId}/cancel
```

**Functionality**: Cancels an existing order and restores the stock.

**Input**: Path parameter `orderId`

**Output**:
```json
{
    "result": {
        "orderId": "number",
        "user": {
            "userId": "number",
            "username": "string"
        },
        "branch": {
            "branchId": "number",
            "branchName": "string"
        },
        "orderDate": "string",
        "status": "CANCELLED",
        "paymentMethod": "string",
        "shippingAddress": "string",
        "orderItems": [
            {
                "orderItemId": "number",
                "product": {
                    "productId": "number",
                    "productName": "string",
                    "price": "number"
                },
                "quantity": "number",
                "price": "number"
            }
        ],
        "subtotal": "number",
        "discount": "number",
        "grandTotal": "number",
        "deliveryInfo": {
            "deliveryInfoId": "number",
            "status": "CANCELLED",
            "estimatedDeliveryDate": "string"
        }
    },
    "success": true,
    "message": "Order cancelled successfully"
}
```

## Promo Code Management

### Get All Promo Codes
```http
GET /promo-code
```

**Functionality**: Retrieves a list of all promo codes in the system.

**Input**: None

**Output**:
```json
{
    "result": [
        {
            "promoCodeId": "number",
            "code": "string",
            "discountPercent": "number",
            "startDate": "string",
            "endDate": "string",
            "minOrderValue": "number",
            "maxDiscountValue": "number",
            "usageLimit": "number",
            "usageCount": "number"
        }
    ],
    "success": true,
    "message": "Promo codes retrieved successfully"
}
```

### Get Promo Code by ID
```http
GET /promo-code/{promoCodeId}
```

**Functionality**: Retrieves detailed information about a specific promo code.

**Input**: Path parameter `promoCodeId`

**Output**:
```json
{
    "result": {
        "promoCodeId": "number",
        "code": "string",
        "discountPercent": "number",
        "startDate": "string",
        "endDate": "string",
        "minOrderValue": "number",
        "maxDiscountValue": "number",
        "usageLimit": "number",
        "usageCount": "number"
    },
    "success": true,
    "message": "Promo code retrieved successfully"
}
```
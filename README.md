# MSA Project

## Overview

MSA (Multi-Store Application) powers multi-branch retail operations with role-based access control and end-to-end business workflows: inventory, inter-branch transfers, orders, products/promotions, suppliers, branches, accounts, discount campaigns, customer support, product search, shipping addresses, dashboards, and notifications.

Key components:
- Backend: Spring Boot, MySQL, Redis, Quartz Scheduler, MapStruct, FCM Notifications
- Security: JWT authentication via HttpOnly Cookies (no tokens in localStorage)
- Integrations: VNPay (payments), Goship (shipping – feature-flagged with robust fallback)
- Deployment: Docker Compose

---

## Roles & Authorization

- Admin: System-wide administration for branches, products, inventory, orders, accounts, discount campaigns, and reports. Reviews and approves transfer requests.
- Manager: Manages a single branch, inventory, and branch orders; creates transfer requests and confirms receipts.
- Surveyor: Performs physical stock audits and submits inventory check forms.
- Customer: Shops, pays, tracks orders, and manages profile/addresses.

Uses a Custom Security Service for flexible role checks, e.g. `@PreAuthorize("@customSecurity.isAdminOrManager()")` (no hardcoded MANAGER_1, MANAGER_2...).

---

## Core Business Workflows

### 1) Inventory & Stock Audits
- Surveyor conducts physical counts and submits results to the system.
- System compares physical vs. system quantities, computes variance, and generates reports for Admin/Manager.
- When `StockNumber <= MinThreshold`, a Quartz job auto-creates a `TransferRequest` (assumes one `InventoryProduct` per product per warehouse), checking `BatchNumber` and `ExpiryDate`.
- Surveyor can proactively submit pre-audit requests to Managers for verification before inbound/outbound operations.

### 2) Inter-branch Transfer
- Manager raises (or Quartz auto-creates) a `TransferRequest` with product, quantity, and destination.
- Admin inputs the approved quantity and approves/rejects.
- On approval:
  - Auto-create outbound shipment at source (status `shipped`) and decrement head-stock.
  - Auto-create inbound receipt at destination (status `in_progress`).
- Destination Manager verifies goods and marks `received`, which increments destination stock.
- Full FCM notification coverage: creation, pending approval, approved/rejected, auto-created.

### 3) Order Flow (Customer)
- Customer: select products → apply coupon/loyalty points (if any) → `PreviewOrder` → `CreateOrder` → choose COD or VNPay → track shipment via UI/notifications → complete on delivery.
- Admin: manage all orders and handle escalations.
- Manager: manage orders for their branch.

### 4) Products, ABC Classification & Auto-Promotions
- Admin CRUD for products, variants, bundled/attached products, categories, suppliers.
- ABC Classification: scheduled job processes `OrderDetails` revenue and assigns A (~80%), B (~15%), C (~5%) using Pareto; manual override allowed.
- Auto-Promotion (attached products):
  - When buying an A-class product, the system auto-selects a C-class product as a free item if `StockNumber > 2 * MinThreshold` and `ExpiryDate > 30 days`.
  - Perishables (meat/fish) marked `IsExemptFromPromotion = true` are excluded.
  - Cart updates set `IsFreeItem = true` when conditions are met.

### 5) Suppliers, Branches, Accounts, Discount Campaigns
- Suppliers: Admin maintains list, contacts, contracts, delivery history and quality metrics.
- Branches: Admin creates/updates/deletes branches; allocates resources and targets.
- Accounts: Admin creates/updates/deletes users and roles for Admin/Manager/Surveyor/Customer.
- Discount campaigns & coupons: create for supplier/category scope, define conditions, and track effectiveness.

### 6) Support, Search, Addresses, Dashboards, Notifications
- Customer support: in-app chat/complaint flows with direct responses.
- Product search: keyword, category, price, stock filters; detailed product view.
- Shipping addresses: full CRUD and default selection.
- Dashboards: Admin (global) and Manager (branch) views for inventory and sales insights.
- Notifications: FCM push for orders, transfers, and promotions.

---

## Security & Authentication
- JWT stored in HttpOnly cookies (Secure, SameSite=Lax) to mitigate XSS; no localStorage usage.
- Spring Security filter extracts JWT from cookies and sets the authentication context.
- Login/Logout endpoints set/clear cookies automatically.

---

## Representative APIs (short list)
- Return Orders: create, view, filter with paging, approve/reject; Goship integration (feature-flag `goship.enabled` with mock fallback on disable/failure).
- Transfer Requests: manual creation/approval/rejection and Quartz auto-creation; outbound/inbound docs and inventory updates.
- Orders: preview, create, payment via COD/VNPay, status tracking; loyalty/coupons.
- Inventory: audits, variance reports, low-stock alerts.
- Products: CRUD, ABC classification (cron), auto-promotion (attached free items with exemptions).
- Users/Roles: Admin/Manager/Surveyor/Customer; cookie-based auth.
- Notifications: FCM by role/event.

Note: For exact endpoints/DTOs/responses, see the corresponding `Controller` and `Service` classes.

---

## Run with Docker Compose

Requirements: Docker, Docker Compose, Git.

1) Clone the repository and open the project folder:
```
git clone https://github.com/anhquannn/MSA-ExpendingVersion.git
cd MSA_EV
```

2) Start the services:
```
docker-compose up -d
```

3) Check status and logs:
```
docker-compose ps
docker-compose logs -f app
```

4) Stop services:
```
docker-compose down
```

### Services
- app: Spring Boot on port 1081
- redis: Redis 6379
- db: MySQL 3306

### Environment (from docker-compose.yml)
- SPRING_REDIS_HOST=redis
- SPRING_REDIS_PORT=6379
- SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/market_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
- SPRING_DATASOURCE_USERNAME=root
- SPRING_DATASOURCE_PASSWORD=secret
- MYSQL_ROOT_PASSWORD=secret
- MYSQL_DATABASE=market_db

Volumes: `redis-data`, `mysql-data` for persistent storage.

---

## Important Configuration
- Cookie Auth: frontend uses `withCredentials`; backend sets/clears cookies on login/logout.
- Goship: `goship.enabled=false` by default (dev). Enable in production for real API; mock fallback prevents approval flow failures.
- Quartz: scheduled job to auto-create `TransferRequest` when stock hits `MinThreshold`.
- ABC Cron: scheduled classification from `OrderDetails` with optional manual overrides.

---

## Contributing
Contributions are welcome! Please branch off, commit your changes, and open a Pull Request. Describe the impacted business flow, related APIs, and any required migrations.

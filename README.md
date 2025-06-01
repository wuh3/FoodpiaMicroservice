# FoodpiaMicroservice
## Project Architecture
```
foodopia-backend/
├── api-gateway/
├── auth-service/
├── customer-service/
├── meal-service/
├── operations-service/        # Inventory & Customer Service
├── finance-service/           # Cost & Revenue Accounting
├── monitoring-service/        # Platform Monitoring
├── kitchen-service/
├── notification-service/
└── shared/
```
## Project request flow
```
Request Flow
Client Request
     ↓
API Gateway (Port 8080)
     ↓
┌─────────────────────────────────────┐
│  Gateway Security Layer             │
│  - JWT Validation                   │
│  - Route-based Authorization        │
│  - CORS Handling                    │
└─────────────────────────────────────┘
     ↓
Route to Appropriate Service:
├── /auth/** → Authentication Service (Port 8081)
├── /api/dishes/** → Food Service (Port 8082)
├── /api/customers/** → Customer Service (Port 8083)
└── /api/orders/** → Order Service (Port 8084)
```
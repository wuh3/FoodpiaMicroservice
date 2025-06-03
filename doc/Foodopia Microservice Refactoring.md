## General product structure

**The entire Fooopia platform will be interacting with 3 major actors: Customers, The Operation Team, and the Kitchen Team. The Customers will simply manage their subscriptions through the customer-frontend. The Operation team will manage the inventory, account the revenue, and measuring the workload of the platform through the operation-frontend. The Kitchen team will accept and complete distributions of meal orders daily through the kitchen-frontend.**

## Backend draft structure

```
foodopia-backend/
├── api-gateway/                     # API Gateway service
├── auth-service/                    # Authentication & User Management
├── customer-service/                # Customer subscriptions & profiles
├── meal-service/                    # Meals, dishes, ratings catalog
├── operations-service/              # Operations team functionality
├── kitchen-service/                 # Kitchen team service (new)
├── notification-service/            # Notifications across services
├── shared/                          # Shared libraries
│   ├── common/                      # Common utilities
│   └── proto/                       # Protocol buffers or DTOs
└── docker-compose.yml               # Local development setup
```

## Frontend Draft Structure

```
// Structure for each frontend
foodopia-customer-frontend/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   │   ├── MealSelection/
│   │   ├── Subscriptions/
│   │   └── Profile/
│   ├── services/
│   ├── context/
│   └── App.js
├── package.json
└── Dockerfile

foodopia-operations-frontend/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   │   ├── IngredientManagement/
│   │   ├── CostCalculation/
│   │   └── Analytics/
│   ├── services/
│   ├── context/
│   └── App.js
├── package.json
└── Dockerfile

foodopia-kitchen-frontend/
├── public/
├── src/
│   ├── components/
│   ├── pages/
│   │   ├── TaskManagement/
│   │   ├── Recipes/
│   │   └── ProductionSchedule/
│   ├── services/
│   ├── context/
│   └── App.js
├── package.json
└── Dockerfile
```

### Separation of Concerns for Authentication and Customer Services

![image-20250514144950925](/Users/haozhewu/Library/Application Support/typora-user-images/image-20250514144950925.png) 


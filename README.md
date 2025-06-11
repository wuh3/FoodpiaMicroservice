# FoodpiaMicroservice
## Problem Statement

## Project Goal
In today’s fast-paced urban environment, China’s modern working class faces increasing challenges in maintaining healthy and convenient eating habits. Irregular work schedules, limited in-office dining options, unhealthy delivery choices, and fluctuating food prices often make it difficult for individuals to balance nutrition, taste, and convenience.

Foodopia is designed to address these pain points by providing a fully customizable meal delivery platform tailored to the evolving needs of urban professionals. Through Foodopia, users can create personalized meal plans based on their dietary preferences, health goals, taste profiles, and work schedules. Each meal is freshly prepared daily, ensuring high quality and timely delivery that adapts to users’ busy lifestyles.

Key features of Foodopia include:
	•	DIY Meal Customization: Users can select ingredients, flavors, portion sizes, and dietary restrictions to build their own meals.
	•	Health-Focused Options: We collaborate with certified nutritionists to offer balanced meal plans, and introduce exclusive VIP options with premium nutritional guidance.
	•	Flexible Scheduling: Our platform allows users to easily adjust delivery times, pause or resume plans, and accommodate changing work patterns.
	•	AI-Powered Personalization: An embedded AI agent continuously learns from user preferences, provides recommendations, and offers personalized meal suggestions to enhance user experience.
	•	Dynamic Menus: Regular updates to the menu ensure variety, seasonal offerings, and continuous innovation to keep users engaged.

Foodopia’s mission is to bring together technology, nutrition, and convenience to transform daily eating into a seamless, enjoyable, and health-conscious experience for China’s working professionals.
## Technologies
This project is built for research purposes, exploring the integration of modern web, cloud, and AI technologies. The frontend leverages the React framework to enable dynamic, responsive user interfaces. On the backend, the system adopts a hybrid architecture combining Microservices and Event-Driven patterns, implemented using Java Spring Boot. Each microservice manages its own data independently, utilizing dedicated MongoDB instances to ensure scalability and service isolation. All services are containerized with Docker and orchestrated using Kubernetes for efficient deployment, scaling, and management. Furthermore, the system integrates advanced AI models via MCP servers to deliver more automated and intelligent user experiences.

## Project Architecture
```
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
=========================================================
=========================================================
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

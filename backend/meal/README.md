# Use Case 5: Meal Service

## Summary

**Meal service is one of the core services that involves cross servicee communication with multiple services including Customer, Operation, Delivery and Kitchen. It holds the responsibilities for catalog management, meal generations, meal customizations. It also helps coodinate the inventory management with the kitchen service, cost calculations with the operation service, subscription managment with the customer service, and delivery coordination with the delivery service.**



## Entity relationships

```
foodopia_meal database:
├── meal_templates          # Meal structure definitions
├── scheduled_meals         # Individual meal instances
├── dishes                  # Dish catalog
└── ingredients             # Ingredient catalog
```

### Meal Service Core Entities:

1. **MealTemplate** (structure definition)
2. **ScheduledMeal** (individual meal instance)
3. **Dish** (dish catalog)
4. **DishIngredient** (embedded in Dish)
5. **Ingredient** (base ingredients)

### Key Relationships:

- ScheduledMeal → MealTemplate (N:1)
- ScheduledMeal ↔ Dish (N:N via DBRef array)
- Dish → DishIngredient (1:N embedded)
- DishIngredient → Ingredient (N:1 embedded reference)

### Cross-Service References (String IDs):

- ScheduledMeal.userId → User (customer-service)
- ScheduledMeal.subscriptionId → UserSubscription (customer-service)
- ScheduledMeal.deliveryId → Delivery (delivery-service)

## API docs

### Meal Service APIs

#### Meal Templates

```
GET    /api/meal-templates              # List templates
GET    /api/meal-templates/{id}         # Get template details
POST   /api/meal-templates/{id}/validate # Validate dish selection
POST   /api/meal-templates              # Create template (admin)
```

#### Dishes

```
GET    /api/dishes                      # List dishes (filterable)
GET    /api/dishes/{id}                 # Get dish details
GET    /api/dishes/category/{category}  # Get dishes by category
GET    /api/dishes/available            # Get available dishes
POST   /api/dishes                      # Create dish (admin)
PUT    /api/dishes/{id}                 # Update dish (admin)
PATCH  /api/dishes/{id}/availability    # Toggle availability (kitchen)
```

#### Ingredients

```
GET    /api/ingredients                 # List ingredients
GET    /api/ingredients/{id}            # Get ingredient details
POST   /api/ingredients                 # Create ingredient (admin)
PATCH  /api/ingredients/{id}/price      # Update price (admin)
```

#### Meal Customization (Meal-Service manages customization)

```
GET    /api/meal-customizations/{scheduledMealId}/recommendations  # Get options
PUT    /api/meal-customizations/{scheduledMealId}/customize        # Customize meal
GET    /api/meal-customizations/{scheduledMealId}                  # Get customization details
```
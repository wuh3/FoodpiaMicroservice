# Use Case 7: Customer profile

## Summary

The central user information management place. It should allow user to add, modify, and delete personal info and diet preference. It should also allow user to manage current subscriptions and payment options.

## Sectors

### Personal information

1. Profile pic
2. Legal name
3. nickname
4. phone
5. email
6. Address

### Diet preference

**Each section below should contain bunch of tags**

1. Savory: light -> strong
2. Dietary goal: Low fat, high protein, vegan, vegetarian etc.
3. Allegies
4. Food to avoid

### Subscription

#### List of subscriptions

- Manage and cancel

#### Manage subscription

- Scheduling
- Skip meals
- Modify meals
- Cancel

#### Delivery management (link to delivery service)

- View delivery
- feedback
- Report problem

### Payments

1. Default payment option
2. credits
3. Credit cards

## Functional Requirements

1. User should be able to view and update personal information
2. User should be able to view and update Diet Preference. Diet goal can be multiple tags
3. User should be able to view, add, update, and cancel subscription
   1. For each subscription, user should be able to view the destails (such as number of meals per month, number of meals left for current month, the meal template for each meal, need to communicate with Meal Service )
   2. For each subscription, an optional delivery schedule can be added, updated, and deleted. A delivery schedule should work like a calendar. User can assign meal delivery on valid time slots everyday (like 11:30am for lunch on Monday 05/25/2026). User may or may not fully assign all the meal quota for the month. 
      1. Each delivery on the calendar can be customized: meal content, delivery address
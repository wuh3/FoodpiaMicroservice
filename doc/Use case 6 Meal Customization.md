# Use case 6: Meal Customization

## Summary

User makes subscription to one meal plan and customize the meals for the following week.

## Basic Flow

#### a. Precondition

User has already made a subscription to a meal plan. The user received notification about making customization for next week's meals

#### b. Customization page

User goes to the customization page, where it shows the current meal plan and meal schedules for the following weeks. There will be a place to make customization, pending customization requests and historical customizations.

#### c. Meal customization

Preferably, the user can interact with a day on the calendar panel, clicking on breakfast/lunch/dinner. By clicking, a form pops up to allow user to select dishes corresponding to the meal plan and delivery time. (Optional but useful: user can copy customizations from previous days or select auto-select). After completing the form, the customization for the selected meal was cached and user proceeds to customize more meals. 

#### d. Submitting request

User completed all the customization forms and click on submitting request. A submission confirmation received. 

System records the submission request and notify the kitchen service and delivery service. 

System keeps track of the customization request, updates the state of current meal plan (meals left, customization history)

#### e. Request confirmed

System received confirmation notifications from related services. System updates pending request to confirmed. System sends notification to user. 

## Alternate flow A: modifying exisiting customization

**A1.**

User initializes changes on existing meal customization.

**A2.**

User completed all the customization forms and click on submitting request. A submission confirmation received. 

System records the submission request and notify the kitchen service and delivery service. 

System keeps track of the customization request, updates the state of current meal plan (meals left, customization history)

**A3.**

System received confirmation notifications from related services. System updates pending request to confirmed. System sends notification to user. 



## Alternate flow B: customization rejected or error

**B1.**

One or more related service rejected the customization request, or the request timed out. System sends notification to the user. 

**B2.**

System initializes rollback events and notify related services.

**B3a**

System received success confirmation from related services. System roll back to the original customization

**B3b**

System received error or time out from related services. System retry and repeat step B2 for 2 more times. If still fails, initialize a manual rollback request.




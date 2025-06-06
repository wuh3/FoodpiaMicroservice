import axios from 'axios';

const API_URL = '/api/meal-plans/';

class MealPlanService {
    async getAllMealPlans() {
        try {
            const response = await axios.get(API_URL);
            return response.data;
        } catch (error) {
            console.error('Error fetching meal plans:', error);
            // For now, return dummy data
            return [
                {
                    id: 1,
                    name: 'Basic Plan',
                    mealsPerMonth: 30,
                    dishOptions: [2, 3, 4],
                    price: 149.99,
                    description: 'Perfect for individuals looking for convenient daily meals',
                },
                {
                    id: 2,
                    name: 'Family Plan',
                    mealsPerMonth: 45,
                    dishOptions: [3, 4, 5],
                    price: 199.99,
                    description: 'Ideal for small families with varied meal preferences',
                },
                {
                    id: 3,
                    name: 'Premium Plan',
                    mealsPerMonth: 60,
                    dishOptions: [3, 4, 5, 6],
                    price: 249.99,
                    description: 'Our most comprehensive plan with maximum flexibility',
                }
            ];
        }
    }

    async getMealPlanById(id) {
        try {
            const response = await axios.get(API_URL + id);
            return response.data;
        } catch (error) {
            console.error(`Error fetching meal plan with ID ${id}:`, error);
            return null;
        }
    }
}

export default new MealPlanService();
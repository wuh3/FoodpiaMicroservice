import axios from 'axios';

const API_URL = '/api/dishes/';

class DishService {
    async getAllDishes() {
        try {
            const response = await axios.get(API_URL);
            return response.data;
        } catch (error) {
            console.error('Error fetching dishes:', error);
            // Return dummy data for now
            return [
                {
                    id: 1,
                    name: 'Avocado Toast with Poached Eggs',
                    description: 'Whole grain toast topped with smashed avocado, poached eggs, and microgreens.',
                    tags: ['Breakfast', 'Healthy', 'High Protein', 'Vegetarian'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?avocado-toast'
                },
                {
                    id: 2,
                    name: 'Quinoa Bowl with Roasted Vegetables',
                    description: 'Fluffy quinoa topped with seasonal roasted vegetables, chickpeas, and tahini dressing.',
                    tags: ['Lunch/Dinner', 'Vegan', 'Low Calories', 'Gluten-Free'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?quinoa-bowl'
                },
                {
                    id: 3,
                    name: 'Grilled Salmon with Lemon Dill Sauce',
                    description: 'Wild-caught salmon fillet grilled to perfection, served with a light lemon dill sauce.',
                    tags: ['Lunch/Dinner', 'High Protein', 'Keto-Friendly', 'Omega-3 Rich'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?salmon'
                },
                {
                    id: 4,
                    name: 'Greek Yogurt Parfait',
                    description: 'Layers of Greek yogurt, fresh berries, honey, and homemade granola.',
                    tags: ['Breakfast', 'Healthy', 'Low Calories', 'Vegetarian'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?yogurt-parfait'
                },
                {
                    id: 5,
                    name: 'Chicken Fajita Bowl',
                    description: 'Grilled chicken strips with bell peppers, onions, and Mexican spices over brown rice.',
                    tags: ['Lunch/Dinner', 'High Protein', 'Gluten-Free'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?chicken-fajita'
                },
                {
                    id: 6,
                    name: 'Veggie Stir Fry with Tofu',
                    description: 'Crispy tofu and colorful vegetables stir-fried in a ginger-garlic sauce.',
                    tags: ['Lunch/Dinner', 'Vegan', 'Low Calories', 'High Fiber'],
                    imageUrl: 'https://source.unsplash.com/random/300x200/?stir-fry'
                }
            ];
        }
    }

    async getDishById(id) {
        try {
            const response = await axios.get(API_URL + id);
            return response.data;
        } catch (error) {
            console.error(`Error fetching dish with ID ${id}:`, error);
            return null;
        }
    }

    async getPopularDishes() {
        try {
            const response = await axios.get(API_URL + 'popular');
            return response.data;
        } catch (error) {
            console.error('Error fetching popular dishes:', error);
            // Return dummy data for now
            return [
                {
                    id: 1,
                    name: 'Mediterranean Chicken Bowl',
                    description: 'Grilled chicken with hummus, quinoa, cucumber, tomatoes, and feta cheese.',
                    tags: ['Lunch/Dinner', 'High Protein', 'Mediterranean'],
                    imageUrl: 'https://source.unsplash.com/random/600x400/?mediterranean-bowl',
                    rating: 4.9,
                    ordersLastMonth: 1243
                },
                {
                    id: 2,
                    name: 'Protein Pancakes with Berries',
                    description: 'Fluffy protein-packed pancakes topped with fresh mixed berries and maple syrup.',
                    tags: ['Breakfast', 'High Protein', 'Vegetarian'],
                    imageUrl: 'https://source.unsplash.com/random/600x400/?pancakes',
                    rating: 4.8,
                    ordersLastMonth: 1187
                },
                {
                    id: 3,
                    name: 'Teriyaki Salmon with Stir-Fried Vegetables',
                    description: 'Wild-caught salmon glazed with teriyaki sauce, served with stir-fried vegetables.',
                    tags: ['Lunch/Dinner', 'High Protein', 'Omega-3 Rich'],
                    imageUrl: 'https://source.unsplash.com/random/600x400/?teriyaki-salmon',
                    rating: 4.7,
                    ordersLastMonth: 1092
                },
                {
                    id: 4,
                    name: 'Thai Coconut Curry with Tofu',
                    description: 'Crispy tofu and vegetables in a creamy coconut curry sauce, served with jasmine rice.',
                    tags: ['Lunch/Dinner', 'Vegan', 'Spicy'],
                    imageUrl: 'https://source.unsplash.com/random/600x400/?curry',
                    rating: 4.6,
                    ordersLastMonth: 978
                },
                {
                    id: 5,
                    name: 'Overnight Chia Pudding',
                    description: 'Chia seeds soaked in almond milk, topped with fresh fruits and nuts.',
                    tags: ['Breakfast', 'Vegan', 'High Fiber'],
                    imageUrl: 'https://source.unsplash.com/random/600x400/?chia-pudding',
                    rating: 4.5,
                    ordersLastMonth: 912
                }
            ];
        }
    }

    async getAllTags() {
        try {
            const response = await axios.get(API_URL + 'tags');
            return response.data;
        } catch (error) {
            console.error('Error fetching tags:', error);
            // Extract all unique tags from the dummy dishes
            const dishes = await this.getAllDishes();
            const allTags = dishes.flatMap(dish => dish.tags);
            return [...new Set(allTags)];
        }
    }

    async searchDishes(query, tags) {
        try {
            const response = await axios.post(API_URL + 'search', { query, tags });
            return response.data;
        } catch (error) {
            console.error('Error searching dishes:', error);

            // Filter dishes locally based on the search query and tags
            const dishes = await this.getAllDishes();

            return dishes.filter(dish => {
                // Check if dish matches search query
                const matchesQuery = !query ||
                    dish.name.toLowerCase().includes(query.toLowerCase()) ||
                    dish.description.toLowerCase().includes(query.toLowerCase());

                // Check if dish has all the selected tags
                const matchesTags = !tags || tags.length === 0 ||
                    tags.every(tag => dish.tags.includes(tag));

                return matchesQuery && matchesTags;
            });
        }
    }
}

export default new DishService();
import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Badge, Spinner, Alert } from 'react-bootstrap';
import axios from 'axios';
import '../../css/PopularDishesPage.css';

const PopularDishesPage = () => {
    const [popularDishes, setPopularDishes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchPopularDishes = async () => {
            try {
                setLoading(true);
                // In production, use the API endpoint
                const response = await axios.get('/api/dishes/popular/previous-month');
                setPopularDishes(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching popular dishes:', error);
                setError('Failed to load popular dishes. Please try again later.');
                setLoading(false);

                // For development/demo, use mock data if API fails
                const mockPopularDishes = [
                    {
                        dish: {
                            id: 1,
                            name: 'Mediterranean Chicken Bowl',
                            description: 'Grilled chicken with hummus, quinoa, cucumber, tomatoes, and feta cheese.',
                            tags: ['Lunch/Dinner', 'High Protein', 'Mediterranean'],
                            imageUrl: 'https://source.unsplash.com/random/600x400/?mediterranean-bowl',
                        },
                        averageRating: 4.9,
                        ratingCount: 243
                    },
                    {
                        dish: {
                            id: 2,
                            name: 'Protein Pancakes with Berries',
                            description: 'Fluffy protein-packed pancakes topped with fresh mixed berries and maple syrup.',
                            tags: ['Breakfast', 'High Protein', 'Vegetarian'],
                            imageUrl: 'https://source.unsplash.com/random/600x400/?pancakes',
                        },
                        averageRating: 4.8,
                        ratingCount: 187
                    },
                    {
                        dish: {
                            id: 3,
                            name: 'Teriyaki Salmon with Stir-Fried Vegetables',
                            description: 'Wild-caught salmon glazed with teriyaki sauce, served with stir-fried vegetables.',
                            tags: ['Lunch/Dinner', 'High Protein', 'Omega-3 Rich'],
                            imageUrl: 'https://source.unsplash.com/random/600x400/?teriyaki-salmon',
                        },
                        averageRating: 4.7,
                        ratingCount: 92
                    },
                    {
                        dish: {
                            id: 4,
                            name: 'Thai Coconut Curry with Tofu',
                            description: 'Crispy tofu and vegetables in a creamy coconut curry sauce, served with jasmine rice.',
                            tags: ['Lunch/Dinner', 'Vegan', 'Spicy'],
                            imageUrl: 'https://source.unsplash.com/random/600x400/?curry',
                        },
                        averageRating: 4.6,
                        ratingCount: 78
                    },
                    {
                        dish: {
                            id: 5,
                            name: 'Overnight Chia Pudding',
                            description: 'Chia seeds soaked in almond milk, topped with fresh fruits and nuts.',
                            tags: ['Breakfast', 'Vegan', 'High Fiber'],
                            imageUrl: 'https://source.unsplash.com/random/600x400/?chia-pudding',
                        },
                        averageRating: 4.5,
                        ratingCount: 65
                    }
                ];

                setPopularDishes(mockPopularDishes);
            }
        };

        fetchPopularDishes();
    }, []);

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading popular dishes...</p>
            </Container>
        );
    }

    if (error && popularDishes.length === 0) {
        return (
            <Container className="py-5">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <h1 className="text-center mb-2">Most Popular Dishes</h1>
            <p className="text-center text-muted mb-5">Based on last month's ratings</p>

            {error && (
                <Alert variant="warning" className="mb-4">
                    {error} Showing cached data.
                </Alert>
            )}

            <Row className="justify-content-center">
                {popularDishes.map((popularDish, index) => {
                    const { dish, averageRating, ratingCount } = popularDish;

                    return (
                        <Col xs={12} className="mb-5" key={dish.id}>
                            <Card className="popular-dish-card">
                                <Row className="g-0">
                                    <Col md={6} className={index % 2 === 0 ? 'order-md-1' : 'order-md-2'}>
                                        <Card.Img
                                            src={dish.imageUrl}
                                            alt={dish.name}
                                            className="popular-dish-image"
                                        />
                                    </Col>
                                    <Col md={6} className={index % 2 === 0 ? 'order-md-2' : 'order-md-1'}>
                                        <Card.Body className="p-4 d-flex flex-column h-100 justify-content-center">
                                            <div className="popularity-metrics mb-3">
                        <span className="rating">
                          <i className="bi bi-star-fill"></i> {averageRating.toFixed(1)}/5
                        </span>
                                                <span className="ratings-count">
                          {ratingCount} {ratingCount === 1 ? 'rating' : 'ratings'}
                        </span>
                                            </div>
                                            <h2 className="dish-title">{dish.name}</h2>
                                            <Card.Text className="dish-description mb-4">
                                                {dish.description}
                                            </Card.Text>
                                            <div className="dish-tags mb-3">
                                                {dish.tags.map(tag => (
                                                    <Badge
                                                        key={tag}
                                                        bg="light"
                                                        text="dark"
                                                        className="me-2 mb-2"
                                                    >
                                                        {tag}
                                                    </Badge>
                                                ))}
                                            </div>
                                            <button className="btn btn-primary">Add to Cart</button>
                                        </Card.Body>
                                    </Col>
                                </Row>
                            </Card>
                        </Col>
                    );
                })}
            </Row>
        </Container>
    );
};

export default PopularDishesPage;
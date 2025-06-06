import React, { useState, useEffect } from 'react';
import { Card, Form, Button, Row, Col, Alert } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import axios from 'axios';
import '../../css/MealRatingComponent.css';

const StarRating = ({ rating, setRating, disabled = false }) => {
    const [hover, setHover] = useState(0);

    return (
        <div className="star-rating">
            {[...Array(5)].map((_, index) => {
                const starValue = index + 1;

                return (
                    <span
                        key={index}
                        className={`star ${starValue <= (hover || rating) ? 'filled' : ''}`}
                        onClick={() => !disabled && setRating(starValue)}
                        onMouseEnter={() => !disabled && setHover(starValue)}
                        onMouseLeave={() => !disabled && setHover(0)}
                        style={{ cursor: disabled ? 'default' : 'pointer' }}
                    >
            ★
          </span>
                );
            })}
        </div>
    );
};

const MealRatingComponent = ({ meal, onRatingSubmitted }) => {
    const { isAuthenticated } = useAuth();
    const [mealRating, setMealRating] = useState(0);
    const [dishRatings, setDishRatings] = useState({});
    const [feedback, setFeedback] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);
    const [existingRating, setExistingRating] = useState(null);

    // Check if meal has already been rated
    useEffect(() => {
        const checkExistingRating = async () => {
            if (!isAuthenticated || !meal) return;

            try {
                const response = await axios.get(`/api/ratings/meals/${meal.id}`);
                if (response.data) {
                    setExistingRating(response.data);
                    setMealRating(response.data.mealRating || 0);
                    setDishRatings(response.data.dishRatings || {});
                    setFeedback(response.data.feedback || '');
                }
            } catch (error) {
                // No existing rating found or error occurred
                console.log('No existing rating found');
            }
        };

        checkExistingRating();
    }, [meal, isAuthenticated]);

    // Set up initial dish ratings
    useEffect(() => {
        if (meal && meal.dishes && Object.keys(dishRatings).length === 0) {
            const initialDishRatings = {};
            meal.dishes.forEach(dish => {
                initialDishRatings[dish.id] = 0;
            });
            setDishRatings(initialDishRatings);
        }
    }, [meal, dishRatings]);

    const handleDishRatingChange = (dishId, rating) => {
        setDishRatings(prev => ({
            ...prev,
            [dishId]: rating
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!isAuthenticated) {
            setError('You must be logged in to rate a meal');
            return;
        }

        if (mealRating === 0) {
            setError('Please provide an overall rating for the meal');
            return;
        }

        // Filter out dishes that weren't rated (rating === 0)
        const filteredDishRatings = {};
        Object.entries(dishRatings).forEach(([dishId, rating]) => {
            if (rating > 0) {
                filteredDishRatings[dishId] = rating;
            }
        });

        const ratingData = {
            mealRating,
            dishRatings: filteredDishRatings,
            feedback
        };

        setLoading(true);
        setError(null);

        try {
            let response;

            if (existingRating) {
                // Update existing rating
                response = await axios.put(`/api/ratings/${existingRating.id}`, ratingData);
            } else {
                // Create new rating
                response = await axios.post(`/api/ratings/meals/${meal.id}`, ratingData);
            }

            setSuccess(true);
            setLoading(false);

            if (onRatingSubmitted) {
                onRatingSubmitted(response.data);
            }
        } catch (error) {
            setError('Failed to submit rating. Please try again.');
            setLoading(false);
        }
    };

    if (!meal) {
        return null;
    }

    if (!isAuthenticated) {
        return (
            <Alert variant="info">
                Please log in to rate this meal.
            </Alert>
        );
    }

    if (meal.status !== 'DELIVERED' && meal.status !== 'COMPLETED' && meal.status !== 'RATED' && !existingRating) {
        return (
            <Alert variant="info">
                You can rate this meal after it has been delivered.
            </Alert>
        );
    }

    if (success) {
        return (
            <Alert variant="success">
                Thank you for your rating! Your feedback helps us improve our service.
            </Alert>
        );
    }

    return (
        <Card className="meal-rating-card">
            <Card.Header>
                <h4>{existingRating ? 'Update Your Rating' : 'Rate Your Meal'}</h4>
            </Card.Header>
            <Card.Body>
                {error && <Alert variant="danger">{error}</Alert>}

                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-4">
                        <Form.Label><strong>Overall Meal Rating</strong></Form.Label>
                        <div className="d-flex align-items-center">
                            <StarRating rating={mealRating} setRating={setMealRating} />
                            <span className="ms-2 text-muted">
                {mealRating > 0 ? `${mealRating}/5` : 'Select a rating'}
              </span>
                        </div>
                    </Form.Group>

                    {meal.dishes && meal.dishes.length > 0 && (
                        <Form.Group className="mb-4">
                            <Form.Label><strong>Rate Individual Dishes</strong> (optional)</Form.Label>

                            {meal.dishes.map(dish => (
                                <Row key={dish.id} className="mb-3 align-items-center">
                                    <Col xs={12} md={6}>
                                        <div className="dish-name">{dish.name}</div>
                                    </Col>
                                    <Col xs={12} md={6}>
                                        <div className="d-flex align-items-center">
                                            <StarRating
                                                rating={dishRatings[dish.id] || 0}
                                                setRating={(rating) => handleDishRatingChange(dish.id, rating)}
                                            />
                                            <span className="ms-2 text-muted">
                        {dishRatings[dish.id] > 0 ? `${dishRatings[dish.id]}/5` : 'Select a rating'}
                      </span>
                                        </div>
                                    </Col>
                                </Row>
                            ))}
                        </Form.Group>
                    )}

                    <Form.Group className="mb-4">
                        <Form.Label><strong>Feedback</strong> (optional)</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            placeholder="Tell us what you liked or how we can improve..."
                            value={feedback}
                            onChange={(e) => setFeedback(e.target.value)}
                        />
                    </Form.Group>

                    <Button
                        type="submit"
                        variant="primary"
                        disabled={loading || mealRating === 0}
                    >
                        {loading ? 'Submitting...' : existingRating ? 'Update Rating' : 'Submit Rating'}
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    );
};

export default MealRatingComponent;
import React from 'react';
import { Container, Row, Col, Card, Button, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import '../../css/Home.css';

const Home = () => {
    const { currentUser, isAuthenticated } = useAuth();

    // Sample meal plan data - this would come from an API in production
    const mealPlans = [
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

    return (
        <Container className="py-3">
            <div className="hero-section">
                <h1>Welcome to Foodopia</h1>
                <p>Delicious food delivered to your doorstep</p>
                {!isAuthenticated && (
                    <Button as={Link} to="/register" variant="light" size="lg" className="mt-3">
                        Get Started
                    </Button>
                )}
            </div>

            {isAuthenticated && (
                <Alert variant="success" className="text-center mb-4">
                    Welcome back, <strong>{currentUser.username}</strong>! Ready to order some delicious food?
                </Alert>
            )}

            {/* Links to Menu and Popular Dishes */}
            <div className="d-flex justify-content-center gap-3 mt-4 mb-5">
                <Link to="/menu" className="btn btn-primary">Browse Full Menu</Link>
                <Link to="/popular" className="btn btn-outline-primary">View Popular Dishes</Link>
            </div>

            {/* Meal Plans Section */}
            <section className="meal-plans mb-5">
                <h2 className="text-center mb-4">Select Your Meal Plan</h2>
                <Row>
                    {mealPlans.map((plan) => (
                        <Col md={4} key={plan.id} className="mb-4">
                            <Card className="h-100 shadow-sm meal-plan-card">
                                <Card.Body>
                                    <Card.Title>{plan.name}</Card.Title>
                                    <Card.Subtitle className="mb-2 text-muted">{plan.mealsPerMonth} meals/month</Card.Subtitle>
                                    <Card.Text>{plan.description}</Card.Text>
                                    <div className="dish-options mb-3">
                                        <p className="mb-1">Dishes per meal options:</p>
                                        <div className="d-flex gap-2">
                                            {plan.dishOptions.map((option) => (
                                                <div key={option} className="dish-option-pill">
                                                    {option} dishes
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                    <div className="price-tag mb-3">${plan.price}/month</div>
                                    <Button variant="success" className="w-100">Select Plan</Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </section>

            {/* Features Section */}
            <Row className="mb-5">
                <Col md={4} className="mb-3">
                    <Card className="h-100 feature-card">
                        <Card.Img variant="top" src="https://source.unsplash.com/random/300x200/?pizza" />
                        <Card.Body>
                            <Card.Title>Fast Delivery</Card.Title>
                            <Card.Text>
                                We deliver your food as fast as possible. Our delivery is always on time.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4} className="mb-3">
                    <Card className="h-100 feature-card">
                        <Card.Img variant="top" src="https://source.unsplash.com/random/300x200/?restaurant" />
                        <Card.Body>
                            <Card.Title>Quality Food</Card.Title>
                            <Card.Text>
                                All our meals are prepared with high-quality ingredients by professional chefs.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4} className="mb-3">
                    <Card className="h-100 feature-card">
                        <Card.Img variant="top" src="https://source.unsplash.com/random/300x200/?chef" />
                        <Card.Body>
                            <Card.Title>Best Offers</Card.Title>
                            <Card.Text>
                                We provide the best deals and offers to make your dining experience affordable.
                            </Card.Text>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Home;
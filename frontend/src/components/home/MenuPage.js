import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, InputGroup, Badge } from 'react-bootstrap';
import axios from 'axios';
import '../../css/MenuPage.css';

const MenuPage = () => {
    const [dishes, setDishes] = useState([]);
    const [filteredDishes, setFilteredDishes] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedTags, setSelectedTags] = useState([]);
    const [availableTags, setAvailableTags] = useState([]);
    const [loading, setLoading] = useState(true);

    // Fetch dishes and tags from backend
    useEffect(() => {
        const fetchDishes = async () => {
            try {
                // In production, this would be an API call
                // For now, we'll use mock data
                const mockDishes = [
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

                setDishes(mockDishes);
                setFilteredDishes(mockDishes);

                // Extract unique tags from dishes
                const allTags = mockDishes.flatMap(dish => dish.tags);
                const uniqueTags = [...new Set(allTags)];
                setAvailableTags(uniqueTags);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching dishes:', error);
                setLoading(false);
            }
        };

        fetchDishes();
    }, []);

    // Filter dishes when search term or selected tags change
    useEffect(() => {
        let results = dishes;

        // Filter by search term
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            results = results.filter(
                dish => dish.name.toLowerCase().includes(term) ||
                    dish.description.toLowerCase().includes(term)
            );
        }

        // Filter by selected tags
        if (selectedTags.length > 0) {
            results = results.filter(dish =>
                selectedTags.every(tag => dish.tags.includes(tag))
            );
        }

        setFilteredDishes(results);
    }, [searchTerm, selectedTags, dishes]);

    // Handle search input change
    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    // Toggle tag selection
    const toggleTag = (tag) => {
        setSelectedTags(prevTags =>
            prevTags.includes(tag)
                ? prevTags.filter(t => t !== tag)
                : [...prevTags, tag]
        );
    };

    // Clear all filters
    const clearFilters = () => {
        setSearchTerm('');
        setSelectedTags([]);
    };

    if (loading) {
        return <Container className="py-5 text-center"><p>Loading menu...</p></Container>;
    }

    return (
        <Container className="py-5">
            <h1 className="text-center mb-4">Our Monthly Menu</h1>

            {/* Filter Section */}
            <div className="filter-section mb-4">
                <Row>
                    <Col md={6} className="mb-3">
                        <InputGroup>
                            <Form.Control
                                placeholder="Search dishes..."
                                value={searchTerm}
                                onChange={handleSearchChange}
                            />
                            {searchTerm && (
                                <InputGroup.Text
                                    className="clear-search"
                                    onClick={() => setSearchTerm('')}
                                >
                                    ✕
                                </InputGroup.Text>
                            )}
                        </InputGroup>
                    </Col>
                    <Col md={6} className="d-flex align-items-center">
                        {selectedTags.length > 0 && (
                            <button
                                className="btn btn-sm btn-outline-secondary me-2"
                                onClick={clearFilters}
                            >
                                Clear Filters
                            </button>
                        )}
                        <div className="filter-count">
                            {filteredDishes.length} {filteredDishes.length === 1 ? 'dish' : 'dishes'} found
                        </div>
                    </Col>
                </Row>

                <div className="tags-container mt-3">
                    {availableTags.map(tag => (
                        <div
                            key={tag}
                            className={`tag-pill ${selectedTags.includes(tag) ? 'active' : ''}`}
                            onClick={() => toggleTag(tag)}
                        >
                            {tag}
                        </div>
                    ))}
                </div>
            </div>

            {/* Dishes Grid */}
            <Row>
                {filteredDishes.length > 0 ? (
                    filteredDishes.map(dish => (
                        <Col md={4} key={dish.id} className="mb-4">
                            <Card className="h-100 dish-card">
                                <Card.Img variant="top" src={dish.imageUrl} />
                                <Card.Body>
                                    <Card.Title>{dish.name}</Card.Title>
                                    <Card.Text>{dish.description}</Card.Text>
                                    <div className="dish-tags">
                                        {dish.tags.map(tag => (
                                            <Badge
                                                key={tag}
                                                bg="light"
                                                text="dark"
                                                className="me-1 mb-1"
                                            >
                                                {tag}
                                            </Badge>
                                        ))}
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))
                ) : (
                    <Col className="text-center py-5">
                        <p>No dishes match your current filters. Try changing your search criteria.</p>
                        <button className="btn btn-primary" onClick={clearFilters}>
                            Reset Filters
                        </button>
                    </Col>
                )}
            </Row>
        </Container>
    );
};

export default MenuPage;
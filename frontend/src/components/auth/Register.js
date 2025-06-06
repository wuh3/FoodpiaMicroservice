import React, { useState, useEffect } from 'react';
import { Form, Button, Alert, Row, Col } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import { useAuth } from '../../context/AuthContext';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    const [errors, setErrors] = useState({});
    const [apiError, setApiError] = useState('');
    const [loading, setLoading] = useState(false);

    const { isAuthenticated } = useAuth();
    const navigate = useNavigate();

    // Field validation timers
    const [usernameTimer, setUsernameTimer] = useState(null);

    useEffect(() => {
        // If user is already logged in, redirect to home
        if (isAuthenticated) {
            navigate('/');
        }
    }, [isAuthenticated, navigate]);

    const validateField = (name, value) => {
        let error = '';

        switch (name) {
            case 'username':
                if (value.length < 6) {
                    error = 'Username must be at least 6 characters long';
                } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
                    error = 'Username must contain only letters and numbers';
                } else if (!/[a-zA-Z]/.test(value) || !/[0-9]/.test(value)) {
                    error = 'Username must contain both letters and numbers';
                }
                break;

            case 'email':
                if (!/\S+@\S+\.\S+/.test(value)) {
                    error = 'Please enter a valid email address';
                }
                break;

            case 'password':
                if (value.length < 8) {
                    error = 'Password must be at least 8 characters long';
                } else if (!/[a-z]/.test(value)) {
                    error = 'Password must contain at least one lowercase letter';
                } else if (!/[A-Z]/.test(value)) {
                    error = 'Password must contain at least one uppercase letter';
                } else if (!/[0-9]/.test(value)) {
                    error = 'Password must contain at least one digit';
                }
                break;

            case 'confirmPassword':
                if (value !== formData.password) {
                    error = 'Passwords do not match';
                }
                break;

            default:
                break;
        }

        return error;
    };

    const checkUsernameAvailability = async (username) => {
        if (username.length >= 6) {
            try {
                const isAvailable = await AuthService.checkUsernameAvailability(username);
                if (!isAvailable) {
                    setErrors(prev => ({
                        ...prev,
                        username: 'This username is already taken'
                    }));
                }
            } catch (error) {
                console.error('Error checking username availability:', error);
            }
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData({
            ...formData,
            [name]: value
        });

        // Validate field as user types
        const error = validateField(name, value);

        setErrors({
            ...errors,
            [name]: error
        });

        // For username, debounce the availability check
        if (name === 'username' && !error) {
            if (usernameTimer) clearTimeout(usernameTimer);
            const timer = setTimeout(() => {
                checkUsernameAvailability(value);
            }, 500);
            setUsernameTimer(timer);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Clear previous messages
        setApiError('');

        // Validate all fields
        const newErrors = {};
        Object.keys(formData).forEach(key => {
            const error = validateField(key, formData[key]);
            if (error) newErrors[key] = error;
        });

        // If there are validation errors, don't submit
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setLoading(true);

        try {
            const { username, email, password, confirmPassword } = formData;
            const response = await AuthService.register(username, email, password, confirmPassword);

            if (response.success) {
                // Redirect to login page with username prefilled
                navigate(`/login?username=${username}&registered=true`);
            } else {
                setApiError(response.message);
            }
        } catch (error) {
            setApiError('An unexpected error occurred. Please try again.');
            console.error('Registration error:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-form">
            <h2 className="auth-title">Create an Account</h2>

            {apiError && <Alert variant="danger">{apiError}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formUsername">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="Choose a username"
                        value={formData.username}
                        onChange={handleChange}
                        isInvalid={!!errors.username}
                        required
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.username}
                    </Form.Control.Feedback>
                    <Form.Text className="text-muted">
                        Username must be at least 6 characters and contain both letters and numbers.
                    </Form.Text>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formEmail">
                    <Form.Label>Email address</Form.Label>
                    <Form.Control
                        type="email"
                        name="email"
                        placeholder="Enter your email"
                        value={formData.email}
                        onChange={handleChange}
                        isInvalid={!!errors.email}
                        required
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.email}
                    </Form.Control.Feedback>
                </Form.Group>

                <Row>
                    <Col md={6}>
                        <Form.Group className="mb-3" controlId="formPassword">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                placeholder="Create a password"
                                value={formData.password}
                                onChange={handleChange}
                                isInvalid={!!errors.password}
                                required
                            />
                            <Form.Control.Feedback type="invalid">
                                {errors.password}
                            </Form.Control.Feedback>
                            <Form.Text className="text-muted">
                                Password must be at least 8 characters with lowercase, uppercase letters and digits.
                            </Form.Text>
                        </Form.Group>
                    </Col>

                    <Col md={6}>
                        <Form.Group className="mb-3" controlId="formConfirmPassword">
                            <Form.Label>Confirm Password</Form.Label>
                            <Form.Control
                                type="password"
                                name="confirmPassword"
                                placeholder="Confirm your password"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                isInvalid={!!errors.confirmPassword}
                                required
                            />
                            <Form.Control.Feedback type="invalid">
                                {errors.confirmPassword}
                            </Form.Control.Feedback>
                        </Form.Group>
                    </Col>
                </Row>

                <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                    {loading ? 'Registering...' : 'Register'}
                </Button>
            </Form>

            <div className="text-center mt-3">
                <p>Already have an account? <Link to="/login">Login here</Link></p>
            </div>
        </div>
    );
};

export default Register;
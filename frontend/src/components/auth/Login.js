import React, { useState, useEffect } from 'react';
import { Form, Button, Alert } from 'react-bootstrap';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import { useAuth } from '../../context/AuthContext';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState('');

    const { login, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // If user is already logged in, redirect to home
        if (isAuthenticated) {
            navigate('/');
        }

        // Check if username is passed via query params (from registration)
        const params = new URLSearchParams(location.search);
        const usernameParam = params.get('username');
        const registered = params.get('registered');

        if (usernameParam) {
            setUsername(usernameParam);
        }

        if (registered === 'true') {
            setSuccess('Registration successful! Please login with your credentials.');
        }
    }, [isAuthenticated, navigate, location.search]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Clear previous messages
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const response = await AuthService.login(username, password);

            if (response.success) {
                login(response.user);
                navigate('/');
            } else {
                setError(response.message || 'Login failed. Please check your credentials.');
            }
        } catch (error) {
            setError('An unexpected error occurred. Please try again.');
            console.error('Login error:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-form">
            <h2 className="auth-title">Login to Foodopia</h2>

            {error && <Alert variant="danger">{error}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formUsername">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Enter your username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        type="password"
                        placeholder="Enter your password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </Form.Group>

                <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </Button>
            </Form>

            <div className="text-center mt-3">
                <p>Don't have an account? <Link to="/register">Register here</Link></p>
            </div>
        </div>
    );
};

export default Login;
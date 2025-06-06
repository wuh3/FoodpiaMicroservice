import axios from 'axios';

const API_URL = '/api/auth/';

class AuthService {
    async login(username, password) {
        try {
            const response = await axios.post(API_URL + 'login', {
                username,
                password
            });

            // Assuming the backend returns a success field and username
            if (response.data.success) {
                return {
                    success: true,
                    user: {
                        username: response.data.username,
                        // Add other user data as needed
                    }
                };
            } else {
                return {
                    success: false,
                    message: response.data.message || 'Login failed'
                };
            }
        } catch (error) {
            let message = 'An error occurred during login';
            if (error.response) {
                message = error.response.data.message || 'Authentication failed';
            }
            return {
                success: false,
                message
            };
        }
    }

    async register(username, email, password, confirmPassword) {
        try {
            const response = await axios.post(API_URL + 'register', {
                username,
                email,
                password,
                confirmPassword
            });

            return {
                success: true,
                message: 'Registration successful'
            };
        } catch (error) {
            let message = 'An error occurred during registration';
            if (error.response) {
                message = error.response.data.message || 'Registration failed';
            }
            return {
                success: false,
                message
            };
        }
    }

    async checkUsernameAvailability(username) {
        try {
            const response = await axios.get(API_URL + `check-username/${username}`);
            return response.data.ok;
        } catch (error) {
            console.error('Error checking username availability:', error);
            return false;
        }
    }
}

export default new AuthService();
import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/layout/NavBar';
import Home from './components/home/Home';
import MenuPage from './components/home/MenuPage';
import PopularDishesPage from './components/home/PopularDishesPage';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import Footer from './components/layout/Footer';
import { Container } from 'react-bootstrap';

function App() {
    return (
        <div className="d-flex flex-column min-vh-100">
            <Navbar />
            <Container className="py-4 flex-grow-1">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/menu" element={<MenuPage />} />
                    <Route path="/popular" element={<PopularDishesPage />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </Container>
            <Footer />
        </div>
    );
}

export default App;
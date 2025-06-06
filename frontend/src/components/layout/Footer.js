import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';

const Footer = () => {
    return (
        <footer className="bg-dark text-light py-4 mt-4">
            <Container>
                <Row>
                    <Col md={4} className="mb-3 mb-md-0">
                        <h5>Foodopia</h5>
                        <p className="text-muted">Your favorite food ordering platform</p>
                    </Col>
                    <Col md={4} className="mb-3 mb-md-0">
                        <h5>Quick Links</h5>
                        <ul className="list-unstyled">
                            <li><a href="/" className="text-decoration-none text-muted">Home</a></li>
                            <li><a href="/menu" className="text-decoration-none text-muted">Menu</a></li>
                            <li><a href="/about" className="text-decoration-none text-muted">About Us</a></li>
                            <li><a href="/contact" className="text-decoration-none text-muted">Contact</a></li>
                        </ul>
                    </Col>
                    <Col md={4}>
                        <h5>Contact Us</h5>
                        <address className="text-muted">
                            123 Food Street<br />
                            Tasty City, FC 12345<br />
                            <a href="mailto:info@foodopia.com" className="text-decoration-none text-muted">info@foodopia.com</a>
                        </address>
                    </Col>
                </Row>
                <hr className="my-4" />
                <div className="text-center text-muted">
                    <small>&copy; {new Date().getFullYear()} Foodopia. All rights reserved.</small>
                </div>
            </Container>
        </footer>
    );
};

export default Footer;
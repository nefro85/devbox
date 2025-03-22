import React, { useState } from 'react';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import Toast from 'react-bootstrap/Toast';

function LoginToast({ titleText, messageText }) {
    const [showA, setShowA] = useState(true);

    const toggleShowA = () => setShowA(!showA);

    return (
        <Row>
            <Col>
                <Toast show={showA} onClose={toggleShowA}>
                    <Toast.Header>
                        <strong className="me-auto">{titleText}</strong>
                    </Toast.Header>
                    <Toast.Body><pre>{messageText}</pre></Toast.Body>
                </Toast>
            </Col>
        </Row>
    );
}

export default LoginToast;
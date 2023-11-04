import React, { useState, useEffect } from 'react'

import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Toast from 'react-bootstrap/Toast';


export default function Token({title, tokenVal}) {
    const [showA, setShowA] = useState(true);

    const toggleShowA = () => setShowA(!showA);

    return (
        <Row>
            <Col>
                <Toast show={showA} onClose={toggleShowA}>
                    <Toast.Header>
                        <strong className="me-auto">{title}</strong>
                    </Toast.Header>
                    <Toast.Body>{tokenVal}</Toast.Body>
                </Toast>
            </Col>
        </Row>
    );
}

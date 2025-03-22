import React, { useState, useEffect } from 'react'

import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Button } from 'react-bootstrap';


export default function AuthStuf() {


    function getCreds() {
        navigator.credentials.get({
            mediation: 'silent'
        }).then(credentials => {
            if (credentials) {
                console.log(credentials);
            }
        });
    }

    return (
        <Row>
            <Col>
                <Button onClick={getCreds}>GetCreds</Button>
            </Col>
        </Row>
    )
}


import React, { useState, useEffect } from 'react'

import Toast from 'react-bootstrap/Toast';
import Form from 'react-bootstrap/Form';


export default function Token({ title, tokenVal , onDispose}) {
    const [show, setShow] = useState(true);

    const dispose = () => {
        setShow(!show);
        if (onDispose != undefined) {
            onDispose.dispose();
        }
    }

    return (
        <Toast show={show} onClose={dispose}>
            <Toast.Header>
                <strong className="me-auto">{title}</strong>
            </Toast.Header>
            <Toast.Body>
                <Form.Control readOnly="true" as="textarea" value={tokenVal} />
            </Toast.Body>
        </Toast>
    );
}

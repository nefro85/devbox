import React, { useState, useEffect } from 'react'
import AuthForm from './Auth';
import { Button, Container } from 'react-bootstrap';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Token from './Token';


export default function User() {

    const [authOk, setAuthOk] = useState({ "authOk": false });
    const [tokenList, updateTokenList] = useState([]);

    useEffect(() => {
        fetch("user/status")
            .then(res => res.json())
            .then(
                (result) => {
                    console.log(result);
                    setAuthOk(result.authOk);
                },
                (error) => {
                    console.trace(error);
                }
            );
    });

    function handleLogin() {
        setAuthOk({ "authOk": true });
    }

    function handleLogOut() {
        fetch("user/logout").then(res => {
            console.log(res);
            setAuthOk({ "authOk": false });
        });
    }

    function askForToken() {
        fetch("user/token")
            .then(res => res.json())
            .then(res => {
                let up = [...tokenList, {
                    id: ++tokenList.length,
                    token: res.token
                }];
                updateTokenList(up);
            });
    }


    if (authOk == true) {
        return (
            <Container>
                <Row>
                    <Col>
                        <Button onClick={askForToken}>Generete Token</Button>
                    </Col>
                    <Col>
                        <Button onClick={handleLogOut}>Logout</Button>
                    </Col>
                </Row>
                {tokenList.map(e => 
                    <Row>
                        <Col>
                            <Token key={e.id} title={"JWToken #"+e.id} tokenVal={e.token} />
                        </Col>
                    </Row>                
                )}
            </Container>
        )
    } else {
        return (<AuthForm onLogin={() => handleLogin()} />)
    }
}
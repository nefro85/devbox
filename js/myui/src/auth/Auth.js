import React from 'react';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import WebAuthn from './vertx-auth-webauthn'
import { Container } from 'react-bootstrap';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import FloatingLabel from 'react-bootstrap/FloatingLabel';

const webAuthn = new WebAuthn({
    callbackPath: '/webauthn/callback',
    registerPath: '/webauthn/register',
    loginPath: '/webauthn/login'
});

class AuthForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = { userName: "", email: "" };

        this.handleRegister = this.handleRegister.bind(this);
        this.handleChangeEmail = this.handleChangeEmail.bind(this);
        this.handleChangeUserName = this.handleChangeUserName.bind(this)
        this.handleLogin = this.handleLogin.bind(this);
    }

    handleRegister() {
        webAuthn.register({
            name: this.state.email,
            displayName: this.state.userName
        }).then(() => {
            this.props.onLogin();
        }).catch(err => {
            console.error(err);
        });
    }

    handleLogin() {
        webAuthn.login({
            name: this.state.email
        }).then(() => {
            console.log('logged in');
            this.props.onLogin();
        }).catch(err => {
            console.error(err);
        });
    }

    handleChangeEmail(event) {
        const { name, value } = event.target;
        this.setState(prev => ({
            email: value
        }));
    }

    handleChangeUserName(event) {
        const { name, value } = event.target;
        this.setState(prev => ({
            userName: value
        }));
    }


    render() {
        return (
            <div className='form-signin w-100 m-auto'>
                <Form>
                    <FloatingLabel label="Email address" className='mb-3'>
                        <Form.Control type="email" placeholder="Enter email"
                            onChange={this.handleChangeEmail} value={this.state.email} />
                    </FloatingLabel>
                    <FloatingLabel label="Display Name" className='mb-3'>
                        <Form.Control type="text" placeholder="Your Name"
                            onChange={this.handleChangeUserName} value={this.state.userName} />
                    </FloatingLabel>
                </Form>
                <Row>
                    <Col>
                        <Button onClick={this.handleRegister}>Register</Button>
                    </Col>
                    <Col>
                        <Button onClick={this.handleLogin}>Login</Button>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default AuthForm;
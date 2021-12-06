import React, {Component} from 'react';
import {Button, Col, Container, NavLink, Row} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";

export default  class Register extends Component {
    render() {
        const registers = (e,v) =>{
            console.log(v,"register value")
        }
        return (
            <div>
                <Container>
                    <Row className="justify-content-center align-items-center">
                        <Col md={{size:5}}>
                            <img src="https://zira.uz/wp-content/uploads/2018/01/samsa-1.jpg" className="d-block w-100" alt="a"/>
                        </Col>
                        <Col md={{size:4,offset:1}}>
                            <AvForm onValidSubmit={registers}>
                                <AvField name="phoneNumber" label="Telefon raqam kiriting"
                                         required={true} placeholder="+998991112233"
                                         type="text"/>
                                <AvField name="password" label="Parol"
                                         required={true} placeholder="***"
                                         type="password"/>
                                <AvField name="firstName" label="Ismingiz"
                                         required={true} placeholder=" "
                                         type="text"/>
                                <AvField name="lastName" label="Parol"
                                         required={true} placeholder=" "
                                         type="text"/>
                                <Button type="submit" color="success">Saqlash</Button>
                                <NavLink href="/Login">Login</NavLink>
                            </AvForm>
                        </Col>
                    </Row>
                </Container>
            </div>
        );
    }
}
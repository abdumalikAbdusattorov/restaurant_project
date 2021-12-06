import React, {useState} from "react";
import {Link} from "react-router-dom";
import {Col, Collapse, Container, Nav, Navbar, NavItem, NavLink, Row} from "reactstrap";

export default function Employees() {
    const [isOpen, setIsOpen] = useState(false);
    const toggle = () => setIsOpen(!isOpen);
    return(
        <div>
            <Container>
                <Row>
                    <header>
                        <Navbar light expand="md">
                            <Collapse isOpen={isOpen} navbar>
                                <Nav className="mr-auto" navbar>
                                    <NavItem>
                                        <NavLink href="/adminPage">Korxona malumotlari</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/product">Mahsulotlar</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/category">Kategoriyalar</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/user">Xodimlar</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/rooms">Xonalar</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/payType">To'lov turi</NavLink>
                                    </NavItem>
                                    <NavItem>
                                        <NavLink href="/">Chiqish</NavLink>
                                    </NavItem>
                                </Nav>
                            </Collapse>
                        </Navbar>
                    </header>
                </Row>
                <Row>
                    <Col><h2>Xodimlar</h2></Col>
                </Row>
            </Container>
        </div>
    )
}
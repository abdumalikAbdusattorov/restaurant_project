import React, {useState} from 'react';
import {
    Button,
    Row,
    Col, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink,Collapse
} from 'reactstrap';
import Slider from "./Slider";


function Home(){
    return (
        <div>
            <main>
                <section style={{height: 600}} className="my-md-5">
                    <Row className="justify-content-center align-items-center">
                        <Col md={{size: 6}}>
                            <h2>Lorem ipsum</h2>
                            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Consequuntur culpa cum
                                explicabo quisquam, ut voluptate!</p>
                            <Button color="warning">Batafsil</Button>{' '}
                        </Col>
                        <Col md={{size: 6}}>
                            <img
                                src="https://avatanplus.com/files/resources/original/575b190d081041553bd9dae9.png"
                                className="w-100 h-100" alt=""/>
                        </Col>
                    </Row>
                </section>
                <section>
                    <Slider/>
                </section>
            </main>
        </div>
    );
}
export default Home;


import React from 'react';
import {
    Container, Col,
    Button, Row,NavLink
} from 'reactstrap';
import axios from 'axios'
import {AvForm, AvField,} from 'availity-reactstrap-validation';
import { useHistory } from "react-router-dom";
/*import reducer from "../reducer";*/




export default function Login() {
    /*const [state,dispatch] = useReducer(reducer)*/

    const history = useHistory();
        const login = (e,v) =>{
            //console.log(v,"login value")
            axios.post('http://localhost:8081/api/auth/login',v)
                .then(res=>{
                    /*console.log(res);*/
                    localStorage.setItem('MilliyToken', res.data.body.tokenType + " " + res.data.body.accessToken);
                    axios.get('http://localhost:8081/api/user/me', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            // console.log(res,"me ==========");
                            if (res.data.object != null) {
                                res.data.object.roles.map(role => {
                                    if (role.name === 'ROLE_ADMIN') {
                                        history.push("/adminPage")

                                    }
                                    if (role.name === 'ROLE_USER') {
                                        history.push("/userCabinet")
                                    }
                                    if (role.name === 'ROLE_MANAGER') {
                                        history.push("/userCabinet")
                                    }
                                })
                            }
                        });
                })
        }

        return (
            <div>
                <Container >
                    <Row className="justify-content-center align-items-center">
                        <Col md={{size:5}}  className="py-5">
                            <img src="https://zira.uz/wp-content/uploads/2020/05/krylyshki_v_medovo_soevom_marinade_s_kukuruzoy_6.jpg" className="d-block w-100" alt="a"/>
                        </Col>
                        <Col md={{size:4,offset:1}} className="py-5">
                            <AvForm onValidSubmit={login}>
                                <AvField name="phoneNumber" label="Telefon raqam kiriting"
                                         required={true} placeholder="+998991112233"/>
                                <AvField name="password" label="Parol"
                                         required={true} placeholder="***"
                                         type="password"/>
                                    <Button type="submit" color="success">Kirish</Button>
                                    <NavLink href="/signUp">Register</NavLink>
                            </AvForm>
                        </Col>
                    </Row>
                </Container>
            </div>
        );
    }


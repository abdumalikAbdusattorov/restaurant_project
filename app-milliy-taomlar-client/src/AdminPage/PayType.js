import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import axios from "axios";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {
    Col,
    Collapse,
    Container,
    Table,
    Modal,
    ModalHeader,
    ModalBody,
    ModalFooter,
    Button,
    Nav,
    Navbar,
    NavItem,
    NavLink,
    Row
} from "reactstrap";

export default function PayType() {
    useEffect(() => {
        if (!stopEffect) {
            axios.get('http://localhost:8081/api/payType/getAll', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res);
                    setPayTypeArray(res.data.object);
                    setStopEffect(true);
                })
        }
    }, []);

    const [stopEffect, setStopEffect] = useState(false);
    const [addPayTypeModal, setAddPayTypeModal] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const [currentPayType, setCurrentPayType] = useState('');
    const [modal, setModal] = useState(false);
    const [payTypeArray, setPayTypeArray] = useState([]);
    const [tempPayTypeId, setTempPayTypeId] = useState('');
    const [showDeleteModal, setShowDeleteModal] = useState(false);


    const toggle = () => setIsOpen(!isOpen);

    const addPayType = () => {
        setAddPayTypeModal(!addPayTypeModal);
        setCurrentPayType('');
    };
    const saveOrEditPayType = (e, v) => {
        console.log(v, "<<<<<<<<<<<<>>>>>>>>>>>>")
        if (currentPayType) {
            v = {...v, id: currentPayType.id}
        }
        axios.post('http://localhost:8081/api/payType', v, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setModal(!modal);
                axios.get('http://localhost:8081/api/payType/getAll', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setPayTypeArray(res.data.object);
                    })
                setAddPayTypeModal(!addPayTypeModal);
            })

    };

    const editPayType = (item) => {
        console.log(item,"bu edit payTypes")
        setAddPayTypeModal(!addPayTypeModal);
        setCurrentPayType(item);
    };
    const deletePayType = (id) => {
        setTempPayTypeId(id);
        setShowDeleteModal(!showDeleteModal)
    };
    const changeEnabled = (id) => {
        axios.get('http://localhost:8081/api/payType/blockOrActivate?id='+id, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res=>{
                axios.get('http://localhost:8081/api/payType/getAll', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setPayTypeArray(res.data.object);
                    })
            })
    };

    const togglePayTypeDeleteModal = () => {
        setTempPayTypeId('');
        setShowDeleteModal(!showDeleteModal);
    };
    const payTypeDeleteYes = () => {
        if (tempPayTypeId){
            axios.delete('http://localhost:8081/api/payType/'+tempPayTypeId,{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res=>{
                    setShowDeleteModal(!showDeleteModal);
                    axios.get('http://localhost:8081/api/payType/getAll', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            setPayTypeArray(res.data.object);
                        })
                })
        }
    };


    return (
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
                                        <NavLink href="/orders">Buyurtmalar</NavLink>
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
                    <Col><h2>To'lov turi</h2></Col>
                </Row>
                <Row>
                    <Col>
                        <Button className="btn btn-success" onClick={addPayType}>To'lov turi qo'shish</Button>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Table>
                            <thead>
                            <tr>
                                <th>№</th>
                                <th>Nomi Uz</th>
                                <th>Nomi Ru</th>
                                <th>Xolati</th>
                                <th>Operatsiyalar</th>
                            </tr>
                            </thead>
                            <tbody>
                            {payTypeArray ? payTypeArray.map((item, index) =>
                                <tr>
                                    <td>{index + 1}</td>
                                    <td>{item.nameUz}</td>
                                    <td>{item.nameRu}</td>
                                    <td><input type="checkbox" checked={item.active}/></td>
                                    <td>
                                        <Button  onClick={() => editPayType(item)} color="warning">Edit</Button>
                                        <Button className="ml-3" color="info"
                                                onClick={() => changeEnabled(item.id)}>{item.active ? "Bloklash" : "Aktivlashtirish"}</Button>

                                        <Button outline onClick={() => deletePayType(item.id)} className="ml-2"
                                                color="danger">Delete</Button>
                                    </td>
                                </tr>
                            ) : ''}
                            </tbody>
                        </Table>
                    </Col>
                </Row>
                <Modal isOpen={addPayTypeModal} fade={false} toggle={addPayType}>
                    <ModalHeader>{currentPayType ? "To'lov turini o'zgartirish " : "Yangi To'lov turi qo'shish"}</ModalHeader>
                    <AvForm onValidSubmit={saveOrEditPayType}>
                        <ModalBody>
                            <Row>
                                <Col>
                                    <AvField required={true} type="text"
                                             label="Nomi Uz" className="mt-2"
                                             placeholder="Nomi Uz"
                                             defaultValue={currentPayType ? currentPayType.nameUz : ''}
                                             name="nameUz"/>
                                    <AvField required={true} type="text"
                                             label="Nomi Ru" placeholder="Nomi Ru"
                                             defaultValue={currentPayType ? currentPayType.nameRu : ''}
                                             name="nameRu"/>
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button color="danger" onClick={addPayType}>Bekor qilish</Button>
                            <Button color="primary" type="submit">Saqlash</Button>
                        </ModalFooter>
                    </AvForm>
                </Modal>
                <Modal isOpen={showDeleteModal} toggle={togglePayTypeDeleteModal}>
                    <ModalHeader>To'lov turini o'chirishni istaysizmi?</ModalHeader>

                    <ModalBody>

                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={togglePayTypeDeleteModal}>Bekor qilish</Button>
                        <Button className="ml-3" color="success" onClick={payTypeDeleteYes}
                                type="button">O'chirish</Button>
                    </ModalFooter>
                </Modal>
            </Container>
        </div>
    )
}
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
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
import axios from "axios";
import {AvField, AvForm} from "availity-reactstrap-validation";
import Pagination from "react-js-pagination";

export default function Rooms() {

    useEffect(() => {
        if (!stopEffect) {

            axios.get('http://localhost:8081/api/room/getAllRoomsByPageable?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res,"asijdasd=================");
                    setRoomArray(res.data.object);
                    setStopEffect(true);
                    setTotalElement(res.data.totalElements)

                })
        }
    }, []);

    const [stopEffect, setStopEffect] = useState(false);
    const [addRoomModal, setAddRoomModal] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const [currentRoom, setCurrentRoom] = useState('');
    const [modal, setModal] = useState(false);
    const [roomArray, setRoomArray] = useState([]);
    const [activePage, setActivePage] = useState(1);
    const [totalElement, setTotalElement] = useState(0);
    const [tempRoomId, setTempRoomId] = useState('');
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [tempFileId,setTempFileId]=useState('')

    const deleteRoom = (id) => {
        setTempRoomId(id);
        setShowDeleteModal(!showDeleteModal);
    };

    const roomDeleteYes = () => {
        if (tempRoomId) {
            axios.delete('http://localhost:8081/api/room/' + tempRoomId, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    setShowDeleteModal(!showDeleteModal);

                    axios.get('http://localhost:8081/api/room/getAllRoomsByPageable?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            console.log(res);
                            setRoomArray(res.data.object);
                            setTotalElement(res.data.totalElements)
                        })
                })
        }
    };

    const editRoom = (item) => {
        setAddRoomModal(!addRoomModal);
        setCurrentRoom(item);
        setTempFileId(item.attachment)
        //console.log(item);
    };

    const saveOrEditRoom = (e, v) => {

        if (currentRoom) {
            v = {...v, id: currentRoom.id}
        }
        if (tempFileId){
            v={...v,attachment:tempFileId}
        }
        // console.log(v, "<<<<<<<<<<<<>>>>>>>>>>>>")
            axios.post('http://localhost:8081/api/room', v, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setModal(!modal);
                axios.get('http://localhost:8081/api/room/getAllRoomsByPageable', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setRoomArray(res.data.object);
                    })
                setAddRoomModal(!addRoomModal);
            })

    };

    const handlePageChange = (pageNumber) => {
        setActivePage(pageNumber);
        let page = pageNumber - 1;
        axios.get('http://localhost:8081/api/room/getAllRoomsByPageable?page=' + page + '&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                console.log(res);
                setRoomArray(res.data.object);
                setTotalElement(res.data.totalElements);
            })
    };
    const toggleRoomSaveOrEditModal = () => {
        setModal(!modal);
        setCurrentRoom('');
    };
    const toggleRoomDeleteModal = () => {
        setTempRoomId([]);
        setShowDeleteModal(!showDeleteModal);
    };
    const getPhotoId = (e) => {
        const formData = new FormData();

        formData.append('file', e.target.files[0]);
        formData.append("type", e.target.name);

        axios.post('http://localhost:8081/api/file',formData,{headers: {"Authorization": localStorage.getItem('MilliyToken'),"Content-Type": "multipart/form-data"}})
            .then(res => {
                console.log(res);
                setTempFileId(res.data.object[0].fileId)
                // setPayTypeArray(res.data.object);
                // setStopEffect(true);
            })
    };
    const addRoom = () => {
        setAddRoomModal(!addRoomModal);
        setCurrentRoom('');
        setTempFileId('')
    };
    const toggle = () => setIsOpen(!isOpen);

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
                <Col><h2>Xonalar</h2></Col>
            </Row>
            <Row>
                <Col>
                    <Button className="btn btn-success" onClick={addRoom}>Xona qo'shish</Button>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Table>
                        <thead>
                        <tr>
                            <th>№</th>
                            <th>Raqam</th>
                            <th>Xona malumot Uz</th>
                            <th>Xona malumot Ru</th>
                            <th>Narxi</th>
                            <th>Rasm</th>
                            <th>O'zgartirish | O'chirish</th>
                        </tr>
                        </thead>
                        <tbody>
                        {roomArray ? roomArray.map((item, index) =>
                            <tr>
                                <td>{(activePage * 10) + index + 1 - 10}</td>
                                <td>{item.number}</td>
                                {/*{console.log(item.number,"xona raqami")}*/}
                                <td>{item.descriptionUz}</td>
                                <td>{item.descriptionRu}</td>
                                <td>{item.price}</td>
                                <td>
                                    <div style={{width: "40px", height: "40px"}}><img
                                        src={item.attachment ? "http://localhost:8081/api/file/" + item.attachment : ''} alt=""
                                        className="img-fluid"/></div>
                                </td>
                                <td><Button color="info" onClick={() => editRoom(item)}
                                            className="mr-5">Edit</Button>
                                    <Button color="danger"
                                            onClick={() => deleteRoom(item.id)}>Delete</Button></td>
                                {/*<td><input type="checkbox" checked={item.active}/></td>*/}
                                {/*<td>*/}
                                {/*    <Button outline onClick={() => editPayType(item) } color="warning">Edit</Button>*/}
                                {/*    <Button className="ml-3" color="info"*/}
                                {/*            onClick={() => changeEnabled(item.id, item.active ? false : true)}>{item.active ? "Bloklash" : "Aktivlashtirish"}</Button>*/}

                                {/*    <Button outline onClick={() => deletePayType(item.id) } className="ml-2" color="danger">Delete</Button>*/}
                                {/*</td>*/}
                            </tr>
                        ) : ''}
                        </tbody>
                    </Table>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Pagination
                        activePage={activePage}
                        itemsCountPerPage={10}
                        totalItemsCount={totalElement}
                        pageRangeDisplayed={5}
                        onChange={handlePageChange.bind(this)} itemClass="page-item"
                        linkClass="page-link"
                    />
                </Col>
            </Row>
            <Modal isOpen={addRoomModal} fade={false} toggle={addRoom}>
                <ModalHeader>{currentRoom ? "Xonani o'zgartirish " : "Yangi Xona qo'shish"}</ModalHeader>
                <AvForm onValidSubmit={saveOrEditRoom}>
                    <ModalBody>
                        <Row>
                            <Col>
                                <AvField required={true} type="text"
                                         label="number" className="mt-2"
                                         placeholder="number"
                                         defaultValue={currentRoom.number ? currentRoom.number : ''}
                                         name="number"/>
                                <AvField required={true} type="text"
                                         label="Izoh Uz" placeholder="Izoh Uz"
                                         name="descriptionUz" defaultValue={currentRoom ? currentRoom.descriptionUz : ''}/>
                                <AvField required={true} type="text"
                                         label="Izoh Ru" placeholder="Izoh Ru"
                                         name="descriptionRu" defaultValue={currentRoom ? currentRoom.descriptionRu : ''}/>
                                <AvField required={true} type="number"
                                         label="price" placeholder="price"
                                         name="price" defaultValue={currentRoom ? currentRoom.price : ''}/>
                                <div>
                                    <input type="file" className="form-control" placeholder="Rasm tanlang" onChange={getPhotoId}/>
                                    <div style={{width: "200px", height: "200px"}}><img
                                        src={tempFileId ? "http://localhost:8081/api/file/" + tempFileId : ''} alt=""
                                        className="img-fluid"/></div>
                                </div>
                            </Col>
                        </Row>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={addRoom}>Bekor qilish</Button>
                        <Button color="primary" type="submit">Saqlash</Button>
                    </ModalFooter>
                </AvForm>
            </Modal>
            <Modal isOpen={showDeleteModal} toggle={toggleRoomDeleteModal}>
                <ModalHeader>Xonani o'chirishni istaysizmi?</ModalHeader>

                <ModalBody>

                </ModalBody>
                <ModalFooter>
                    <Button color="info" onClick={toggleRoomDeleteModal}>Bekor qilish</Button>
                    <Button className="ml-3" color="danger" onClick={roomDeleteYes}
                            type="button">O'chirish</Button>
                </ModalFooter>
            </Modal>
        </Container>
    </div>
)

}

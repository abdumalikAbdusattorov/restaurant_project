import React, {useEffect, useState} from 'react';
/*import {useHistory} from "react-router-dom";*/
import {ProSidebar, Menu, MenuItem} from 'react-pro-sidebar';
import axios from "axios";
import 'react-pro-sidebar/dist/css/styles.css';
import {Link} from 'react-router-dom';
import {
    BrowserRouter as
        Router,
    Switch,
    Route,
} from "react-router-dom";
import {
    Collapse,
    Container,
    Button,
    Nav,
    Row,
    Navbar,
    Col,
    NavbarBrand,
    NavbarToggler,
    NavItem,
    NavLink,
    Modal, ModalHeader, ModalBody, ModalFooter, Table
} from "reactstrap";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import NotFount from "./NotFound";
import {AvField, AvForm} from "availity-reactstrap-validation";

export default function AdminHomePage() {
    /* useEffect(() => {
         if (!stopEffect) {
                 localStorage.getItem('OnlineToken');
             axios.get('http://localhost/api/user/me', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                 .then(res => {
                     // console.log(res,"me ==========");
                     if (res.data.object != null) {
                         res.data.object.roles.map(role => {
                             if (role.name === 'ROLE_ADMIN') {
                                 history.push("/adminPage");
                                 setAdminMe(true);
                             }
                         })
                     }
                 });
         }
     }, []);*/

    /*   const history = useHistory();
       const [stopEffect, setStopEffect] = useState(false);
       const [adminMe, setAdminMe] = useState(false);*/
    useEffect(() => {
        if (!stopEffect) {
            axios.get('http://localhost:8081/api/companyInfo/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res,"asijdasd=================");
                    setCompanyInfoArrayArray(res.data.object);
                    setStopEffect(true);


                })
        }
    }, []);



    const [isOpen, setIsOpen] = useState(false);
    const [addCompanyInfoModal, setAddCompanyInfoModal] = useState(false);
    const [currentCompanyInfo, setCurrentCompanyInfo] = useState('');
    const [tempPhotoId, setTempPhotoId] = useState('');
    const [tempCompanyInfoId, setTempCompanyInfoId] = useState('');
    const [modal, setModal] = useState(false);
    const [companyInfoArray, setCompanyInfoArrayArray] = useState([]);
    const [stopEffect, setStopEffect] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);


    const toggle = () => setIsOpen(!isOpen);

    const addCompanyInfo=()=>{
        setAddCompanyInfoModal(!addCompanyInfoModal);
        setCurrentCompanyInfo('');
        setTempPhotoId('');
    };
    const saveOrEditCompanyInfo=(e,v)=>{
         console.log(v,"SaveOrEdit")
        if (currentCompanyInfo){
            v={...v,id:currentCompanyInfo.id}
        }
        if (tempPhotoId){
            v={...v,attachment:tempPhotoId,botActive:true,deliveryActive:true,percentAndSom:true}
        }
        axios.post('http://localhost:8081/api/companyInfo',v,{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res=>{
                setModal(!modal);
                axios.get('http://localhost:8081/api/companyInfo/all',{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res=>{
                        setCompanyInfoArrayArray(res.data.object);
                    })
                setAddCompanyInfoModal(!addCompanyInfoModal);
            })
    }

    const getPhotoId = (e) => {
        const formData = new FormData();

        formData.append('file', e.target.files[0]);
        formData.append("type", e.target.name);

        axios.post('http://localhost:8081/api/file',formData,{headers: {"Authorization": localStorage.getItem('MilliyToken'),"Content-Type": "multipart/form-data"}})
            .then(res => {
                console.log(res);
                setTempPhotoId(res.data.object[0].fileId)
                // setPayTypeArray(res.data.object);
                // setStopEffect(true);
            })
    };
    const editCompanyInfo=(item)=>{
        setAddCompanyInfoModal(!addCompanyInfoModal);
        setCurrentCompanyInfo(item);
        setTempPhotoId(item.attachment);
    };

    const deleteCompanyInfo=(id)=>{
        setTempCompanyInfoId(id);
        setShowDeleteModal(!showDeleteModal);
    };

    const toggleCompanyInfoDeleteModal = () => {
        setTempCompanyInfoId([]);
        setShowDeleteModal(!showDeleteModal);
    };

    const companyInfoDeleteYes=()=>{
        if (tempCompanyInfoId){
            axios.delete('http://localhost:8081/api/companyInfo/'+tempCompanyInfoId,{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res=>{
                    setShowDeleteModal(!showDeleteModal);
                    axios.get('http://localhost:8081/api/companyInfo/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            //console.log(res,"asijdasd=================");
                            setCompanyInfoArrayArray(res.data.object);
                        })
                })
        }
    };

    const changeEnabled=(id)=>{
        axios.get('http://localhost:8081/api/companyInfo/blockBot?id='+id,{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res=>{
                axios.get('http://localhost:8081/api/companyInfo/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        //console.log(res,"asijdasd=================");
                        setCompanyInfoArrayArray(res.data.object);
                    })
            })
    };

    const changeEnabledDelivery=(id)=>{
        axios.get('http://localhost:8081/api/companyInfo/blockDelivery?id='+id,{headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res=>{
                axios.get('http://localhost:8081/api/companyInfo/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        //console.log(res,"asijdasd=================");
                        setCompanyInfoArrayArray(res.data.object);
                    })
            })
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
                    <Col><h2>Korxona malumotlari</h2></Col>
                </Row>
                <Row>
                    <Col>
                        <Button className="btn btn-success" onClick={addCompanyInfo}>Korxona malumotlarini kiritish</Button>

                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Table>
                            <thead>
                            <tr>
                                <th>№</th>
                                <th>Korxona nomi</th>
                                <th>Izoh uz</th>
                                <th>Izoh ru</th>
                                <th>Yetkazib berish narxi</th>
                                <th>Rasm</th>
                                <th>Bot holati</th>
                                <th>Yetkazib berish holati</th>
                                <th>O'zgartirish</th>
                                <th>O'chirish</th>
                                <th>Bot bloklash</th>
                                <th>Yetkazib berish bloklash</th>
                            </tr>
                            </thead>
                            <tbody>
                            {companyInfoArray ? companyInfoArray.map((item, index) =>
                                <tr>
                                    <td>{index+1}</td>
                                    <td>{item.name}</td>
                                    <td>{item.descriptionUz}</td>
                                    <td>{item.descriptionRu}</td>
                                    <td>{item.deliveryPrice}</td>

                                    <td>
                                        <div style={{width: "40px", height: "40px"}}><img
                                            src={item.attachment ? "http://localhost:8081/api/file/" + item.attachment : ''} alt=""
                                            className="img-fluid"/></div>
                                    </td>
                                    <td><input type="checkbox" checked={item.botActive}/></td>
                                    <td><input type="checkbox" checked={item.deliveryActive}/></td>
                                    <td><Button color="warning" onClick={() => editCompanyInfo(item)}
                                                className="mr-0">Edit</Button>
                                        </td>
                                    <td><Button color="danger" className="ml-2"
                                                onClick={() => deleteCompanyInfo(item.id)}>Delete</Button></td>
                                    <td> <Button className="ml-3" color="info"
                                                 onClick={() => changeEnabled(item.id)}>{item.botActive ? "Bloklash" : "Aktivlashtirish"}</Button></td>
                                    <td> <Button className="ml-3" color="primary"
                                                 onClick={() => changeEnabledDelivery(item.id)}>{item.deliveryActive ? "Bloklash" : "Aktivlashtirish"}</Button></td>
                                </tr>
                            ) : ''}
                            </tbody>
                        </Table>
                    </Col>
                </Row>
            </Container>
            <Modal isOpen={addCompanyInfoModal} fade={false} toggle={addCompanyInfo}>
                <ModalHeader>{currentCompanyInfo ? "Korxona malumotlarini o'zgartirish " : "Yangi korxona malumotlarini qo'shish"}</ModalHeader>
                <AvForm onValidSubmit={saveOrEditCompanyInfo}>
                    <ModalBody>
                        <Row>
                            <Col>
                                <div>
                                    <input type="file" className="form-control" placeholder="Rasm tanlang" onChange={getPhotoId}/>
                                    <div style={{width: "200px", height: "200px"}}><img
                                        src={tempPhotoId ? "http://localhost:8081/api/file/" + tempPhotoId : ''} alt=""
                                        className="img-fluid"/></div>
                                </div>
                                <AvField required={true} type="text"
                                         label="Nomi" className="mt-2"
                                         placeholder="Nomi"
                                         defaultValue={currentCompanyInfo ? currentCompanyInfo.name : ''}
                                         name="name"/>
                                <AvField required={true} type="text"
                                         label="Izoh Uz" className="mt-2"
                                         placeholder="Izoh Uz"
                                         defaultValue={currentCompanyInfo ? currentCompanyInfo.descriptionUz : ''}
                                         name="descriptionUz"/>

                                <AvField required={true} type="text"
                                         label="Izoh Ru" placeholder="Izoh Ru"
                                         defaultValue={currentCompanyInfo ? currentCompanyInfo.descriptionRu : ''}
                                         name="descriptionRu"/>
                                <AvField required={true} type="number"
                                         label="Yetkazib berish narxi" placeholder="Yetkazib berish narxi"
                                         defaultValue={currentCompanyInfo ? currentCompanyInfo.deliveryPrice : ''}
                                         name="deliveryPrice"/>
                            </Col>
                        </Row>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={addCompanyInfo}>Bekor qilish</Button>
                        <Button color="primary" type="submit">Saqlash</Button>
                    </ModalFooter>
                </AvForm>
            </Modal>

            <Modal isOpen={showDeleteModal} toggle={toggleCompanyInfoDeleteModal}>
                <ModalHeader>Xonani o'chirishni istaysizmi?</ModalHeader>

                <ModalBody>

                </ModalBody>
                <ModalFooter>
                    <Button color="info" onClick={toggleCompanyInfoDeleteModal}>Bekor qilish</Button>
                    <Button className="ml-3" color="danger" onClick={companyInfoDeleteYes}
                            type="button">O'chirish</Button>
                </ModalFooter>
            </Modal>
        </div>
    );
}
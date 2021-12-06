import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import {
    Button,
    Col,
    Card,
    CardBody,
    CardTitle,
    CardText,
    Collapse,
    Container,
    Nav,
    TabContent, TabPane,
    Navbar,
    NavItem,
    NavLink,
    Row,
    Table, Modal, ModalHeader, ModalBody, ModalFooter
} from "reactstrap";
import classnames from 'classnames';
import axios from "axios";
import {AvField, AvForm} from "availity-reactstrap-validation";

export default function Orders() {
    useEffect(() => {
        if (!stopEffect) {
            axios.get('http://localhost:8081/api/order/byORderStatus?page=0&size=2', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "Product array")
                    setOrderArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setOrderStatus("NEW");
                })
            axios.get('http://localhost:8081/api/order/byORderStatus?page=0&size=2', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "Product array")
                    setOrderArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setOrderStatus("APPROVED");
                })
            axios.get('http://localhost:8081/api/order/byORderStatus?page=0&size=2', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "Product array")
                    setOrderArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setOrderStatus("SEND");
                })
            axios.get('http://localhost:8081/api/order/byORderStatus?page=0&size=2', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "Product array")
                    setOrderArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setOrderStatus("RECIEVED");
                })
            axios.get('http://localhost:8081/api/payType/getAll', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    setPayTypeArray(res.data.object);
                })
            axios.get('http://localhost:8081/api/category/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    // console.log(res, '======================');
                    setAllCategories(res.data.object);


                })
            setStopEffect(true)
        }
    }, []);

    const [isOpen, setIsOpen] = useState(false);
    const [allNewOrders, setAllNewOrders] = useState([]);
    const [allApprovedOrders, setAllApprovedOrders] = useState([]);
    const [allSendOrders, setAllSendOrders] = useState([]);
    const [allRecievedOrders, setAllRecievedOrders] = useState([]);
    const [newTotal, setNewTotal] = useState(0);
    const [approvedTotal, setApprovedTotal] = useState(0);
    const [sendTotal, setSendTotal] = useState(0);
    const [recievedTotal, setRecievedTotal] = useState(0);
    const [stopEffect, setStopEffect] = useState(false);
    const [orderArray, setOrderArray] = useState([]);
    const [productArray, setProductArray] = useState([]);
    const [allCategories, setAllCategories] = useState([]);
    const [totalElement, setTotalElement] = useState(0);
    const [orderStatus, setOrderStatus] = useState('');
    const [tempNewOrderPage, setTempNewOrderPage] = useState(1);
    const [tempApprovedOrderPage, setTempApprovedOrderPage] = useState(1);
    const [tempSendOrderPage, setTempSendOrderPage] = useState(1);
    const [tempRecievedOrderPage, setTempRecievedOrderPage] = useState(1);
    const [activeTab, setActiveTab] = useState(1);
    const [addOrderModal, setAddOrderModal] = useState(false);
    const [currentOrder, setCurrentOrder] = useState('');
    const [tempFileId, setTempFileId] = useState('');
    const [modal, setModal] = useState(false);
    const [tempCatId, setTempCatId] = useState('');
    const [payTypeArray, setPayTypeArray] = useState([]);
    const [reqProductWithAmountList, setReqProductWithAmountList] = useState([
        {
            catId: '',
            productList: [],
            productId: '',
            amount: '',
            canceledAmount: ''
        }
    ]);


    const addOrderProduct = () => {
        setAddOrderModal(!addOrderModal);
        setCurrentOrder('');

    };

    const getNext = (status) => {
        let s = 0;
        if (status === 'NEW') {
            s = tempNewOrderPage;
            setTempNewOrderPage(tempNewOrderPage + 1);
        }
        if (status === 'APPROVED') {
            s = tempApprovedOrderPage;
            setTempApprovedOrderPage(tempApprovedOrderPage + 1);
        }
        if (status === 'SEND') {
            s = tempSendOrderPage;
            setTempSendOrderPage(tempSendOrderPage + 1);
        }
        if (status === 'RECIEVED') {
            s = tempRecievedOrderPage;
            setTempRecievedOrderPage(tempRecievedOrderPage + 1)
        }
        axios.get('http://localhost:8081/api/order/byORderStatus?page=0&size=2', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                console.log(res, "Product array")
                setOrderArray(res.data.object);
                setTotalElement(res.data.totalElements);
                setOrderStatus(status);
            })

    };
    const toggle = (tab) => {
        if (activeTab !== tab) {
            setActiveTab(tab)
        }

    }
    const saveOrEditOrder = (e, v) => {
        console.log(v, "SaveOrEdit")
        let reqProductWithAmountList = [];
        Object.keys(v).forEach(inputName => {
            if (inputName.split('/')[0] === 'productId') {
                reqProductWithAmountList[inputName.split('/')[1]] = {
                    productId: v[inputName],
                    amount: v['amount/' + inputName.split('/')[1]]
                }
            }
        });
        var filtered = reqProductWithAmountList.filter(Boolean);
        let s = {
            clientPhoneNumber: v.clientPhoneNumber,
            clientFirstName: v.clientFirstName,
            clientLastName: v.clientLastName,
            orderAdress: v.orderAdress,
            reqProductWithAmountList: filtered,
            payStatus: 'UNPAID',
            orderStatus: 'APPROVED',
            payTypeId: v.payTypeId,
            date:v.date
            // time:v.time
        }
        if (currentOrder) {
            s = {...s, id: currentOrder.id}
        }
        console.log(s, "S====Values")
        axios.post('http://localhost:8081/api/order', s, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setModal(!modal);
                axios.get('http://localhost:8081/api/order/byOrderStatus?page=0&size=10&orderStatus=' + "APPROVED", {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setOrderArray(res.data.object);
                    })
                setAddOrderModal(!addOrderModal);
            })

    };

    const getCatId = (e, index) => {
        let tempProductWithAmountArray = reqProductWithAmountList
        let tempCategoryId = e.target.value
        axios.get('http://localhost:8081/api/product/byCatId?catId=' + tempCategoryId, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                tempProductWithAmountArray[index].productList = [...res.data.object]
                tempProductWithAmountArray[index].catId = tempCategoryId
                setReqProductWithAmountList([...tempProductWithAmountArray])
            })
    };

    const addNewRow = () => {
        let s = reqProductWithAmountList;
        s.push({
            catId: '',
            productList: [],
            productId: '',
            amount: '',
            canceledAmount: ''
        })
        setReqProductWithAmountList([...s])
    };

    const getProductId = (e, index) => {
        let tempArray = reqProductWithAmountList
        tempArray[index].productId = e.target.value
        setReqProductWithAmountList([...tempArray])
    };

    const getAmount = (e, index) => {
        let tempProductWithAmountArray = reqProductWithAmountList;
        tempProductWithAmountArray[index].amount = e.target.value;
        setReqProductWithAmountList([...tempProductWithAmountArray]);
    };

    const deleteItem1 = (index) => {
        console.log(reqProductWithAmountList, "REQARR")
        console.log(index, "INDEX")
        let tempProductWithAmountArray = reqProductWithAmountList;
        tempProductWithAmountArray.splice(index, 1);
        console.log(tempProductWithAmountArray, "TEMPARR")
        setReqProductWithAmountList([...tempProductWithAmountArray])
        console.log(reqProductWithAmountList, "REQARR")
    };

    // const removeFunction=(item)=>{
    //      let data = {reqProductWithAmountList}; //Duplicate state.
    //      delete data[item];                  //remove Item form stateCopy.
    //      setReqProductWithAmountList({data});             //Set state as the modify one.
    //  }

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
                    <Col><h2>Buyurtmalar </h2></Col>
                </Row>
                <Nav tabs>
                    <NavItem>
                        <NavLink
                            className={classnames({active: activeTab === '1'})}
                            onClick={() => {
                                toggle('1')
                            }
                            }
                        >
                            Mahsulot buyurtma
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink
                            className={classnames({active: activeTab === '2'})}
                            onClick={() => {
                                toggle('2')
                            }}>
                            Xona buyurtma
                        </NavLink>
                    </NavItem>
                </Nav>
                <TabContent activeTab={activeTab}>
                    <TabPane tabId="1">
                        <Row>
                            <Col>
                                <Button className=" mt-3" color="success" onClick={addOrderProduct}
                                        style={{borderRadius: "20px"}}>Mahsulot buyurtma qo'shish</Button>
                            </Col>
                        </Row>
                        <Row className="mt-3">
                            <Col>
                                <h3>Yangi buyurtmalar</h3>
                                {allNewOrders ? allNewOrders.map((item, index) =>
                                    <Card>
                                        <CardBody>
                                            <CardTitle>
                                                <span>{item.createdAt.substr(0, 10)}</span><br/>
                                                <span>{item.user.firstName ? item.user.firstName : ''}</span>
                                                <span>{item.user.lastName ? item.user.lastName : ''}</span>
                                                <span>{item.user.phoneNumber ? item.user.phoneNumber : ''}</span>
                                            </CardTitle>
                                            <CardText>
                                                {item.resProductWithAmountList ? item.resProductWithAmountList.map((item, index) =>
                                                    <div>
                                                        <span>{index + 1}</span>
                                                        <span>{item.resProduct.nameUz}</span>
                                                        <span>{item.amount}</span>
                                                    </div>
                                                ) : ''}
                                                <div className="mt-2">
                                            <span>Address:{item.orderAddress ? item.orderAddress :
                                                <Button type="button" color="info"
                                                >Location</Button>}</span>
                                                </div>
                                            </CardText>
                                            <Button type="button" color="success">Tasdiqlash</Button>
                                        </CardBody>
                                    </Card>
                                ) : <h4>Buyurtmalar bo'sh</h4>}
                                {allNewOrders.length < newTotal ?
                                    <div>
                                        <Button className="ml-5" type="button" color="warning"
                                                onClick={() => getNext('NEW')}>Yana
                                            ko'rish</Button>
                                    </div>
                                    : ''
                                }
                            </Col>
                        </Row>
                    </TabPane>
                    <TabPane tabId="2">
                        <Row>
                            <Col sm="6">
                                <Button className=" mt-3" color="success" style={{borderRadius: "20px"}}>Xona buyurtma
                                    qo'shish</Button>

                            </Col>
                        </Row>
                    </TabPane>
                </TabContent>
            </Container>
            <Modal style={{maxWidth: '1600px', width: '80%'}} isOpen={addOrderModal} fade={false}
                   toggle={addOrderProduct}>
                <ModalHeader>{currentOrder ? "Buyurtma o'zgartirish " : "Yangi buyurtma qo'shish"}</ModalHeader>
                <AvForm onValidSubmit={saveOrEditOrder}>
                    <ModalBody>
                        <Row>
                            <Col>
                                <Row>
                                    <Col md={{size: 2}}>
                                        <AvField required={true} type="text"
                                                 defaultValue={currentOrder ? currentOrder.firstName : ''}
                                                 className="mt-2" placeholder="Enter Firstname "
                                                 name="clientFirstName"/>
                                    </Col>
                                    <Col md={{size: 2}}>
                                        <AvField required={true} type="text"
                                                 defaultValue={currentOrder ? currentOrder.lastName : ''}
                                                 className="mt-2" placeholder="Enter Lastname "
                                                 name="clientLastName"/>
                                    </Col>
                                    <Col md={{size: 2}}>
                                        <AvField required={true} type="text"
                                                 defaultValue={currentOrder ? currentOrder.clientPhoneNumber : ''}
                                                 className="mt-2" placeholder="Enter PhoneNumber "
                                                 name="clientPhoneNumber"/>
                                    </Col>
                                    <Col md={{size: 2}}>
                                        <AvField required={true} type="text"
                                                 defaultValue={currentOrder ? currentOrder.orderAdress : ''}
                                                 className="mt-2" placeholder="Enter Address"
                                                 name="orderAdress"/>
                                    </Col>
                                    <Col md={{size: 2}}>
                                        <AvField required={true} type="select"
                                                 className="mt-2" placeholder="To'lov turini tanlang"
                                                 name="payTypeId">
                                            <option>To'lov turini tanlang</option>
                                            {payTypeArray ? payTypeArray.map(item =>
                                                <option value={item.id}>{item.nameUz}</option>
                                            ) : ''}
                                        </AvField>

                                    </Col>

                                </Row>
                                <Row>
                                    <Col>
                                        <AvField required={true} type="date"
                                                 defaultValue={currentOrder ? currentOrder.date : ''}
                                                 className="mt-2" placeholder="Sanani tanlang"
                                                 name="date"/>
                                    </Col>
                                    {/*<Col>*/}
                                    {/*    <AvField required={true} type="time"*/}
                                    {/*             defaultValue={currentOrder ? currentOrder.time : ''}*/}
                                    {/*             className="mt-2" placeholder="Vaqtni tanlang"*/}
                                    {/*             name="time"/>*/}
                                    {/*</Col>*/}
                                </Row>
                                <Row>
                                    <Col>
                                        <h5>Mahsulot malumotlarini kiriting</h5>
                                    </Col>
                                </Row>
                                {console.log(reqProductWithAmountList, "==================================")}
                                {reqProductWithAmountList ? reqProductWithAmountList.map((item, index) =>
                                    <Row key={index}>
                                        <Col>
                                            <AvField type="select" className="mt-2" placeholder="Kategoriya tanlang"
                                                     name={`catId/${index}`}
                                                     value={item.catId ? item.catId : ''}
                                                     onChange={(e) => getCatId(e, index)}>
                                                <option>Kategoriya tanlang</option>
                                                {allCategories ? allCategories.map(item1 =>
                                                    <option value={item1.id}>{item1.nameUz}</option>
                                                ) : ''}
                                            </AvField>
                                        </Col>
                                        <Col>
                                            {console.log(item, "ITEM+++++++++++++")}
                                            <AvField type="select" className="mt-2" placeholder="Maxsulot tanlang"
                                                     value={item.productId ? item.productId : ''}
                                                     name={`productId/${index}`}
                                                     onChange={(e) => getProductId(e, index)}>
                                                <option>Mahsulot tanlang</option>

                                                {item.productList ? item.productList.map(item2 =>
                                                    <option value={item2.id}>{item2.nameUz}</option>
                                                ) : ''}
                                            </AvField>
                                        </Col>
                                        <Col>
                                            <AvField required={true} type="number"
                                                     className="mt-2"
                                                     value={item.amount ? item.amount : ''}
                                                     placeholder="Soni" name={`amount/${index}`}
                                                     onChange={(e) => getAmount(e, index)}/>
                                        </Col>
                                        <Col>
                                            <Button className="ml-5" color="danger" style={{borderRadius: "20px"}}
                                                    onClick={() => deleteItem1(index)}>-</Button>
                                        </Col>
                                    </Row>
                                ) : ''}
                                <Row>
                                    <Col md={{size: 2, offset: 5}}>
                                        <Button type="button" className="ml-5" color="info"
                                                style={{borderRadius: "20px"}}
                                                onClick={addNewRow}>+</Button>
                                    </Col>
                                </Row>

                            </Col>
                        </Row>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={addOrderProduct}>Bekor qilish</Button>
                        <Button className="ml-3" color="success" type="submit">Saqlash</Button>
                    </ModalFooter>
                </AvForm>
            </Modal>

        </div>
    )
}
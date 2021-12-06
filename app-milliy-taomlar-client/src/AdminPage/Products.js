import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import {
    Button,
    Col,
    Collapse,
    Container,
    Modal,
    ModalBody, ModalFooter,
    ModalHeader,
    Nav,
    Navbar,
    Input,
    NavItem,
    Table,
    NavLink,
    Label,
    Row
} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {TreeSelect} from 'antd';
import axios from "axios";
import Pagination from "react-js-pagination";

export default function Products() {
    useEffect(() => {
        if (!stopEffect) {
            axios.get('http://localhost:8081/api/product/search?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "Product array")
                    setProductArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                })
            axios.get('http://localhost:8081/api/category/all', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    //console.log(res, '======================');
                    setCategoryArray(res.data.object);
                })
            setStopEffect(true)
        }
    }, []);


    const [productArray, setProductArray] = useState([]);
    const [categoryArray, setCategoryArray] = useState([]);
    const [stopEffect, setStopEffect] = useState(false);
    const [addProductModal, setAddProductModal] = useState(false);
    const [currentProduct, setCurrentProduct] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const toggle = () => setIsOpen(!isOpen);
    const [modal, setModal] = useState(false);
    const [value, setValue] = useState('');
    const {TreeNode} = TreeSelect;
    const [tempFileId, setTempFileId] = useState('')
    const [currentCategory, setCurrentCategory] = useState('')
    const [totalElement, setTotalElement] = useState(0);
    const [activePage, setActivePage] = useState(1);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [tempProductId, setTempProductId] = useState('');
    const [search, setSearch] = useState('');


    const onChange = value => {
        //console.log(value, "Value Category");
        setValue({value});
        setCurrentCategory(value);
    };
    const data = (id) => categoryArray && categoryArray.filter(fitem => fitem.parentId === id).map(item => {
        return <TreeNode value={item.id} title={item.nameUz} key={item.id}>
            {data(item.id)}
        </TreeNode>
    })

    const addProduct = () => {
        setAddProductModal(!addProductModal);
        setCurrentProduct('');
        setCurrentCategory('');
        setValue('');
        setTempFileId('');
    };
    const getPhotoId = (e) => {
        const formData = new FormData();

        formData.append('file', e.target.files[0]);
        formData.append("type", e.target.name);

        axios.post('http://localhost:8081/api/file', formData, {
            headers: {
                "Authorization": localStorage.getItem('MilliyToken'),
                "Content-Type": "multipart/form-data"
            }
        })
            .then(res => {
                console.log(res);
                setTempFileId(res.data.object[0].fileId)
                // setPayTypeArray(res.data.object);
                // setStopEffect(true);
            })
    };

    const saveOrEditProduct = (e, v) => {
        console.log(v, "SaveOrEdit")
        if (currentProduct) {
            v = {...v, id: currentProduct.id}
        }
        if (currentCategory) {
            v = {...v, categoryId: currentCategory}
        }
        if (tempFileId) {
            v = {...v, photoId: tempFileId}
        }

        axios.post('http://localhost:8081/api/product', v, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setModal(!modal);
                axios.get('http://localhost:8081/api/product/search', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setProductArray(res.data.object);
                    })
                setAddProductModal(!addProductModal);
            })
    }

    const editProduct = (item) => {
        console.log(item, "EditProduct");
        setAddProductModal(!addProductModal);
        setCurrentProduct(item);
        setCurrentCategory(item.resCategory.id);
        setValue(item.resCategory.id);
        setTempFileId(item.photoId)
    }

    const changeEnabled = (id,active) => {
        axios.get('http://localhost:8081/api/product/changeActive?id=' + id+'&active='+active, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                axios.get('http://localhost:8081/api/product/search', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        // console.log(res, "Product array")
                        setProductArray(res.data.object);
                        setTotalElement(res.data.totalElements)
                    })
            })
    };

    const deleteProduct = (id) => {
        setTempProductId(id);
        setShowDeleteModal(!showDeleteModal);
    };
    const productDeleteYes = () => {
        if (tempProductId) {
            axios.delete('http://localhost:8081/api/product/' + tempProductId, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    setShowDeleteModal(!showDeleteModal);

                    axios.get('http://localhost:8081/api/product/search?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            console.log(res, "Product array")
                            setProductArray(res.data.object);
                            setTotalElement(res.data.totalElements);
                        })

                })
        }
    };

    const toggleProductDeleteModal = () => {
        setTempProductId('');
        setShowDeleteModal(!showDeleteModal);
    };

    const productSearch = (e) => {
        console.log(e.target.value, "ValueSearch");
        let search = e.target.value;
        if (search) {
            axios.get('http://localhost:8081/api/product/search?page=0&size=10&search=' + search, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, "mana search");
                    setProductArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setSearch(res.data.resProducts);
                })
        } else {
            axios.get('http://localhost:8081/api/product/search?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    setProductArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                })
        }
    };

    const handlePageChange = (pageNumber) => {
        setActivePage(pageNumber);
        let page = pageNumber - 1;
        axios.get('http://localhost:8081/api/product/search?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setProductArray(res.data.object);
                setTotalElement(res.data.totalElements);
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
                    <Col><h2>Mahsulotlar</h2></Col>
                </Row>
                <Row>
                    <Col>
                        <Button className="btn btn-success" onClick={addProduct}>Mahsulot qo'shish</Button>
                    </Col>
                    <Col>
                        <Input type="text" placeholder="search" onChange={productSearch}/>
                    </Col>
                </Row>
                <Row className="mt-2">
                    <Col>
                        <Table>
                            <thead>
                            <tr>
                                <th>№</th>
                                <th>Nomi Uz</th>
                                <th>Nomi Ru</th>
                                <th>Izoh Uz</th>
                                <th>Izoh Ru</th>
                                <th>Narx</th>
                                <th>Kategoriya</th>
                                <th>Aktiv</th>
                                <th>O'zgartirish</th>
                                <th>Holati</th>
                                <th>O'chirish</th>
                            </tr>
                            </thead>
                            <tbody>
                            {productArray ? productArray.map((item, index) =>
                                <tr>
                                    <td>{(activePage * 10) + index + 1 - 10}</td>
                                    <td>{item.nameUz}</td>
                                    <td>{item.nameRu}</td>
                                    <td>{item.descriptionUz}</td>
                                    <td>{item.descriptionRu}</td>
                                    <td>{item.price}</td>
                                    <td>{item.resCategory.nameUz}</td>
                                    <td><input type="checkbox" checked={item.active}/></td>
                                    {console.log(item,"table itemmm")}
                                    <td>
                                        <Button onClick={() => editProduct(item)} color="warning">Edit</Button>


                                    </td>
                                    <td><Button className="ml-3" color="info"
                                                onClick={() => changeEnabled(item.id, item.active ? false : true)}>{item.active ? "Bloklash" : "Aktivlashtirish"}</Button>
                                    </td>
                                    <td><Button outline onClick={() => deleteProduct(item.id)} className="ml-2"
                                                color="danger">Delete</Button></td>
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
            </Container>
            <Modal isOpen={addProductModal} fade={false} toggle={addProduct}>
                <ModalHeader>{currentProduct ? "Mahsulotni o'zgartirish " : "Yangi mahsulot qo'shish"}</ModalHeader>
                <AvForm onValidSubmit={saveOrEditProduct}>
                    <ModalBody>
                        <Row>
                            <Col>
                                <AvField required={true} type="text"
                                         label="Nomi Uz" className="mt-2"
                                         placeholder="Nomi Uz"
                                         defaultValue={currentProduct.nameUz ? currentProduct.nameUz : ''}
                                         name="nameUz"/>
                                <AvField required={true} type="text"
                                         label="Nomi Ru" className="mt-2"
                                         placeholder="Nomi Ru"
                                         defaultValue={currentProduct.nameRu ? currentProduct.nameRu : ''}
                                         name="nameRu"/>
                                <AvField required={true} type="text"
                                         label="Izoh Uz" className="mt-2"
                                         placeholder="Izoh Uz"
                                         defaultValue={currentProduct.descriptionUz ? currentProduct.descriptionUz : ''}
                                         name="descriptionUz"/>
                                <AvField required={true} type="text"
                                         label="Izoh Ru" className="mt-2"
                                         placeholder="Izoh Ru"
                                         defaultValue={currentProduct.descriptionRu ? currentProduct.descriptionRu : ''}
                                         name="descriptionRu"/>
                                <AvField required={true} type="number"
                                         label="Narxi" className="mt-2"
                                         placeholder="Narxi"
                                         defaultValue={currentProduct.price ? currentProduct.price : ''}
                                         name="price"/>

                                <Label>Kategoriya tanlang:</Label>
                                <TreeSelect
                                    showSearch
                                    style={{width: '100%'}}
                                    value={value}
                                    dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
                                    placeholder="Please select"
                                    allowClear
                                    treeDefaultExpandAll
                                    onChange={onChange}
                                >
                                    {data(null)}
                                </TreeSelect>
                                <Label>Rasm yuklang:</Label>
                                <div>
                                    <input type="file" className="form-control" placeholder="Rasm tanlang"
                                           onChange={getPhotoId}/>
                                    <div style={{width: "200px", height: "200px"}}><img
                                        src={tempFileId ? "http://localhost:8081/api/file/" + tempFileId : ''} alt=""
                                        className="img-fluid"/></div>
                                </div>
                            </Col>
                        </Row>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={addProduct}>Bekor qilish</Button>
                        <Button color="primary" type="submit">Saqlash</Button>
                    </ModalFooter>
                </AvForm>
            </Modal>
            <Modal isOpen={showDeleteModal} toggle={toggleProductDeleteModal}>
                <ModalHeader>Xonani o'chirishni istaysizmi?</ModalHeader>

                <ModalBody>

                </ModalBody>
                <ModalFooter>
                    <Button color="info" onClick={toggleProductDeleteModal}>Bekor qilish</Button>
                    <Button className="ml-3" color="danger" onClick={productDeleteYes}
                            type="button">O'chirish</Button>
                </ModalFooter>
            </Modal>
        </div>
    )
}
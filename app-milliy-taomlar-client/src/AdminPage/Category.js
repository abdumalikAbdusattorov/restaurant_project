import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {
    Button,
    Col,
    Collapse,
    Container,
    Modal, ModalBody, ModalFooter,
    ModalHeader,
    Nav,
    Navbar,
    NavItem,
    NavLink,
    Row,
    Table
} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import axios from "axios";
import {TreeSelect} from 'antd';
import {object} from "prop-types";
import Pagination from "react-js-pagination";
import {Picker} from "emoji-mart";
import "emoji-mart/css/emoji-mart.css";

export default function Category() {
    useEffect(() => {
        if (!stopEffect) {
            axios.get('http://localhost:8081/api/category/getAllCategoriesByPageable?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    console.log(res, '======================');
                    setCategoryArray(res.data.object);
                    setTotalElement(res.data.totalElements);
                    setStopEffect(true);
                })
        }
    }, []);
    const [emojiPickerState, setEmojiPicker] = useState(false);
    const [tempIcon, setTempIcon] = useState(''); /////////////////////////////

    let emojiPicker;
    if (emojiPickerState) {
        emojiPicker = (
            <Picker
                title="Pick your emoji…"
                emoji="point_up"
                onSelect={emoji => chooseIcon(emoji.native)}
            />
        );
    }
    const chooseIcon = (icon) => {
        setTempIcon(icon)
        setEmojiPicker(!emojiPickerState)
    }

    function triggerPicker(event) {
        event.preventDefault();
        setEmojiPicker(!emojiPickerState);
    }

    const onChange = value => {
        console.log(value);
        setValue({value});
        setTempParentCategoryId(value)
    };

    const [stopEffect, setStopEffect] = useState(false);
    const [addCategoryModal, setAddCategoryModal] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const [currentCategory, setCurrentCategory] = useState('');
    const [modal, setModal] = useState(false);
    const [categoryArray, setCategoryArray] = useState([]);
    const [tempCategoryId, setTempCategoryId] = useState('');
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [totalElement, setTotalElement] = useState(0);
    const [activePage, setActivePage] = useState(1);

    const [value, setValue] = useState('');
    const [tempParentCategoryId, setTempParentCategoryId] = useState('');
    const {TreeNode} = TreeSelect;


    const toggle = () => setIsOpen(!isOpen);

    const addCategory = () => {
        setAddCategoryModal(!addCategoryModal);
        setCurrentCategory('');
        setValue('');
    };

    const data = (id) => categoryArray && categoryArray.filter(fitem => fitem.parentId === id).map(item => {
        return <TreeNode value={item.id} title={item.nameUz} key={item.id}>
            {data(item.id)}
        </TreeNode>
    })

    const handlePageChange = (pageNumber) => {
        setActivePage(pageNumber);
        let page = pageNumber - 1;
        axios.get('http://localhost:8081/api/category/getAllCategoriesByPageable?page=' + page + '&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                console.log(res);
                setCategoryArray(res.data.object);
                setTotalElement(res.data.totalElements);
            })
    };

    const saveOrEditCategory = (e, v) => {
        console.log(v, "<<<<<<<<<<<<>>>>>>>>>>>>")
        if (currentCategory) {
            v = {...v, id: currentCategory.id}
        }
        if (tempParentCategoryId) {
            v = {...v, parentId: tempParentCategoryId}
        }
        if (tempIcon) {
            v = {...v,telegramIcon: tempIcon}
        }
        axios.post('http://localhost:8081/api/category', v, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
            .then(res => {
                setModal(!modal);
                axios.get('http://localhost:8081/api/category/getAllCategoriesByPageable?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                    .then(res => {
                        setCategoryArray(res.data.object);
                        setTotalElement(res.data.totalElements);

                    })
                setAddCategoryModal(!addCategoryModal);
            })

    };

    const editCategory = (item) => {
        console.log(item, "EDIT CATEGORY")
        setAddCategoryModal(!addCategoryModal);
        setCurrentCategory(item);
        setValue(item.id);
    };
    const deleteCategory = (id) => {
        setTempCategoryId(id);
        setShowDeleteModal(!showDeleteModal)
    };
    // const changeEnabled = (id) => {
    //     axios.get('http://localhost/api/category/blockOrActivate?id=' + id, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
    //         .then(res => {
    //             axios.get('http://localhost/api/category/getAllCategoriesByPageable', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
    //                 .then(res => {
    //                     setCategoryArray(res.data.object);
    //                 })
    //         })
    // };


    const toggleCategoryDeleteModal = () => {
        setTempCategoryId('');
        setShowDeleteModal(!showDeleteModal);
    };
    const CategoryDeleteYes = () => {
        if (tempCategoryId) {
            axios.delete('http://localhost:8081/api/category/' + tempCategoryId, {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                .then(res => {
                    setShowDeleteModal(!showDeleteModal);
                    axios.get('http://localhost:8081/api/category/getAllCategoriesByPageable?page=0&size=10', {headers: {"Authorization": localStorage.getItem('MilliyToken')}})
                        .then(res => {
                            setCategoryArray(res.data.object);
                            setTotalElement(res.data.totalElements)
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
                    <Col><h2>Kategoriyalar</h2></Col>
                </Row>
                <Row>
                    <Col>
                        <Button className="btn btn-success" onClick={addCategory}>Kategoriya qo'shish</Button>
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
                                <th>Ota kategoryaUz</th>
                                <th>Ota kategoryaRu</th>
                                <th>Taom icon</th>
                                <th>O'zgartirish | O'chirish</th>
                            </tr>
                            </thead>
                            <tbody>
                            {categoryArray ? categoryArray.map((item, index) =>
                                <tr>
                                    <td>{(activePage * 10) + index + 1 - 10}</td>
                                    <td>{item.parentNameUz === null ? <b>{item.nameUz}</b> : item.nameUz}</td>
                                    <td>{item.parentNameRu === null ? <b>{item.nameRu}</b> : item.nameRu}</td>
                                    <td>{item.parentNameUz}</td>
                                    <td>{item.parentNameRu}</td>
                                    <td>{item.telegramIcon}</td>
                                    <td>
                                        <Button onClick={() => editCategory(item)} color="warning">Edit</Button>
                                        <Button outline onClick={() => deleteCategory(item.id)} className="ml-2"
                                                color="danger">Delete</Button>
                                    </td>
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
                <Modal isOpen={addCategoryModal} fade={false} toggle={addCategory}>
                    <ModalHeader>{currentCategory ? "Kategoriyani o'zgartirish " : "Yangi Kategoriya qo'shish"}</ModalHeader>
                    <AvForm onValidSubmit={saveOrEditCategory}>
                        <ModalBody>
                            <Row>
                                <Col>
                                    {emojiPicker}
                                    <button
                                        className="mt-2 ma4 b ph3 pv2 input-reset ba b--black bg-transparent grow pointer f6 dib"
                                        onClick={triggerPicker}
                                        defaultValue={currentCategory ? currentCategory.telegramIcon : ''}
                                        name="telegramIcon">
                                        <span role="img" aria-label="">Taom iconi 😀</span>
                                    </button>
                                    <AvField required={true} type="text"
                                             label="Nomi Uz" className="mt-2"
                                             placeholder="Nomi Uz"
                                             defaultValue={currentCategory ? currentCategory.nameUz : ''}
                                             name="nameUz"/>
                                    <AvField required={true} type="text"
                                             label="Nomi Ru" placeholder="Nomi Ru"
                                             defaultValue={currentCategory ? currentCategory.nameRu : ''}
                                             name="nameRu"/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
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
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button color="danger" onClick={addCategory}>Bekor qilish</Button>
                            <Button color="primary" type="submit">Saqlash</Button>
                        </ModalFooter>
                    </AvForm>
                </Modal>
                <Modal isOpen={showDeleteModal} toggle={toggleCategoryDeleteModal}>
                    <ModalHeader>Kategoriyani o'chirishni istaysizmi?</ModalHeader>

                    <ModalBody>

                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={toggleCategoryDeleteModal}>Bekor qilish</Button>
                        <Button className="ml-3" color="success" onClick={CategoryDeleteYes}
                                type="button">O'chirish</Button>
                    </ModalFooter>
                </Modal>

            </Container>
        </div>
    )
}
import React, {useState} from 'react';
import Home from "./pages/Home";
import NotFount from './NotFound';
import {
    BrowserRouter as
    Router,
    Switch,
    Route,
} from "react-router-dom";
import {Container,Navbar,NavbarBrand,NavbarToggler,Collapse,Nav,NavItem,NavLink} from "reactstrap";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AdminHomePage from "./AdminHomePage";
import Rooms from "./AdminPage/Rooms"
import Products from "./AdminPage/Products"
import Category from "./AdminPage/Category"
import Employees from "./AdminPage/Employees"
import PayType from "./AdminPage/PayType"
import Orders from "./AdminPage/Orders"
/*import reducer, {stateAdmin} from "./reducer";*/


//const AdminState = React.createContext()

/*export const useAdmin = () = {
    return useContext(AdminState)
};*/

/*const IS_ADMIN = 'isADMIN'
const NOT_ADMIN = 'notAdmin'*/



function App() {
   /* const [state,dispatch] = useReducer(reducer,stateAdmin)*/
    const [isOpen, setIsOpen] = useState(false);
    const toggle = () => setIsOpen(!isOpen);
    return (
        <Router>
            <Switch>
                <Container>
                        <header>
                            <Navbar light expand="md">
                                <NavbarBrand href="/">Milliy Ta'omlar</NavbarBrand>
                                <NavbarToggler onClick={toggle}/>
                                <Collapse isOpen={isOpen} navbar>
                                    <Nav className="ml-auto" navbar>
                                        <NavItem>
                                            <NavLink href="/login">Kirish</NavLink>
                                        </NavItem>
                                        <NavItem>
                                            <NavLink href="/register">Ro'yxattan o'tish</NavLink>
                                        </NavItem>
                                    </Nav>
                                </Collapse>
                            </Navbar>
                        </header>
                    <Route exact path="/" component={Home}/>
                    <Route path="/login" component={Login}/>
                    <Route path="/register" component={Register}/>
                    <Route path="/rooms" component={Rooms}/>
                    <Route path="/product" component={Products}/>
                    <Route path="/category" component={Category}/>
                    <Route path="/user" component={Employees}/>
                    <Route path="/payType" component={PayType}/>
                    <Route path="/orders" component={Orders}/>
                    <Route exact path="/adminPage" component={AdminHomePage}/>
                </Container>
                <Route component={NotFount}/>
            </Switch>
        </Router>
    );
}
export default App
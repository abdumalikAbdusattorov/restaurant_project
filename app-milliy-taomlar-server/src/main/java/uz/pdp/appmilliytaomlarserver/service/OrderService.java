package uz.pdp.appmilliytaomlarserver.service;

import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.Orders;
import uz.pdp.appmilliytaomlarserver.entity.ProductWithAmount;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.payload.*;
import uz.pdp.appmilliytaomlarserver.repository.*;
import uz.pdp.appmilliytaomlarserver.utils.CommonUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    ProductService productService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PayTypeRepository payTypeRepository;
    @Autowired
    ProductWithAmountRepository productWithAmountRepository;
    @Autowired
    ProductRepository productRepository;


    public ApiResponse saveOrEdit(ReqOrder reqOrder, User operator) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");

            Orders orders = new Orders();
            if (reqOrder.getId() != null) {
                orders = ordersRepository.findById(reqOrder.getId()).orElseThrow(() -> new ResourceNotFoundException("orders", "id", reqOrder.getId()));
                apiResponse.setMessage("Edited");
                List<ProductWithAmount> productWithAmountList = orders.getProductWithAmountList();
                for (ProductWithAmount productWithAmount : productWithAmountList) {
                    productWithAmountRepository.delete(productWithAmount);
                }
            }
            User client = new User();
            if (reqOrder.getClientId() != null) {
                client = userRepository.findById(reqOrder.getClientId()).orElseThrow(() -> new ResourceNotFoundException("client", "id", reqOrder.getClientId()));
            }
            Optional<User> optionalClient = userRepository.findByPhoneNumberContaining(reqOrder.getClientPhoneNumber());
            if (optionalClient.isPresent()) {
                client = optionalClient.get();
            }
            client.setPhoneNumber(reqOrder.getClientPhoneNumber());
            client.setFirst_name(reqOrder.getClientFirstName());
            client.setLast_name(reqOrder.getClientLastName());
            User savedClient = userRepository.save(client);
            orders.setClient(savedClient);
            orders.setOperator(operator);
            orders.setOrderType(reqOrder.getOrderType());
            orders.setOrderStatus(reqOrder.getOrderStatus());
            orders.setPayStatus(reqOrder.getPayStatus());
            if (reqOrder.getPayTypeId() != null) {
                orders.setPayType(payTypeRepository.findById(reqOrder.getPayTypeId()).orElseThrow(() -> new ResourceNotFoundException("payType", "id", reqOrder.getPayTypeId())));
            }
            if (reqOrder.getLan() != null && reqOrder.getLat() != null) {
                orders.setLan(reqOrder.getLan());
                orders.setLat(reqOrder.getLat());
            }
            if (reqOrder.getOrderAdress() != null) {
                orders.setAddress(reqOrder.getOrderAdress());
            }
            // + " " + reqOrder.getTime().toString()
            orders.setOrderDateTime(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(reqOrder.getDate().getTime())));
            Orders savedOrder = ordersRepository.save(orders);
            for (ReqProductWithAmount reqProductWithAmount : reqOrder.getReqProductWithAmountList()) {
                ProductWithAmount productWithAmount = new ProductWithAmount();
                productWithAmount.setOrders(savedOrder);
                productWithAmount.setProduct(productRepository.findById(reqProductWithAmount.getProductId()).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", reqProductWithAmount.getProductId())));
                productWithAmount.setAmount(reqProductWithAmount.getAmount());
                productWithAmountRepository.save(productWithAmount);
            }
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ResPageable getByOrderStatus(OrderStatus orderStatus, Integer page, Integer size) {
        Page<Orders> ordersList = ordersRepository.findAllByOrderStatus(orderStatus, CommonUtils.getPageable(page, size));
        return new ResPageable(ordersList.stream().map(this::getResOrder).collect(Collectors.toList()), ordersList.getTotalElements(), page);
    }

    private ResOrder getResOrder(Orders orders) {
        ResOrder resOrder = new ResOrder();
        resOrder.setId(orders.getId());
        resOrder.setCreatedAt(orders.getCreatedAt());
        if (orders.getAddress() != null) {
            resOrder.setOrderAddress(orders.getAddress());
        }
        if (orders.getLan() != null && orders.getLat() != null) {
            resOrder.setLan(orders.getLan());
            resOrder.setLat(orders.getLat());
        }
        resOrder.setFromBot(orders.isFromBot());
        resOrder.setUser(orders.getOperator());
        resOrder.setPayType(orders.getPayType().getId());
        resOrder.setOrderSum(orders.getOrderSum());
        resOrder.setOrderStatus(orders.getOrderStatus());
        resOrder.setPayStatus(orders.getPayStatus());
        resOrder.setRooms(orders.getRooms());
        resOrder.setOrderDateAndTime(orders.getOrderDateTime());

        for (ProductWithAmount productWithAmount : orders.getProductWithAmountList()) {
            if (productWithAmount.getId() != null) {

            }
        }
        ordersRepository.save(orders);
        return resOrder;
    }

    public ResProductWithAmount getResProductWithAmount(ProductWithAmount productWithAmount) {
        ResProductWithAmount resProductWithAmount = new ResProductWithAmount();
        resProductWithAmount.setId(productWithAmount.getId());
        resProductWithAmount.setResProduct(productService.getResProduct(productWithAmount.getProduct()));
        resProductWithAmount.setAmount(productWithAmount.getAmount());
        resProductWithAmount.setAmount(productWithAmount.getAmount());
        return resProductWithAmount;
    }


    public ApiResponse changeOrderStatus(UUID id, OrderStatus orderStatus) {
        try {
            Orders orders = ordersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("order", "id", id));
            orders.setOrderStatus(orderStatus);
            return new ApiResponse("Changed", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }

    }

    public ApiResponseModel getBySearchByDate(Timestamp startDate, Timestamp endDate, Integer page, Integer size) {
        Page<Orders> ordersPage = ordersRepository.findAllByCreatedAtBetween(startDate, endDate, CommonUtils.getPageable(page, size));
        return new ApiResponseModel(true, "all", ordersPage.stream().map(this::getResOrder).collect(Collectors.toList()));
    }

    public Object removeOrder(UUID id) {
        try {
            ordersRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);

        }
    }

    public Object cancelOrder(ReqCancelOrder reqCancelOrder) {
        try {
            if (reqCancelOrder.isFullCancel()) {
                for (ReqProductWithAmount reqProductWithAmount : reqCancelOrder.getReqProductWithAmountList()) {
                    ProductWithAmount productWithAmount = productWithAmountRepository.findById(reqProductWithAmount.getId()).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", reqProductWithAmount.getId()));
                    Orders orders = productWithAmount.getOrders();
                    orders.setOrderStatus(OrderStatus.CANCELED);
                    ordersRepository.save(orders);
                    productWithAmount.setCancelledAmount(productWithAmount.getAmount());
                    productWithAmountRepository.save(productWithAmount);
                }
            } else {
                for (ReqProductWithAmount reqProductWithAmount : reqCancelOrder.getReqProductWithAmountList()) {
                    ProductWithAmount productWithAmount = productWithAmountRepository.findById(reqProductWithAmount.getId()).orElseThrow(() -> new ResourceNotFoundException("productWithAount", "id", reqProductWithAmount.getId()));
                    Orders orders = productWithAmount.getOrders();
                    orders.setOrderStatus(OrderStatus.PARTLY_CANCELED);
                    ordersRepository.save(orders);
                    productWithAmount.setCancelledAmount(reqProductWithAmount.getCanceledAmount());
                    productWithAmountRepository.save(productWithAmount);
                }
            }
            return new ApiResponse("Ok", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);

        }
    }
}

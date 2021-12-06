package uz.pdp.appmilliytaomlarserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appmilliytaomlarserver.entity.User;
import uz.pdp.appmilliytaomlarserver.entity.enums.OrderStatus;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqCancelOrder;
import uz.pdp.appmilliytaomlarserver.payload.ReqOrder;
import uz.pdp.appmilliytaomlarserver.security.CurrentUser;
import uz.pdp.appmilliytaomlarserver.service.OrderService;
import uz.pdp.appmilliytaomlarserver.utils.AppConstants;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrdersController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqOrder reqOrder, @CurrentUser User user) {
        ApiResponse response = orderService.saveOrEdit(reqOrder, user);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/byOrderStatus")
    public HttpEntity<?> getORdersByOrderStatus(@RequestParam(value = "orderStatus") OrderStatus orderStatus,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(orderService.getByOrderStatus(orderStatus, page, size));
    }

    @GetMapping("/changeOrderStatus")
    public HttpEntity<?> changeOrderStatus(@RequestParam UUID id, @RequestParam OrderStatus orderStatus) {
        ApiResponse response = orderService.changeOrderStatus(id, orderStatus);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/bySearch")
    public ApiResponseModel getBySearch(
            @RequestParam(value = "startDate", defaultValue = AppConstants.BEGIN_DATE) Timestamp startDate,
            @RequestParam(value = "endDate", defaultValue = AppConstants.END_DATE) Timestamp endDate,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer size) {
        return orderService.getBySearchByDate(startDate, endDate, page, size);
    }

    @DeleteMapping("/id")
    public HttpEntity<?> removeOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.removeOrder(id));
    }

    @PostMapping("/cancelOrder")
    public HttpEntity<?> cancelOrder(@RequestBody ReqCancelOrder reqCancelOrder) {
        return ResponseEntity.ok(orderService.cancelOrder(reqCancelOrder));
    }
}

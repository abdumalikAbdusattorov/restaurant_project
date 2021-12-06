package uz.pdp.appmilliytaomlarserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqPayType;
import uz.pdp.appmilliytaomlarserver.service.PayTypeService;
import uz.pdp.appmilliytaomlarserver.utils.AppConstants;


@RestController
@RequestMapping("/api/payType")
public class PayTypeController {
    @Autowired
    PayTypeService payTypeService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqPayType reqPayType){
        ApiResponse response = payTypeService.saveOrEdit(reqPayType);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED: HttpStatus.ACCEPTED: HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removePayType(@PathVariable Integer id){
        return ResponseEntity.ok(payTypeService.removePayType(id));
    }

//    @GetMapping("/blockOrActivate/{id}")
    @GetMapping("/blockOrActivate")
    public HttpEntity<?> blockOrActivate(@RequestParam Integer id){
        return ResponseEntity.ok(payTypeService.blockOrActivate(id));
    }
    
    @GetMapping("/getAll")
    public ApiResponseModel getAllPayTypes(){
        return payTypeService.getAllPayTypes();
    }

    @GetMapping("/getAllByActive")
    public ApiResponseModel getAll(@RequestParam Boolean active){
        return payTypeService.getAll(active);
    }


}

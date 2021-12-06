package uz.pdp.appmilliytaomlarserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqRoom;
import uz.pdp.appmilliytaomlarserver.service.RoomsService;
import uz.pdp.appmilliytaomlarserver.utils.AppConstants;

@RestController
@RequestMapping("/api/room")
public class RoomsController {

    @Autowired
    RoomsService roomsService;

    @PostMapping
    public HttpEntity<?> saveOrEditRoom(@RequestBody ReqRoom reqRoom){
        ApiResponse response=roomsService.saveOrEditRooms(reqRoom);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteRoom(@PathVariable Integer id){
        ApiResponse response=roomsService.deleteRoom(id);
        return ResponseEntity.status(response.isSuccess()?HttpStatus.OK:HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/all")
    public ApiResponseModel getAll(){
        return roomsService.getAll();
    }

    @GetMapping("/getAllRoomsByPageable")
    public HttpEntity<?> getAllByPageable(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                          @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size){
        return ResponseEntity.ok(roomsService.getAllByPageable(page, size));
    }
}

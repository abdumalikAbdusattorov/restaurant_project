package uz.pdp.appmilliytaomlarserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.PayType;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqPayType;
import uz.pdp.appmilliytaomlarserver.payload.ResPageable;
import uz.pdp.appmilliytaomlarserver.repository.PayTypeRepository;


import java.util.List;
import java.util.Optional;

@Service
public class PayTypeService {
    @Autowired
    PayTypeRepository payTypeRepository;


    public ApiResponse saveOrEdit(ReqPayType reqPayType) {
        ApiResponse apiResponse=new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            PayType payType=new PayType();
            if (reqPayType.getId()!=null){
                payType=payTypeRepository.findById(reqPayType.getId()).orElseThrow(() -> new ResourceNotFoundException("payType","id",reqPayType.getId()));
                apiResponse.setMessage("Edited");
            }
            payType.setNameUz(reqPayType.getNameUz());
            payType.setNameRu(reqPayType.getNameRu());
            payType.setActive(reqPayType.isActive());
            payTypeRepository.save(payType);
        }catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ApiResponse removePayType(Integer id) {
        try {
            payTypeRepository.deleteById(id);
            return new ApiResponse("Deleted",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public ApiResponse blockOrActivate(Integer id) {
        Optional<PayType> optionalPayType=payTypeRepository.findById(id);
        if (optionalPayType.isPresent()){
            PayType payType=optionalPayType.get();
            payType.setActive(!payType.isActive());
            payTypeRepository.save(payType);
            return new ApiResponse(payType.isActive()?"Activated":"Blocked",true);
        }
        return new ApiResponse("Error",false);
    }

    public ApiResponseModel getAll(Boolean active) {
        List<PayType> all = payTypeRepository.findAll();
        if (active!=null){
           all=payTypeRepository.findAllByActive(active);
        }
        return new ApiResponseModel(true,"Ok",all);
    }

    public ApiResponseModel getAllPayTypes() {
       return new ApiResponseModel(true,"ok",payTypeRepository.findAll());
    }
}

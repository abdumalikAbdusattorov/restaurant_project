package uz.pdp.appmilliytaomlarserver.service;


import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.CompanyInfo;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.payload.*;
import uz.pdp.appmilliytaomlarserver.repository.AttachmentRepository;
import uz.pdp.appmilliytaomlarserver.repository.CompanyInfoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class CompanyInfoService {
    private final CompanyInfoRepository companyInfoRepository;
    private final AttachmentRepository attachmentRepository;

    public CompanyInfoService(CompanyInfoRepository companyInfoRepository, AttachmentRepository attachmentRepository) {
        this.companyInfoRepository = companyInfoRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public ApiResponse saveOrEdit(ReqCompanyInfo reqCompanyInfo) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            CompanyInfo companyInfo = new CompanyInfo();

            if (reqCompanyInfo.getId() != null) {
                companyInfo = companyInfoRepository.findById(reqCompanyInfo.getId()).orElseThrow(() -> new ResourceNotFoundException("companyInfo", "id", reqCompanyInfo.getId()));
                apiResponse.setMessage("Edited");
            }
            companyInfo.setName(reqCompanyInfo.getName());
            companyInfo.setDescriptionUz(reqCompanyInfo.getDescriptionUz());
            companyInfo.setDescriptionRu(reqCompanyInfo.getDescriptionRu());
            companyInfo.setDeliveryPrice(reqCompanyInfo.getDeliveryPrice());
            companyInfo.setBotActive(reqCompanyInfo.isBotActive());
            companyInfo.setPercentAndSom(reqCompanyInfo.isPercentAndSom());
            companyInfo.setDeliveryActive(reqCompanyInfo.isDeliveryActive());
            if (reqCompanyInfo.getAttachment() != null) {
                companyInfo.setAttachment(attachmentRepository.findById(reqCompanyInfo.getAttachment()).orElseThrow(() -> new ResourceNotFoundException("photo", "id", reqCompanyInfo.getAttachment())));
            }
            companyInfoRepository.save(companyInfo);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse removeCompanyInfo(Integer id) {
        try {
            companyInfoRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponseModel getAll() {
        List<ResCompanyInfo> resCompanyInfoList = new ArrayList<>();
        List<CompanyInfo> all = companyInfoRepository.findAll();
        for (CompanyInfo companyInfo : all) {
            resCompanyInfoList.add(getResCompanyInfo(companyInfo));
        }
        return new ApiResponseModel(true, "Ok", resCompanyInfoList);
    }

    public ResCompanyInfo getResCompanyInfo(CompanyInfo companyInfo) {
        ResCompanyInfo resCompanyInfo = new ResCompanyInfo();
        resCompanyInfo.setId(companyInfo.getId());
        resCompanyInfo.setName(companyInfo.getName());
        resCompanyInfo.setDescriptionUz(companyInfo.getDescriptionUz());
        resCompanyInfo.setDescriptionRu(companyInfo.getDescriptionRu());
        resCompanyInfo.setDeliveryPrice(companyInfo.getDeliveryPrice());
        resCompanyInfo.setBotActive(companyInfo.isBotActive());
        resCompanyInfo.setPercentAndSom(companyInfo.isPercentAndSom());
        resCompanyInfo.setDeliveryActive(companyInfo.isDeliveryActive());
        if (companyInfo.getAttachment() != null) {
            resCompanyInfo.setAttachment(companyInfo.getAttachment().getId());
        }
        return resCompanyInfo;
    }

    public ApiResponse blockBot(Integer id) {
        Optional<CompanyInfo> optionalCompanyInfo=companyInfoRepository.findById(id);
        if (optionalCompanyInfo.isPresent()){
            CompanyInfo companyInfo=optionalCompanyInfo.get();
            companyInfo.setBotActive(!companyInfo.isBotActive());
            companyInfoRepository.save(companyInfo);
            return new ApiResponse(companyInfo.isBotActive()?"Activated":"Blocked",true);
        }
        return new ApiResponse("Error",false);
    }

    public ApiResponse blockDelivery(Integer id) {
        Optional<CompanyInfo> optionalCompanyInfo=companyInfoRepository.findById(id);
        if (optionalCompanyInfo.isPresent()){
            CompanyInfo companyInfo=optionalCompanyInfo.get();
            companyInfo.setDeliveryActive(!companyInfo.isDeliveryActive());
            companyInfoRepository.save(companyInfo);
            return new ApiResponse(companyInfo.isDeliveryActive()?"Activated":"Blocked",true);
        }
        return new ApiResponse("Error",false);
    }

    public ApiResponse percentAndSom(Integer id) {
        Optional<CompanyInfo> optionalCompanyInfo=companyInfoRepository.findById(id);
        if (optionalCompanyInfo.isPresent()){
            CompanyInfo companyInfo=optionalCompanyInfo.get();
            companyInfo.setDeliveryActive(!companyInfo.isPercentAndSom());
            companyInfoRepository.save(companyInfo);
            return new ApiResponse(companyInfo.isPercentAndSom()?"Activated":"Blocked",true);
        }
        return new ApiResponse("Error",false);
    }
}

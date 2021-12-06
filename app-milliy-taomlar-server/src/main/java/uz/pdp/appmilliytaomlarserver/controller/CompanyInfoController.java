package uz.pdp.appmilliytaomlarserver.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponse;
import uz.pdp.appmilliytaomlarserver.payload.ApiResponseModel;
import uz.pdp.appmilliytaomlarserver.payload.ReqCompanyInfo;
import uz.pdp.appmilliytaomlarserver.service.CompanyInfoService;

@RestController
@RequestMapping("/api/companyInfo")
public class CompanyInfoController {

    @Autowired
    CompanyInfoService companyInfoService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqCompanyInfo reqCompanyInfo) {
        ApiResponse response = companyInfoService.saveOrEdit(reqCompanyInfo);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeCompanyInfo(@PathVariable Integer id) {
        ApiResponse response = companyInfoService.removeCompanyInfo(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);

    }

    @GetMapping("/all")
    public ApiResponseModel getAll() {
        return companyInfoService.getAll();
    }

        @GetMapping("/blockBot")
        public ApiResponse blockBot(@RequestParam Integer id){
            return companyInfoService.blockBot(id);
        }
        @GetMapping("/blockDelivery")
        public ApiResponse blockDelivery(@RequestParam Integer id){
            return companyInfoService.blockDelivery(id);
        }

        @GetMapping("/percentAndSom")
        public ApiResponse percentAndSom(@RequestParam Integer id){
            return companyInfoService.percentAndSom(id);
        }

}

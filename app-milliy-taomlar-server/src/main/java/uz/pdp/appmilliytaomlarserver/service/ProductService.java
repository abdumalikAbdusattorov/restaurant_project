package uz.pdp.appmilliytaomlarserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.Product;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.payload.*;
import uz.pdp.appmilliytaomlarserver.repository.AttachmentRepository;
import uz.pdp.appmilliytaomlarserver.repository.CategoryRepository;
import uz.pdp.appmilliytaomlarserver.repository.ProductRepository;
import uz.pdp.appmilliytaomlarserver.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryService categoryService;

    public ApiResponse saveOrEditProduct(ReqProduct reqProduct) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setMessage("Saved");
            apiResponse.setSuccess(true);
            Product product = new Product();
            if (reqProduct.getId() != null) {
                apiResponse.setMessage("Edited");
                product = productRepository.findById(reqProduct.getId()).orElseThrow(() -> new
                        ResourceNotFoundException("product", "id", reqProduct.getId()));
            }
            if (reqProduct.getPhotoId() != null) {
                product.setAttachment(attachmentRepository.findById(reqProduct.getPhotoId()).orElseThrow(() -> new ResourceNotFoundException("attechment", "id", reqProduct.getPhotoId())));
            }
            product.setCategory(categoryRepository.findById(reqProduct.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category", "id", reqProduct.getCategoryId())));
            product.setNameUz(reqProduct.getNameUz());
            product.setNameRu(reqProduct.getNameRu());
            product.setDescriptionUz(reqProduct.getDescriptionUz());
            product.setDescriptionRu(reqProduct.getDescriptionRu());
            product.setPrice(reqProduct.getPrice());
            productRepository.save(product);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse removeProduct(UUID id) {
        try {
            productRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse changeActive(UUID id, boolean active) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(active);
            productRepository.save(product);
            return new ApiResponse(active ? "Activated" : "Blocked", true);
        }
        return new ApiResponse("Error", false);
    }

    public ResProduct getResProduct(Product product) {
        ResProduct resProduct = new ResProduct();
        resProduct.setId(product.getId());
        resProduct.setNameUz(product.getNameUz());
        resProduct.setNameRu(product.getNameRu());
        resProduct.setDescriptionUz(product.getDescriptionUz());
        resProduct.setDescriptionRu(product.getDescriptionRu());
        resProduct.setPrice(product.getPrice());
        resProduct.setActive(product.isActive());
        resProduct.setResCategory(categoryService.getResCategory(product.getCategory()));
        if (product.getAttachment() != null) {
            resProduct.setPhotoId(product.getAttachment().getId());
        }
        return resProduct;
    }

    public ApiResponseModel getByCatId(Integer catId) {
        List<ResProduct> resProductList = new ArrayList<>();
        if (catId != null) {
            resProductList = productRepository.findAllByCategoryId(catId).stream().map(this::getResProduct).collect(Collectors.toList());
        }
        return new ApiResponseModel(true, "Ok", resProductList);
    }

    public ApiResponseModel sizeListByProduct(UUID id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(product -> new ApiResponseModel(true, "ok", getResProduct(product))).orElseGet(() -> new ApiResponseModel(false, "Error", null));
    }

    public ResPageable getBySearch(Integer page, Integer size, String search) {
        List<ResProduct> resProducts = new ArrayList<>();
        long totalElement = 0;
        if (search.equals("all")) {
            Page<Product> productPage = productRepository.findAll(CommonUtils.getPageable(page, size));
            totalElement = productPage.getTotalElements();
            for (Product product : productPage) {
                resProducts.add(getResProduct(product));
            }
        } else {
            List<Product> bySearch = productRepository.findAllByNameUzStartingWithIgnoreCaseOrNameRuStartingWithIgnoreCaseOrCategoryNameUzStartingWithIgnoreCaseOrCategoryNameRuStartingWithIgnoreCase(search, search, search, search);
            for (Product product : bySearch) {
                resProducts.add(getResProduct(product));
            }
            totalElement = resProducts.size();
        }
        return new ResPageable(resProducts, totalElement, page);
    }

}

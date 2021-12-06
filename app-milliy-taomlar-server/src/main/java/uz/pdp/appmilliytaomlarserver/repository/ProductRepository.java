package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Product;

import java.util.List;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByCategoryId(Integer catId, Pageable pageable);


    List<Product> findAllByNameUzStartingWithIgnoreCaseOrNameRuStartingWithIgnoreCaseOrCategoryNameUzStartingWithIgnoreCaseOrCategoryNameRuStartingWithIgnoreCase(String nameUz, String nameRu, String category_nameUz, String category_nameRu);

    List<Product> findAllByCategoryId(Integer catId);


    //List<Product> findAllByNameUzContainingWithIgnoreCaseOrNameRuContainingWithIgnoreCaseOrDescriptionUzContainingWithIg
}

package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByParentIsNull();
    List<Category> findAllByParentId(Integer parent_id);
}

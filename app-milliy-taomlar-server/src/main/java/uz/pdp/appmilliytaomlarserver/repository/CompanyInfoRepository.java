package uz.pdp.appmilliytaomlarserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appmilliytaomlarserver.entity.CompanyInfo;

import java.util.List;


public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Integer> {
    List<CompanyInfo> findAllByDeliveryPrice(Integer deliveryPrice);
}

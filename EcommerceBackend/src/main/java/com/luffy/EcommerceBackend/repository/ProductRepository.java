package com.luffy.EcommerceBackend.repository;

import com.luffy.EcommerceBackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrBrandIgnoreCaseContaining
            (String name,String description,String brand);
    //OrfindByDescriptionIgnoreCaseContainingOrfindByCategoryIgnoreCaseContainingOrfindByBrandIgnoreCaseContaining
//findByPostProfileIgnoreCaseContainingOrPostDescIgnoreCaseContaining
}

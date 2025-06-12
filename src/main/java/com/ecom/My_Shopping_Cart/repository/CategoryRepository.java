package com.ecom.My_Shopping_Cart.repository;

import com.ecom.My_Shopping_Cart.model.Category;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public Boolean existsByName(String name);
    public List<Category> findByIsActiveTrue();
}

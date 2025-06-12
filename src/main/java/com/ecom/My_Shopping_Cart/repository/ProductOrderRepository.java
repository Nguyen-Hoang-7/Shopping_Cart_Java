package com.ecom.My_Shopping_Cart.repository;

import com.ecom.My_Shopping_Cart.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(Integer userId);
}

package com.ecom.My_Shopping_Cart.repository;

import com.ecom.My_Shopping_Cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);
    public Integer countByUserId(Integer userId);
    public List<Cart> findByUserId(Integer userId);
}

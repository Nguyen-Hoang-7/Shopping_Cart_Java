package com.ecom.My_Shopping_Cart.service.impl;

import com.ecom.My_Shopping_Cart.model.Cart;
import com.ecom.My_Shopping_Cart.model.Product;
import com.ecom.My_Shopping_Cart.model.UserDtls;
import com.ecom.My_Shopping_Cart.repository.CartRepository;
import com.ecom.My_Shopping_Cart.repository.ProductRepository;
import com.ecom.My_Shopping_Cart.repository.UserRepository;
import com.ecom.My_Shopping_Cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        }
        else {
            cart = cartStatus;
            cart.setQuantity(cartStatus.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);
        return saveCart;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> usercarts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;

        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c: usercarts) {
            Double totalPrice = c.getProduct().getDiscountPrice() * c.getQuantity();
            c.setTotalPrice(totalPrice);

            totalOrderPrice += totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);
        }
        // usercarts.get(0).setTotalPrice(totalPrice);


        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;
        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;
            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
                // return true;
            }
            else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }
        else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }

    }
}

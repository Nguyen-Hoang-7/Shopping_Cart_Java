package com.ecom.My_Shopping_Cart.service.impl;

import com.ecom.My_Shopping_Cart.model.Cart;
import com.ecom.My_Shopping_Cart.model.OrderAddress;
import com.ecom.My_Shopping_Cart.model.OrderRequest;
import com.ecom.My_Shopping_Cart.model.ProductOrder;
import com.ecom.My_Shopping_Cart.repository.CartRepository;
import com.ecom.My_Shopping_Cart.repository.ProductOrderRepository;
import com.ecom.My_Shopping_Cart.repository.ProductRepository;
import com.ecom.My_Shopping_Cart.service.OrderService;
import com.ecom.My_Shopping_Cart.utils.CommonUtil;
import com.ecom.My_Shopping_Cart.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart c: carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());
            order.setProduct(c.getProduct());
            order.setPrice(c.getProduct().getDiscountPrice());
            order.setQuantity(c.getQuantity());
            order.setUser(c.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setAddress(orderRequest.getAddress());
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            orderRepository.save(order);

            ProductOrder saveOrder = orderRepository.save(order);
            commonUtil.sendMailForProductOrder(saveOrder, "success");
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status);
            ProductOrder updateOrder = orderRepository.save(productOrder);
            return updateOrder;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}

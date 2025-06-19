package com.ecom.My_Shopping_Cart.service;

import com.ecom.My_Shopping_Cart.model.OrderRequest;
import com.ecom.My_Shopping_Cart.model.ProductOrder;

import java.util.List;

public interface OrderService {
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;
    public List<ProductOrder> getOrdersByUser(Integer userId);
    public ProductOrder updateOrderStatus(Integer id, String status);
    public List<ProductOrder> getAllOrders();
}

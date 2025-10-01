package com.ecom.My_Shopping_Cart.service;

import com.ecom.My_Shopping_Cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    public Category saveCategory(Category category);
    public Boolean existCategory(String name);
    public List<Category> getAllCategory();
    public Boolean deleteCategory(int id);
    public Category getCategoryById(int id);
    public List<Category> getAllActiveCategory();
    public Page<Category> getAllCategoryPagination(Integer pageNo, Integer pageSize);
}

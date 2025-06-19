package com.ecom.My_Shopping_Cart.controller;

import com.ecom.My_Shopping_Cart.model.*;
import com.ecom.My_Shopping_Cart.service.*;
import com.ecom.My_Shopping_Cart.utils.CommonUtil;
import com.ecom.My_Shopping_Cart.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) throws Exception {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            String userProfileImage = userDtls.getProfileImage();
            String encoded_userProfileImage = URLEncoder.encode(userProfileImage, StandardCharsets.UTF_8).replace("+", "%20");
            System.out.println(encoded_userProfileImage);
            m.addAttribute("userProfileImage", encoded_userProfileImage);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categories", allActiveCategory);
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Adding product to cart failed!");
        }
        else {
            session.setAttribute("succMsg", "Successfully add product to cart!");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCart(Principal p, Model m) {

        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);
        Double totalOrderPrice = 0.0;
        if (carts.size() > 0) {
            totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
        }
        else {
            totalOrderPrice = 0.0;
        }
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);
        Double totalPrice = 0.0;
        Double totalOrderPrice = 0.0;
        if (carts.size() > 0) {
            totalPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 35 + 15;

        }
        m.addAttribute("totalPrice", totalPrice);
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception{
        //System.uot.println(request);
        UserDtls user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(), request);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {
        UserDtls loginUser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
        m.addAttribute("orders", orders);
        return "/user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderStatus: values) {
            if (orderStatus.getId().equals(st)) {
                status = orderStatus.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
        try {
            commonUtil.sendMailForProductOrder(updateOrder, status);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Status not updated");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile() {

        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile file, HttpSession session) {
        UserDtls updateUserProfile = userService.updateUserProfile(user, file);
        if (!ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("succMsg", "Profile is updated");
        } else {
            session.setAttribute("errorMsg", "Profile is not updated");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p, HttpSession session) {
        UserDtls loggedInUserDetails = getLoggedInUserDetails(p);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodePassword);
            UserDtls updateUser = userService.updateUser(loggedInUserDetails);
            if (ObjectUtils.isEmpty(updateUser)) {
                session.setAttribute("errorMsg", "Password is not updated || Error in server");
            }
            else {
                session.setAttribute("succMsg", "Password Updated Successfully");
            }

        }
        else {
            session.setAttribute("errorMsg", "Current Password Incorrect");
        }
        return "redirect:/user/profile";
    }
}

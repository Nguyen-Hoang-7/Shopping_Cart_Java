package com.ecom.My_Shopping_Cart.utils;

import com.ecom.My_Shopping_Cart.model.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {
    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String recipentEmail) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("nguyenhoangvn3@gmail.com", "Shopping Cart");
        helper.setTo(recipentEmail);

        String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";
        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        // Generate URL : https://localhost:8080/reset-passwor
        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");
    }

    String msg = null;

    public Boolean sendMailForProductOrder(ProductOrder order, String status) throws Exception {
        msg = "<p>Hello [[name]]</p>. <p>Thank you for your order <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Product Details: </b></p>"
                + "<p>Name: [[productName]]</p>"
                + "<p>Category: [[category]]</p>"
                + "<p>Quantity: [[quantity]]</p>"
                + "<p>Price: [[price]]</p>"
                + "<p>Payment Type: [[paymentType]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("nguyenhoangvn3@gmail.com", "Shopping Cart");
        helper.setTo(order.getOrderAddress().getEmail());

        //OrderStatus[] values = OrderStatus.values();
        msg = msg.replace("[[name]]", order.getOrderAddress().getLastName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", order.getProduct().getTitle());
        msg = msg.replace("[[category]]", order.getProduct().getCategory());
        msg = msg.replace("[[quantity]]", order.getQuantity().toString());
        msg = msg.replace("[[price]]", order.getPrice().toString());
        msg = msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }
}

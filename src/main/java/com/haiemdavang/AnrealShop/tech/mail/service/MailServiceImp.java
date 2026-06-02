package com.haiemdavang.AnrealShop.tech.mail.service;

import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.service.order.OrderServiceImp;
import com.haiemdavang.AnrealShop.tech.mail.MailType;
import com.haiemdavang.AnrealShop.tech.redis.service.IRedisService;
import com.haiemdavang.AnrealShop.utils.MailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImp implements IMailService{
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final IRedisService redisService;
    private final OrderServiceImp orderServiceImp;


    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${server.fe.base_url}")
    private String feBaseUrl;

    private final String OTP_REQUEST_PREFIX = "otp_request:";
    private final String OTP_CODE_PREFIX = "otp_code:";

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true, "UTF-8");
            mailHelper.setFrom(mailFrom);
            mailHelper.setSubject(subject);
            mailHelper.setText(htmlContent, true);
            mailHelper.setTo(to);
            javaMailSender.send(mail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
            throw new AnrealShopException("EMAIL_EXCEPTION");
        }
    }

    @Override
    @Transactional
    public void sendOTP(String email, String mailTypeInput) {
        MailType mailType;
        try {
           mailType =  MailType.valueOf(mailTypeInput);
        } catch (IllegalArgumentException e) {
            throw new AnrealShopException("INVALID_MAIL_TYPE");
        }
        if(!userRepository.existsByEmail(email))
            throw new BadRequestException("USER_NOT_FOUND");
        int stamp = 0;

        if(redisService.isExists(OTP_REQUEST_PREFIX + email)) {
            stamp = Integer.parseInt(redisService.getValue(OTP_REQUEST_PREFIX + email));
        }
        int MAX_ATTEMPTS = 5;
        if(stamp >= MAX_ATTEMPTS) {
            redisService.addValue(OTP_REQUEST_PREFIX + email, stamp + "", 12, TimeUnit.HOURS);
            throw new ForbiddenException("OTP_DENIED");
        }else {
            String code = getCode();
            sendHtmlEmail(email, "Your OTP", MailTemplate.getEmailHTML(code, email, mailType));
            int EXPIRATION_TIME = 1;
            redisService.addValue(OTP_CODE_PREFIX + email, code, EXPIRATION_TIME, TimeUnit.MINUTES);
            redisService.addValue(OTP_REQUEST_PREFIX + email, stamp + 1 + "", 2, TimeUnit.HOURS);
        }
    }

    @Override
    public boolean verifyOTP(String otp, String email) {
        String otpKey = OTP_CODE_PREFIX + email;
        if(!redisService.isExists(otpKey))
            throw new BadRequestException("OTP_NOT_FOUND");
        if(redisService.getValue(otpKey) == null || !redisService.getValue(otpKey).equals(otp) )
            throw new BadRequestException("OTP_INVALID");
        return true;
    }

    @Override
    public String getCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    @Override
    public void delOTP(String email) {
        redisService.del(OTP_CODE_PREFIX + email);
        redisService.del(OTP_REQUEST_PREFIX + email);
        redisService.del(email);
    }


    @Transactional(readOnly = true)
    public void sendMailNewOrder(String orderId) {
        List<ShopOrder> shopAndItemForShop = orderServiceImp.getShopOrderByOrderId(orderId);
        for (ShopOrder shopOrder: shopAndItemForShop) {
            String emailShop = shopOrder.getShop().getUser().getEmail();
            String shopName = shopOrder.getShop().getName();
            Set<ProductOrderItemDto> productOrderItems = orderServiceImp.getProductOrderItemByShopOrder(shopOrder.getId());
            String templateMail = MailTemplate.getNewOrderHTMLVietnamese(orderId, shopName, productOrderItems, true, feBaseUrl);
            sendHtmlEmail(emailShop, "Đơn Hàng Mới", templateMail);
        }
    }

    @Transactional(readOnly = true)
    public void sendMailShipperPickup(Set<String> shopOrderIds) {
        List<ShopOrder> shopOrders = orderServiceImp.getShopOrderByShopOrderIds(shopOrderIds);
        for (ShopOrder shopOrder: shopOrders) {
            String emailUser = shopOrder.getUser().getEmail();
            String userName = shopOrder.getUser().getFullName();
            Set<ProductOrderItemDto> productOrderItems = orderServiceImp.getProductOrderItemByShopOrder(shopOrder.getId());
            String templateMail = MailTemplate.getOrderShippingHTMLVietnamese(shopOrder.getId(), userName, productOrderItems, false, feBaseUrl);
            sendHtmlEmail(emailUser, "Cập nhật trạng thái đơn hàng", templateMail);
        }
    }

    @Transactional(readOnly = true)
    public void sendMailOrderDelivering(String shopOrderId) {
        ShopOrder shopOrder = orderServiceImp.getShopOrderById(shopOrderId);
        String emailUser = shopOrder.getUser().getEmail();
        String userName = shopOrder.getUser().getFullName();
        Set<ProductOrderItemDto> productOrderItems = orderServiceImp.getProductOrderItemByShopOrder(shopOrder.getId());
        String templateMail = MailTemplate.getOrderDeliveringHTMLVietnamese(shopOrder.getId(), userName, productOrderItems, false, feBaseUrl);
        sendHtmlEmail(emailUser, "Cập nhật trạng thái đơn hàng", templateMail);
    }


}

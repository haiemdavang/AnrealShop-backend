package com.haiemdavang.AnrealShop.tech.mail.service;

import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
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

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImp implements IMailService{
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final IRedisService redisService;
    @Value("${spring.mail.username}")
    private String mailFrom;

    private final String OTP_REQUEST_PREFIX = "otp_request:";
    private final String OTP_CODE_PREFIX = "otp_code:";

    @Override
    @Transactional
    public void sendOTP(String email, String mailTypeInput) {
        MailType mailType;
        try {
           mailType =  MailType.valueOf(mailTypeInput);
        } catch (IllegalArgumentException e) {
            throw new AnrealShopException("INVALID_MAIL_TYPE");
        }
        if(userRepository.existsByEmail(email) && !mailType.equals(MailType.VERIFY_EMAIL))
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
            MimeMessage mail = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mailHelper = new MimeMessageHelper(mail, true, "UTF-8");
                mailHelper.setFrom(mailFrom);
                mailHelper.setSubject("Your OTP");
                String code = getCode();
                mailHelper.setText(MailTemplate.getEmailHTML(code, email, mailType), true);
                mailHelper.setTo(email);
                javaMailSender.send(mail);
                int EXPIRATION_TIME = 1;
                redisService.addValue(OTP_CODE_PREFIX + email, code, EXPIRATION_TIME, TimeUnit.MINUTES);
                redisService.addValue(OTP_REQUEST_PREFIX + email, stamp + 1 + "", 2, TimeUnit.HOURS);
            } catch (MessagingException e) {
                throw new AnrealShopException("EMAIL_EXCEPTION");
            }
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




}

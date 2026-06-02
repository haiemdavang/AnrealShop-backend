package com.haiemdavang.AnrealShop.utils;

import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.tech.mail.MailType;

import java.util.Set;

public class MailTemplate {

    public static String getOtpVerificationEmailHTMLVietnamese(String code, String userName) {
        return "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "<head>\r\n"
                + "    <meta charset=\"UTF-8\">\r\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
                + "    <title>Xác Minh OTP - AnrealShop</title>\r\n"
                + "    <style>\r\n"
                + "        * { margin: 0; padding: 0; box-sizing: border-box; }\r\n"
                + "        body {\r\n"
                + "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;\r\n"
                + "            background-color: #f1f5f9;\r\n"
                + "            padding: 20px;\r\n"
                + "            line-height: 1.6;\r\n"
                + "        }\r\n"
                + "        .container {\r\n"
                + "            max-width: 600px;\r\n"
                + "            margin: 0 auto;\r\n"
                + "            background: #ffffff;\r\n"
                + "            border-radius: 12px;\r\n"
                + "            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);\r\n"
                + "            overflow: hidden;\r\n"
                + "        }\r\n"
                + "        .header {\r\n"
                + "            background: linear-gradient(135deg, #0ea5e9, #0284c7);\r\n"
                + "            padding: 32px 24px;\r\n"
                + "            text-align: center;\r\n"
                + "        }\r\n"
                + "        .header-title {\r\n"
                + "            font-size: 24px;\r\n"
                + "            font-weight: 700;\r\n"
                + "            color: #ffffff;\r\n"
                + "            margin-bottom: 8px;\r\n"
                + "        }\r\n"
                + "        .header-icon {\r\n"
                + "            font-size: 48px;\r\n"
                + "            margin-bottom: 12px;\r\n"
                + "        }\r\n"
                + "        .content {\r\n"
                + "            padding: 32px 24px;\r\n"
                + "        }\r\n"
                + "        .greeting {\r\n"
                + "            font-size: 16px;\r\n"
                + "            color: #1e293b;\r\n"
                + "            margin-bottom: 16px;\r\n"
                + "        }\r\n"
                + "        .username {\r\n"
                + "            font-weight: 600;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "        }\r\n"
                + "        .message {\r\n"
                + "            font-size: 14px;\r\n"
                + "            color: #475569;\r\n"
                + "            margin-bottom: 24px;\r\n"
                + "        }\r\n"
                + "        .otp-container {\r\n"
                + "            background: linear-gradient(135deg, #eff6ff, #dbeafe);\r\n"
                + "            border: 2px solid #0ea5e9;\r\n"
                + "            border-radius: 8px;\r\n"
                + "            padding: 24px;\r\n"
                + "            text-align: center;\r\n"
                + "            margin: 24px 0;\r\n"
                + "        }\r\n"
                + "        .otp-label {\r\n"
                + "            font-size: 12px;\r\n"
                + "            color: #64748b;\r\n"
                + "            text-transform: uppercase;\r\n"
                + "            letter-spacing: 0.5px;\r\n"
                + "            margin-bottom: 8px;\r\n"
                + "        }\r\n"
                + "        .otp-code {\r\n"
                + "            font-size: 32px;\r\n"
                + "            font-weight: 700;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "            letter-spacing: 8px;\r\n"
                + "            font-family: 'Courier New', monospace;\r\n"
                + "        }\r\n"
                + "        .warning {\r\n"
                + "            background: #fef3c7;\r\n"
                + "            border-left: 4px solid #f59e0b;\r\n"
                + "            padding: 12px 16px;\r\n"
                + "            border-radius: 4px;\r\n"
                + "            margin: 24px 0;\r\n"
                + "        }\r\n"
                + "        .warning-text {\r\n"
                + "            font-size: 13px;\r\n"
                + "            color: #92400e;\r\n"
                + "        }\r\n"
                + "        .footer {\r\n"
                + "            background: #f8fafc;\r\n"
                + "            padding: 24px;\r\n"
                + "            text-align: center;\r\n"
                + "            border-top: 1px solid #e2e8f0;\r\n"
                + "        }\r\n"
                + "        .footer-text {\r\n"
                + "            font-size: 12px;\r\n"
                + "            color: #64748b;\r\n"
                + "            line-height: 1.5;\r\n"
                + "        }\r\n"
                + "        .brand {\r\n"
                + "            font-weight: 600;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "        }\r\n"
                + "    </style>\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "    <div class=\"container\">\r\n"
                + "        <div class=\"header\">\r\n"
                + "            <div class=\"header-icon\">🔐</div>\r\n"
                + "            <div class=\"header-title\">Xác Minh OTP</div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"content\">\r\n"
                + "            <p class=\"greeting\">Xin chào <span class=\"username\">" + userName + "</span>!</p>\r\n"
                + "            \r\n"
                + "            <p class=\"message\">\r\n"
                + "                Chúng tôi đã nhận được yêu cầu xác minh tài khoản của bạn. \r\n"
                + "                Sử dụng mã OTP dưới đây để hoàn tất quá trình xác minh:\r\n"
                + "            </p>\r\n"
                + "            \r\n"
                + "            <div class=\"otp-container\">\r\n"
                + "                <div class=\"otp-label\">Mã xác thực của bạn</div>\r\n"
                + "                <div class=\"otp-code\">" + code + "</div>\r\n"
                + "            </div>\r\n"
                + "            \r\n"
                + "            <div class=\"warning\">\r\n"
                + "                <p class=\"warning-text\">\r\n"
                + "                    ⚠️ <strong>Lưu ý quan trọng:</strong><br>\r\n"
                + "                    • Mã OTP này chỉ có hiệu lực trong <strong>10 phút</strong><br>\r\n"
                + "                    • Vui lòng không chia sẻ mã này với bất kỳ ai<br>\r\n"
                + "                    • Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email\r\n"
                + "                </p>\r\n"
                + "            </div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"footer\">\r\n"
                + "            <p class=\"footer-text\">\r\n"
                + "                Email này được gửi tự động từ hệ thống <span class=\"brand\">AnrealShop</span>.<br>\r\n"
                + "                Vui lòng không trả lời email này.\r\n"
                + "            </p>\r\n"
                + "            <p class=\"footer-text\" style=\"margin-top: 8px;\">\r\n"
                + "                © 2025 AnrealShop. All rights reserved.\r\n"
                + "            </p>\r\n"
                + "        </div>\r\n"
                + "    </div>\r\n"
                + "</body>\r\n"
                + "</html>";
    }

    public static String getEmailVerificationHTMLVietnamese(String code, String userName) {
        return "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "<head>\r\n"
                + "    <meta charset=\"UTF-8\">\r\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
                + "    <title>Xác Minh Email - AnrealShop</title>\r\n"
                + "    <style>\r\n"
                + "        * { margin: 0; padding: 0; box-sizing: border-box; }\r\n"
                + "        body {\r\n"
                + "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;\r\n"
                + "            background-color: #f1f5f9;\r\n"
                + "            padding: 20px;\r\n"
                + "            line-height: 1.6;\r\n"
                + "        }\r\n"
                + "        .container {\r\n"
                + "            max-width: 600px;\r\n"
                + "            margin: 0 auto;\r\n"
                + "            background: #ffffff;\r\n"
                + "            border-radius: 12px;\r\n"
                + "            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);\r\n"
                + "            overflow: hidden;\r\n"
                + "        }\r\n"
                + "        .header {\r\n"
                + "            background: linear-gradient(135deg, #0ea5e9, #0284c7);\r\n"
                + "            padding: 32px 24px;\r\n"
                + "            text-align: center;\r\n"
                + "        }\r\n"
                + "        .header-title {\r\n"
                + "            font-size: 24px;\r\n"
                + "            font-weight: 700;\r\n"
                + "            color: #ffffff;\r\n"
                + "            margin-bottom: 8px;\r\n"
                + "        }\r\n"
                + "        .header-icon {\r\n"
                + "            font-size: 48px;\r\n"
                + "            margin-bottom: 12px;\r\n"
                + "        }\r\n"
                + "        .content {\r\n"
                + "            padding: 32px 24px;\r\n"
                + "        }\r\n"
                + "        .greeting {\r\n"
                + "            font-size: 16px;\r\n"
                + "            color: #1e293b;\r\n"
                + "            margin-bottom: 16px;\r\n"
                + "        }\r\n"
                + "        .username {\r\n"
                + "            font-weight: 600;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "        }\r\n"
                + "        .message {\r\n"
                + "            font-size: 14px;\r\n"
                + "            color: #475569;\r\n"
                + "            margin-bottom: 24px;\r\n"
                + "        }\r\n"
                + "        .otp-container {\r\n"
                + "            background: linear-gradient(135deg, #eff6ff, #dbeafe);\r\n"
                + "            border: 2px solid #0ea5e9;\r\n"
                + "            border-radius: 8px;\r\n"
                + "            padding: 24px;\r\n"
                + "            text-align: center;\r\n"
                + "            margin: 24px 0;\r\n"
                + "        }\r\n"
                + "        .otp-label {\r\n"
                + "            font-size: 12px;\r\n"
                + "            color: #64748b;\r\n"
                + "            text-transform: uppercase;\r\n"
                + "            letter-spacing: 0.5px;\r\n"
                + "            margin-bottom: 8px;\r\n"
                + "        }\r\n"
                + "        .otp-code {\r\n"
                + "            font-size: 32px;\r\n"
                + "            font-weight: 700;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "            letter-spacing: 8px;\r\n"
                + "            font-family: 'Courier New', monospace;\r\n"
                + "        }\r\n"
                + "        .info-box {\r\n"
                + "            background: #f0f9ff;\r\n"
                + "            border-left: 4px solid #0ea5e9;\r\n"
                + "            padding: 16px;\r\n"
                + "            border-radius: 4px;\r\n"
                + "            margin: 24px 0;\r\n"
                + "        }\r\n"
                + "        .info-text {\r\n"
                + "            font-size: 13px;\r\n"
                + "            color: #075985;\r\n"
                + "            line-height: 1.6;\r\n"
                + "        }\r\n"
                + "        .warning {\r\n"
                + "            background: #fef3c7;\r\n"
                + "            border-left: 4px solid #f59e0b;\r\n"
                + "            padding: 12px 16px;\r\n"
                + "            border-radius: 4px;\r\n"
                + "            margin: 24px 0;\r\n"
                + "        }\r\n"
                + "        .warning-text {\r\n"
                + "            font-size: 13px;\r\n"
                + "            color: #92400e;\r\n"
                + "        }\r\n"
                + "        .footer {\r\n"
                + "            background: #f8fafc;\r\n"
                + "            padding: 24px;\r\n"
                + "            text-align: center;\r\n"
                + "            border-top: 1px solid #e2e8f0;\r\n"
                + "        }\r\n"
                + "        .footer-text {\r\n"
                + "            font-size: 12px;\r\n"
                + "            color: #64748b;\r\n"
                + "            line-height: 1.5;\r\n"
                + "        }\r\n"
                + "        .brand {\r\n"
                + "            font-weight: 600;\r\n"
                + "            color: #0ea5e9;\r\n"
                + "        }\r\n"
                + "    </style>\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "    <div class=\"container\">\r\n"
                + "        <div class=\"header\">\r\n"
                + "            <div class=\"header-icon\">📧</div>\r\n"
                + "            <div class=\"header-title\">Xác Minh Email</div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"content\">\r\n"
                + "            <p class=\"greeting\">Xin chào <span class=\"username\">" + userName + "</span>!</p>\r\n"
                + "            \r\n"
                + "            <p class=\"message\">\r\n"
                + "                Cảm ơn bạn đã đăng ký tài khoản tại <span class=\"brand\">AnrealShop</span>. \r\n"
                + "                Để hoàn tất quá trình đăng ký và kích hoạt tài khoản, vui lòng sử dụng mã xác thực dưới đây:\r\n"
                + "            </p>\r\n"
                + "            \r\n"
                + "            <div class=\"otp-container\">\r\n"
                + "                <div class=\"otp-label\">Mã xác thực email</div>\r\n"
                + "                <div class=\"otp-code\">" + code + "</div>\r\n"
                + "            </div>\r\n"
                + "            \r\n"
                + "            <div class=\"info-box\">\r\n"
                + "                <p class=\"info-text\">\r\n"
                + "                    ℹ️ <strong>Hướng dẫn:</strong><br>\r\n"
                + "                    1. Nhập mã xác thực vào trang đăng ký<br>\r\n"
                + "                    2. Nhấn nút \"Xác nhận\" để hoàn tất<br>\r\n"
                + "                    3. Sau khi xác minh, bạn có thể đăng nhập và bắt đầu mua sắm\r\n"
                + "                </p>\r\n"
                + "            </div>\r\n"
                + "            \r\n"
                + "            <div class=\"warning\">\r\n"
                + "                <p class=\"warning-text\">\r\n"
                + "                    ⚠️ <strong>Lưu ý quan trọng:</strong><br>\r\n"
                + "                    • Mã xác thực này chỉ có hiệu lực trong <strong>10 phút</strong><br>\r\n"
                + "                    • Vui lòng không chia sẻ mã này với bất kỳ ai<br>\r\n"
                + "                    • Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email\r\n"
                + "                </p>\r\n"
                + "            </div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"footer\">\r\n"
                + "            <p class=\"footer-text\">\r\n"
                + "                Email này được gửi tự động từ hệ thống <span class=\"brand\">AnrealShop</span>.<br>\r\n"
                + "                Vui lòng không trả lời email này.\r\n"
                + "            </p>\r\n"
                + "            <p class=\"footer-text\" style=\"margin-top: 8px;\">\r\n"
                + "                © 2025 AnrealShop. All rights reserved.\r\n"
                + "            </p>\r\n"
                + "        </div>\r\n"
                + "    </div>\r\n"
                + "</body>\r\n"
                + "</html>";
    }

    public static String buildOrderItemsTableHtml(Set<ProductOrderItemDto> items) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px;\">")
          .append("<thead><tr style=\"background-color: #f1f5f9; border-bottom: 2px solid #e2e8f0; text-align: left;\">")
          .append("<th style=\"padding: 12px; font-weight: 600; color: #475569;\">Sản phẩm</th>")
          .append("<th style=\"padding: 12px; font-weight: 600; color: #475569;\">Phân loại</th>")
          .append("<th style=\"padding: 12px; font-weight: 600; color: #475569;\">SL</th>")
          .append("<th style=\"padding: 12px; font-weight: 600; color: #475569; text-align: right;\">Giá</th>")
          .append("</tr></thead><tbody>");

        for (ProductOrderItemDto item : items) {
            sb.append("<tr style=\"border-bottom: 1px solid #e2e8f0;\">")
              .append("<td style=\"padding: 12px; color: #1e293b; display: flex; align-items: center; gap: 12px;\">");

            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                sb.append("<img src=\"").append(item.getProductImage()).append("\" alt=\"img\" style=\"width: 40px; height: 40px; object-fit: cover; border-radius: 4px;\">");
            }
            sb.append("<span>").append(item.getProductName() != null ? item.getProductName() : "").append("</span></td>")
              .append("<td style=\"padding: 12px; color: #475569;\">").append(item.getVariant() != null ? item.getVariant() : "").append("</td>")
              .append("<td style=\"padding: 12px; color: #475569;\">").append(item.getQuantity()).append("</td>")
              .append("<td style=\"padding: 12px; color: #1e293b; text-align: right; font-weight: 500;\">")
              .append(item.getPrice() != null ? String.format("%,d đ", item.getPrice()) : "0 đ").append("</td>")
              .append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private static String generateOrderEmailHTML(String title, String icon, String userName, String mainMessage, String orderId, String tableHtml, String noteMessage, String noteColor, boolean isForShop, String actionLink, String actionLinkLabel) {
        String actionLinkHtml = "";
        if (actionLink != null && !actionLink.isBlank()) {
            String buttonLabel = (actionLinkLabel != null && !actionLinkLabel.isBlank()) ? actionLinkLabel : "Xem đơn hàng";
            actionLinkHtml = "            <div style=\"text-align: center; margin: 24px 0;\">\r\n"
                    + "                <a href=\"" + actionLink + "\" style=\"display: inline-block; background: #0ea5e9; color: #ffffff; text-decoration: none; padding: 12px 20px; border-radius: 8px; font-weight: 600;\">"
                    + buttonLabel + "</a>\r\n"
                    + "            </div>\r\n";
        }
        return "<!DOCTYPE html>\r\n"
                + "<html>\r\n"
                + "<head>\r\n"
                + "    <meta charset=\"UTF-8\">\r\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
                + "    <title>" + title + " - AnrealShop</title>\r\n"
                + "    <style>\r\n"
                + "        * { margin: 0; padding: 0; box-sizing: border-box; }\r\n"
                + "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Arial, sans-serif; background-color: #f1f5f9; padding: 20px; line-height: 1.6; }\r\n"
                + "        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); overflow: hidden; }\r\n"
                + "        .header { background: linear-gradient(135deg, #0ea5e9, #0284c7); padding: 32px 24px; text-align: center; }\r\n"
                + "        .header-title { font-size: 24px; font-weight: 700; color: #ffffff; margin-bottom: 8px; }\r\n"
                + "        .header-icon { font-size: 48px; margin-bottom: 12px; }\r\n"
                + "        .content { padding: 32px 24px; }\r\n"
                + "        .greeting { font-size: 16px; color: #1e293b; margin-bottom: 16px; }\r\n"
                + "        .username { font-weight: 600; color: #0ea5e9; }\r\n"
                + "        .message { font-size: 14px; color: #475569; margin-bottom: 24px; }\r\n"
                + "        /* Phần mã đơn hàng tối giản trên một hàng */\r\n"
                + "        .order-box { font-size: 15px; color: #334155; margin: 24px 0; padding: 12px 16px; background: #f8fafc; border-radius: 6px; border: 1px dashed #e2e8f0; display: inline-flex; align-items: center; white-space: nowrap; }\r\n"
                + "        .order-title { flex-shrink: 0; }\r\n"
                + "        .order-id { font-family: 'Courier New', monospace; font-weight: 700; color: #0f172a; background: #e2e8f0; padding: 4px 8px; border-radius: 4px; margin: 0 8px; max-width: 150px; overflow: hidden; text-overflow: ellipsis; display: inline-block; vertical-align: middle; }\r\n"
                + "        .btn-copy { background: #0ea5e9; color: #ffffff; border: none; width: 28px; height: 28px; border-radius: 6px; font-size: 14px; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0; transition: background 0.2s; }\r\n"
                + "        .btn-copy:hover { background: #0284c7; }\r\n"
                + "        .note-box { background: " + noteColor + "; border-left: 4px solid rgba(0,0,0,0.1); border-radius: 4px; padding: 12px 16px; margin: 24px 0; }\r\n"
                + "        .note-text { font-size: 13px; color: #1e293b; }\r\n"
                + "        .footer { background: #f8fafc; padding: 24px; text-align: center; border-top: 1px solid #e2e8f0; }\r\n"
                + "        .footer-text { font-size: 12px; color: #64748b; line-height: 1.5; }\r\n"
                + "        .brand { font-weight: 600; color: #0ea5e9; }\r\n"
                + "    </style>\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "    <div class=\"container\">\r\n"
                + "        <div class=\"header\">\r\n"
                + "            <div class=\"header-icon\">" + icon + "</div>\r\n"
                + "            <div class=\"header-title\">" + title + "</div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"content\">\r\n"
                + "            <p class=\"greeting\">" + (isForShop ? "Kính gửi chủ shop " : "Xin chào ") + "<span class=\"username\">" + userName + "</span>!</p>\r\n"
                + "            <p class=\"message\">" + mainMessage + "</p>\r\n"
                + "            \r\n"
                + "            <div class=\"order-box\">\r\n"
                + "                <span class=\"order-title\">Mã đơn hàng:</span>\r\n"
                + "                <span class=\"order-id\" title=\"" + orderId + "\">" + orderId + "</span>\r\n"
                + "                <button class=\"btn-copy\" title=\"Sao chép mã\" onclick=\"navigator.clipboard.writeText('" + orderId + "'); alert('Đã sao chép mã đơn hàng!');\">📋</button>\r\n"
                + "            </div>\r\n"
                + "            \r\n"
                + actionLinkHtml
                + "            \r\n"
                +              tableHtml + "\r\n"
                + "            \r\n"
                + "            <div class=\"note-box\">\r\n"
                + "                <p class=\"note-text\">" + noteMessage + "</p>\r\n"
                + "            </div>\r\n"
                + "        </div>\r\n"
                + "        \r\n"
                + "        <div class=\"footer\">\r\n"
                + "            <p class=\"footer-text\">\r\n"
                + "                Email này được gửi tự động từ hệ thống <span class=\"brand\">AnrealShop</span>.<br>\r\n"
                + "                Vui lòng không trả lời email này.\r\n"
                + "            </p>\r\n"
                + "            <p class=\"footer-text\" style=\"margin-top: 8px;\">\r\n"
                + "                © 2026 AnrealShop. All rights reserved.\r\n"
                + "            </p>\r\n"
                + "        </div>\r\n"
                + "    </div>\r\n"
                + "</body>\r\n"
                + "</html>";
    }

    public static String getNewOrderHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop, String feBaseUrl) {
        String mainMessage = isForShop ? "Shop vừa nhận được một đơn hàng mới. Vui lòng kiểm tra và xác nhận sớm nhất có thể!" : "Cảm ơn bạn đã mua sắm tại AnrealShop! Đơn hàng của bạn đã được hệ thống ghi nhận.";
        String noteMessage = isForShop ? "Bạn cần phân loại và đóng gói sau khi xác nhận đơn hàng." : "Chúng tôi sẽ thông báo cho bạn khi đơn hàng được xác nhận.";
        String orderLink = isForShop && feBaseUrl != null ? feBaseUrl + "/myshop/orders" : null;
        return generateOrderEmailHTML("Đơn hàng mới", "🛒", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#f0f9ff", isForShop, orderLink, "Xem đơn hàng");
    }

    public static String getNewOrderHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop) {
        return getNewOrderHTMLVietnamese(orderId, userName, items, isForShop, null);
    }

    public static String getOrderConfirmHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop) {
        String mainMessage = isForShop ? "Đơn hàng đã được bạn xác nhận thành công." : "Đơn hàng của bạn đã được xác nhận và đang được chuẩn bị.";
        String noteMessage = isForShop ? "Vui lòng hoàn thiện đóng gói và giao cho đơn vị vận chuyển đúng hạn." : "Chúng tôi đang đóng gói sản phẩm và sẽ sớm giao cho đơn vị vận chuyển.";
        return generateOrderEmailHTML("Xác Nhận Đơn Hàng", "✅", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#f0fdf4", isForShop, null, null);
    }

    public static String getOrderShippingHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop, String feBaseUrl) {
        String mainMessage = isForShop ? "Đơn hàng đã được lấy và đang trong quá trình vận chuyển đến người mua." : "Đơn hàng của bạn đã được giao cho đơn vị vận chuyển.";
        String noteMessage =  "Hãy theo dõi hành trình đơn hàng nếu cần.";
        String orderLink = isForShop && feBaseUrl != null ? feBaseUrl + "/settings/orders/" + orderId : null;
        return generateOrderEmailHTML("Đang Vận Chuyển", "📦", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#f0f9ff", isForShop, orderLink, "Xem chi tiết đơn hàng");
    }

    public static String getOrderDeliveringHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop, String feBaseUrl) {
        String mainMessage = isForShop ? "Đơn hàng đang trên đường giao đến khách hàng." : "Đơn hàng của bạn đang trên đường giao đến bạn. vui lòng chú ý điện thoại của bạn khi nhân viên giao hàng liên hệ.";
        String noteMessage = isForShop ? "Tiến độ sẽ được cập nhật liên tục." : "Shipper sẽ liên hệ với bạn trong thời gian sớm nhất.";
        String orderLink = isForShop && feBaseUrl != null ? feBaseUrl + "/settings/orders/" + orderId : null;
        return generateOrderEmailHTML("Đang Giao Hàng", "🚚", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#f0f9ff", isForShop, orderLink, "Xem chi tiết đơn hàng");
    }

    public static String getOrderSuccessHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop) {
        String mainMessage = isForShop ? "Đơn hàng đã được khách hàng nhận thành công." : "Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã tin tưởng AnrealShop!";
        String noteMessage = isForShop ? "Tiền hàng sẽ được đối soát và chuyển vào ví của bạn trong vài ngày tới." : "Đừng quên để lại đánh giá cho sản phẩm bạn nhé.";
        return generateOrderEmailHTML("Giao Hàng Thành Công", "🎉", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#f0fdf4", isForShop, null, null);
    }

    public static String getOrderCancelHTMLVietnamese(String orderId, String userName, Set<ProductOrderItemDto> items, boolean isForShop) {
        String mainMessage = isForShop ? "Một đơn hàng của bạn đã bị hủy." : "Rất tiếc, đơn hàng của bạn đã bị hủy.";
        String noteMessage = isForShop ? "Vui lòng xem chi tiết nguyên nhân trong hệ thống để phản hồi nếu cần." : "Nếu bạn đã thanh toán, chúng tôi sẽ hoàn tiền trong thời gian sớm nhất. Xin lỗi vì sự bất tiện này.";
        return generateOrderEmailHTML("Đơn Hàng Đã Hủy", "❌", userName, mainMessage, orderId, buildOrderItemsTableHtml(items), noteMessage, "#fef2f2", isForShop, null, null);
    }

    public static String getEmailHTML(String code, String email, MailType mailType) {
        return getEmailHTML(code, email, mailType, null);
    }

    public static String getEmailHTML(String code, String email, MailType mailType, String feBaseUrl) {
        switch (mailType){
            case VERIFY_EMAIL -> {
                return getEmailVerificationHTMLVietnamese(code, email);
            }
            case RESET_PASSWORD -> {
                return getOtpVerificationEmailHTMLVietnamese(code, email);
            }
            case NEW_ORDER -> { return getNewOrderHTMLVietnamese(code, email, java.util.Collections.emptySet(), false, feBaseUrl); }
            case ORDER_CONFIRM -> { return getOrderConfirmHTMLVietnamese(code, email, java.util.Collections.emptySet(), false); }
            case ORDER_SHIPPING -> { return getOrderShippingHTMLVietnamese(code, email, java.util.Collections.emptySet(), false, feBaseUrl); }
            case ORDER_DELIVERING -> { return getOrderDeliveringHTMLVietnamese(code, email, java.util.Collections.emptySet(), false, feBaseUrl); }
            case ORDER_SUCCESS -> { return getOrderSuccessHTMLVietnamese(code, email, java.util.Collections.emptySet(), false); }
            case ORDER_CANCEL -> { return getOrderCancelHTMLVietnamese(code, email, java.util.Collections.emptySet(), false); }
            default -> throw new AnrealShopException("Unsupported mail type: " + mailType);
        }
    }
}
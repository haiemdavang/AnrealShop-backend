package com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy;

import com.haiemdavang.AnrealShop.tech.gemini.PromptStrategy;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import org.springframework.stereotype.Component;

@Component
public class ProductShortDescStrategy implements PromptStrategy {

    @Override
    public PromptType getType() {
        return PromptType.PRODUCT_DESCRIPTION;
    }

    @Override
    public String buildPrompt(String context) {
        return "Role: Bạn là một chuyên gia sáng tạo nội dung (Content Creator) cho thương hiệu thời trang cao cấp.\n" +
                "Task: Viết một đoạn mô tả ngắn (Short Description) cho sản phẩm dựa trên thông tin sau: " + context + "\n\n" +
                "Yêu cầu chi tiết:\n" +
                "1. Định dạng: Chỉ trả về văn bản thuần (Plain Text), không sử dụng thẻ HTML, không sử dụng Markdown (không dấu sao, không in đậm).\n" +
                "2. Độ dài: Tối đa 150 - 200 ký tự (khoảng 2-3 câu ngắn gọn).\n" +
                "3. Nội dung: Tập trung vào ưu điểm nổi bật nhất và cảm xúc mà sản phẩm mang lại. Phải thu hút khách hàng click vào xem chi tiết ngay lập tức.\n" +
                "4. Văn phong: Sang trọng, tinh tế và chuyên nghiệp.\n" +
                "5. Giới hạn phản hồi: Chỉ trả về duy nhất nội dung mô tả sản phẩm, không thêm lời dẫn giải, không chào hỏi, không để trong dấu ngoặc kép.";
    }
}

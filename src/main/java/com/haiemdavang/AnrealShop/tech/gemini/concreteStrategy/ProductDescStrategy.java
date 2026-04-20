package com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy;

import com.haiemdavang.AnrealShop.tech.gemini.PromptStrategy;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import org.springframework.stereotype.Component;

@Component
public class ProductDescStrategy implements PromptStrategy {

    @Override
    public PromptType getType() {
        return PromptType.PRODUCT_DESCRIPTION;
    }

    @Override
    public String buildPrompt(String context) {
        return "Hãy sinh đoạn mô tả ngắn chuẩn SEO, hấp dẫn cho sản phẩm sau: " + context
                + ". Yêu cầu: Role: Bạn là một chuyên gia Copywriter và chuyên gia SEO trong lĩnh vực thời trang cao cấp.\n" +
                "\n" +
                "Task: Hãy viết mô tả sản phẩm dựa trên thông tin được cung cấp. Kết quả trả về phải định dạng bằng HTML (chỉ sử dụng các thẻ <h2>, <p>, <ul>, <li>, <strong>) để hiển thị đẹp mắt trên website.\n" +
                "\n" +
                "Context sản phẩm: [DÁN THÔNG TIN/THÔNG SỐ SẢN PHẨM TẠI ĐÂY]\n" +
                "\n" +
                "Yêu cầu nội dung:\n" +
                "\n" +
                "Tiêu đề hấp dẫn (H2): Khơi gợi cảm xúc hoặc nêu bật giá trị cốt lõi của sản phẩm.\n" +
                "\n" +
                "Đoạn dẫn dắt: Viết 2-3 câu quyến rũ, nhấn mạnh vào phong cách sống hoặc giải pháp mà sản phẩm mang lại.\n" +
                "\n" +
                "Đặc điểm nổi bật (H2 + List): Liệt kê các ưu điểm về chất liệu, form dáng, công nghệ may.\n" +
                "\n" +
                "Hướng dẫn phối đồ (H2): Gợi ý cách mix & match để khách hàng dễ hình dung.\n" +
                "\n" +
                "Thông số kỹ thuật/Bảo quản (H2 + List): Trình bày rõ ràng, chuyên nghiệp.\n" +
                "\n" +
                "Yêu cầu SEO & Format:\n" +
                "\n" +
                "Sử dụng từ khóa tự nhiên, mật độ vừa phải.\n" +
                "\n" +
                "Nội dung tập trung vào lợi ích (Benefits) thay vì chỉ liệt kê tính năng (Features).\n" +
                "\n" +
                "Văn phong: Sang trọng, hiện đại, có tính thuyết phục cao.\n" +
                "\n" +
                "Lưu ý: Không viết mã CSS hay thẻ <html>/<body>. Chỉ trả về đoạn mã nội dung nằm trong các thẻ HTML cơ bản.Phản hồi chỉ chứa nội dung HTML duy nhất, không bao bọc trong khối mã Markdown (không có ```), không có lời dẫn hay giải thích gì thêm.";
    }
}

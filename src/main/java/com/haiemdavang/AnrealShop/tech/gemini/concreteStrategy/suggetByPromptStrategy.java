package com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy;

import com.haiemdavang.AnrealShop.tech.gemini.PromptStrategy;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import org.springframework.stereotype.Component;

@Component
public class suggetByPromptStrategy implements PromptStrategy {

    @Override
    public PromptType getType() {
        return PromptType.SUGGEST_TEXT;
    }

    @Override
    public String buildPrompt(String context) {
        return """
        [HỆ THỐNG]: BẠN LÀ CHATBOT TƯ VẤN NHANH TRÊN WEBSITE. BẠN KHÔNG PHẢI LÀ NGƯỜI VIẾT CONTENT QUẢNG CÁO.
        
        Dữ liệu sản phẩm:
        %s
        
        Nhiệm vụ: Dựa vào dữ liệu trên, tạo MỘT tin nhắn chat phản hồi khách hàng.
        
        [QUY TẮC SỐNG CÒN - TUYỆT ĐỐI TUÂN THỦ]:
        1. ĐỘ DÀI: Tối đa 3 câu văn (1 câu chào, 1 câu chốt). KHÔNG viết hashtag, KHÔNG viết tiêu đề, KHÔNG viết mô tả dài dòng.
        2. ĐỊNH DẠNG: Chỉ trả về mã HTML thuần. Tuyệt đối KHÔNG bọc trong ```html.
        3. NGÔN NGỮ: Vui vẻ, tự nhiên như người thật nhắn tin.
        4. NGOÀI LỀ: Nếu khách hỏi linh tinh, trêu lại 1 câu rồi bẻ lái về mua hàng.
        
        [CẤU TRÚC HTML BẮT BUỘC CHO MỖI SẢN PHẨM]:
        <li class="mb-5">
            <a href="/products/{Link}" data-path="/products/{Link}" title="{Tên sản phẩm}" class="font-normal text-gray-900 no-underline hover:text-blue-600 hover:underline cursor-pointer transition-colors duration-200 line-clamp-2">
                {Tên sản phẩm}
            </a>
            <img src="{ảnh}" alt="{Tên sản phẩm}" class="w-20 h-20 object-cover rounded-lg mt-2 block" />
            <p class="mt-1 text-sm text-gray-700"><strong>Giá:</strong> {Giá} - {Ghi đúng 1 ĐIỂM NỔI BẬT nhất, không quá 10 chữ}</p>
        </li>
        
        [MẪU ĐẦU RA BẠN PHẢI BẮT CHƯỚC Y HỆT]:
        <p>Dạ mình tìm thấy vài mẫu cực hợp với bạn nè!</p>
        <ul class="list-none pl-0">
            </ul>
        <p>Bạn ưng bé nào để mình check size cho nha?</p>
        """.formatted(context);
    }
}

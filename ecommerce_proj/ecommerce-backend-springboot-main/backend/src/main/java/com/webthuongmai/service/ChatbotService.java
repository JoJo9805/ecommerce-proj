package com.webthuongmai.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.webthuongmai.entity.Product;
import com.webthuongmai.entity.ProductVariant;
import com.webthuongmai.entity.Voucher;
import com.webthuongmai.repository.ProductRepository;
import com.webthuongmai.repository.ProductVariantRepository;
import com.webthuongmai.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    public String handleChat(String userMessage) {
        // 1. Lấy ngữ cảnh từ Database dựa trên nội dung tin nhắn
        String dbContext = getContextFromDatabase(userMessage);

        // 2. Định nghĩa System Instruction cho Gemini
        String systemInstruction = "Bạn là trợ lý ảo thông minh của sàn thương mại điện tử ShopZone.vn. "
                + "Hãy trả lời câu hỏi của người dùng bằng tiếng Việt một cách ngắn gọn, lịch sự và hữu ích dựa trên ngữ cảnh dữ liệu của cửa hàng dưới đây. "
                + "Nếu người dùng hỏi thông tin không có trong cơ sở dữ liệu, hãy trả lời khéo léo dựa trên kiến thức của bạn nhưng hướng họ về các sản phẩm/dịch vụ của ShopZone.\n\n"
                + "Dữ liệu thực tế từ Database ShopZone:\n" + dbContext;

        // 3. Gọi Gemini API qua Google GenAI SDK
        return callGemini(userMessage, systemInstruction);
    }

    private String getContextFromDatabase(String query) {
        StringBuilder context = new StringBuilder();
        String lowerQuery = query.toLowerCase();

        // 1. Xử lý câu hỏi về Khuyến mãi / Giảm giá / Voucher
        if (lowerQuery.contains("khuyến mãi") || lowerQuery.contains("giảm giá")
                || lowerQuery.contains("voucher") || lowerQuery.contains("coupon")) {
            List<Voucher> vouchers = voucherRepository.findAll();
            context.append("- Các chương trình khuyến mãi/Voucher hiện có:\n");
            int count = 0;
            for (Voucher v : vouchers) {
                if ("ACTIVE".equalsIgnoreCase(v.getStatus()) || v.getStatus() == null) {
                    context.append(String.format(
                            "  * Loại/Mã: %s, Trị giá giảm: %s, Đơn tối thiểu: %s, Số lượng còn lại: %d, Hạn dùng: %s\n",
                            v.getVoucherType(), v.getDiscountValue(), v.getMinOrderValue(),
                            v.getRemainingQuantity(), v.getEndDate()));
                    count++;
                    if (count >= 5) break;
                }
            }
            if (count == 0) {
                context.append("  * Hiện tại chưa có mã giảm giá mới được cập nhật trên hệ thống.\n");
            }
        }

        // 2. Xử lý câu hỏi tìm kiếm Sản phẩm
        List<Product> matchedProducts;
        if (lowerQuery.contains("sản phẩm") || lowerQuery.contains("có bán")
                || lowerQuery.contains("tìm") || lowerQuery.contains("mua")) {
            matchedProducts = productRepository.findAll().stream().limit(5).toList();
        } else {
            matchedProducts = new java.util.ArrayList<>();
            String[] keywords = lowerQuery.split("\\s+");
            for (String kw : keywords) {
                if (kw.length() > 2 && !Arrays.asList(
                        "có", "bán", "không", "bao", "nhiêu", "cửa",
                        "hàng", "shop", "tìm", "mua", "loại").contains(kw)) {
                    List<Product> searchResult = productRepository.findByProductNameContainingIgnoreCase(kw);
                    if (!searchResult.isEmpty()) {
                        matchedProducts.addAll(searchResult);
                        if (matchedProducts.size() >= 5) break;
                    }
                }
            }
        }

        if (!matchedProducts.isEmpty()) {
            context.append("- Danh sách sản phẩm liên quan:\n");
            int count = 0;
            for (Product p : matchedProducts) {
                List<ProductVariant> variants = productVariantRepository.findByProduct_ProductID(p.getProductID());
                String priceStr = "Liên hệ";
                if (variants != null && !variants.isEmpty()) {
                    ProductVariant v = variants.get(0);
                    priceStr = String.format("%,.0f VNĐ", v.getPrice());
                }
                context.append(String.format(
                        "  * Tên SP: %s, Thương hiệu: %s, Giá từ: %s, Mô tả: %s (Mã SP: %d)\n",
                        p.getProductName(),
                        p.getBrand() != null ? p.getBrand() : "ShopZone",
                        priceStr,
                        p.getDescription() != null ? p.getDescription() : "chưa cập nhật",
                        p.getProductID()));
                count++;
                if (count >= 5) break;
            }
        }

        if (context.length() == 0) {
            context.append("- Cửa hàng ShopZone chuyên cung cấp các mặt hàng chất lượng cao gồm thời trang, "
                    + "mỹ phẩm, công nghệ, gia dụng với giá cả tốt nhất và nhiều chương trình tích điểm hấp dẫn.");
        }

        return context.toString();
    }

    private String callGemini(String userPrompt, String systemInstruction) {
        try {
            // Khởi tạo Google GenAI Client với API key
            Client client = Client.builder().apiKey(apiKey).build();

            // Cấu hình generation
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(Part.fromText(systemInstruction)))
                    .temperature(0.3f)
                    .build();

            // Gọi model gemini-2.5-flash
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    userPrompt,
                    config);

            // Trả về text từ response
            if (response != null && response.text() != null) {
                return response.text();
            }
            return "Xin lỗi, hiện tại hệ thống AI đang bận. Vui lòng hỏi lại sau nhé!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Rất tiếc, đã xảy ra lỗi trong quá trình kết nối với máy chủ AI. Vui lòng thử lại!";
        }
    }
}
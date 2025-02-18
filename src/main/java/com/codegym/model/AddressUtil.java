package com.codegym.model;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressUtil {

    /**
     * Trích xuất tên thành phố hoặc tỉnh từ chuỗi địa chỉ.
     * Ví dụ:
     * - "Phan Thúc Duyện, Phường 4, Quận Tân Bình, Thành phố Hồ Chí Minh, 72106, Việt Nam"
     *   sẽ trả về "Ho Chi Minh"
     * - "Đường ABC, Phường XYZ, Thành phố Bắc Ninh, Tỉnh Bắc Ninh"
     *   sẽ trả về "Bac Ninh"
     *
     * @param address Chuỗi địa chỉ cần xử lý.
     * @return Tên thành phố hoặc tỉnh không dấu.
     */
    public static String extractCityOrProvince(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }

        // Tìm phần sau "thành phố" hoặc "tỉnh"
        Pattern pattern = Pattern.compile("(?:thành phố|tỉnh)\\s+([^,]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(address);
        String extracted = "";

        // Nếu có nhiều kết quả, chọn kết quả cuối cùng (thông tin ở cuối chuỗi)
        while (matcher.find()) {
            extracted = matcher.group(1);
        }

        extracted = extracted.trim();

        // Chuyển đổi thành dạng không dấu
        String normalized = Normalizer.normalize(extracted, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return withoutDiacritics;
    }
}

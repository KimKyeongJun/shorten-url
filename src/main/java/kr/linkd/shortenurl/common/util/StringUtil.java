package kr.linkd.shortenurl.common.util;


public class StringUtil {

    private static final char[] BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int FIXED_KEY_LENGTH = 8;

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String encodeBase62(byte[] hash) {
        if (hash == null || hash.length < FIXED_KEY_LENGTH) {
            throw new IllegalArgumentException("Hash must be at least 8 bytes long");
        }

        // 첫 8바이트를 부호 없는 값으로 변환
        long num = 0;
        for (int i = 0; i < FIXED_KEY_LENGTH; i++) {
            num = (num << FIXED_KEY_LENGTH) | ((long) hash[i] & 0xFF);
        }

        if (num == 0) {
            return "00000001"; // 기본값
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(BASE62[(int) (num % 62)]);
            num /= 62;
        }

        // 결과를 뒤집어 최종 Base62 문자열 생성
        String base62 = sb.reverse().toString();

        // 8글자로 줄이기
        return base62.length() > 8 ? base62.substring(0, 8) : base62;
    }

    public static byte[] getHash(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

}

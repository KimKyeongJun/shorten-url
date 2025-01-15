package kr.linkd.shortenurl.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilTest {

    @Test
    @DisplayName("Base62 인코딩이 정상적으로 동작한다")
    void testEncodeBase62() {
        // Given
        byte[] hash = StringUtil.getHash("https://example.com" + 1);

        // When
        String result = StringUtil.encodeBase62(hash);

        // Then
        assertThat(result)
                .hasSize(8)// 길이가 8이어야 함
                .matches("^[A-Za-z0-9]{8}$"); // Base62 패턴 확인)

    }

    @Test
    @DisplayName("해시 값이 null인 경우 예외를 발생시킨다")
    void testEncodeBase62WithNullHash() {
        // Given
        byte[] hash = null;

        // When & Then
        assertThatThrownBy(() -> StringUtil.encodeBase62(hash))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hash must be at least 8 bytes long");
    }

    @Test
    @DisplayName("해시 값의 길이가 8 미만인 경우 예외를 발생시킨다")
    void testEncodeBase62WithShortHash() {
        // Given
        byte[] hash = new byte[] {0x1A, 0x2B};

        // When & Then
        assertThatThrownBy(() -> StringUtil.encodeBase62(hash))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hash must be at least 8 bytes long");
    }

    @Test
    @DisplayName("SHA-256 해시 값을 정상적으로 생성한다")
    void testGetHash() {
        // Given
        String input = "test-input";
        byte[] expectedHash = { // 예상 SHA-256 값 (16진수로 계산된 값 필요)
                -82, 22, 8, -119, 99, 114, 114, 11, 110, -69, 88, 38, 30, 12, 0, -110,
                -58, 8, 50, 75, 8, 4, -68, -103, 38, 124, 23, 83, -103, 15, -86, -88
        };

        // When
        byte[] result = StringUtil.getHash(input);

        // Then
        assertThat(result).isEqualTo(expectedHash);
    }

    @Test
    @DisplayName("빈 문자열의 SHA-256 해시 값을 생성한다")
    void testGetHashWithEmptyString() {
        // Given
        String input = "";
        byte[] expectedHash = {
                (byte) 0xe3, (byte) 0xb0, (byte) 0xc4, 0x42, (byte) 0x98, (byte) 0xfc, 0x1c, 0x14,
                (byte) 0x9a, (byte) 0xfb, (byte) 0xf4, (byte) 0xc8, (byte) 0x99, 0x6f, (byte) 0xb9, 0x24,
                0x27, (byte) 0xae, 0x41, (byte) 0xe4, 0x64, (byte) 0x9b, (byte) 0x93, 0x4c,
                (byte) 0xa4, (byte) 0x95, (byte) 0x99, 0x1b, 0x78, 0x52, (byte) 0xb8, 0x55
        };

        // When
        byte[] result = StringUtil.getHash(input);

        // Debugging Output
        System.out.println("Expected Hash: " + Arrays.toString(expectedHash));
        System.out.println("Generated Hash: " + Arrays.toString(result));

        // Then
        assertThat(result).containsExactly(expectedHash);
    }
}
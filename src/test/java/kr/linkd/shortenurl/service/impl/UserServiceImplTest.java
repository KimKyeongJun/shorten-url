package kr.linkd.shortenurl.service.impl;

import kr.linkd.shortenurl.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=local")
class UserServiceImplTest {

    @Autowired
    public UserService userService;

    @Test
    @DisplayName("사용 중인 서비스의 API Key로 조회하면 사용자 아이디가 조회된다.")
    void validateAndExtractUserId() {
        // Given
        String apiKey = "c0b99385-0a89-438c-98ad-d6ea6d6c579d";

        // When
        Long userId = userService.validateAndExtractUserId(apiKey);

        // Then
        assertThat(userId).isNotNull();
    }

    @Test
    @DisplayName("사용 중인 서비스의 API Key로 조회하면 사용자 아이디가 조회된다.")
    void validateAndExtractUserId_NotFound() {
        // Given
        String apiKey = "c0b99385-0a89-438c-98ad-d6ea6d6c579d11111";

        // When
        Long userId = userService.validateAndExtractUserId(apiKey);

        // Then
        assertThat(userId).isNull();
    }
}
package kr.linkd.shortenurl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.linkd.shortenurl.controller.dto.ShortKeyCreateRequest;
import kr.linkd.shortenurl.service.ShortenUrlService;
import kr.linkd.shortenurl.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShortenUrlController.class)
@ActiveProfiles("local")
class ShortenUrlControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ShortenUrlService shortenUrlService;

    @MockitoBean
    protected UserService userService;

    @Value("${service.url}") // 프로파일 값 주입
    private String serviceUrl;


    @Test
    @DisplayName("단축 URL 생성 요청을 성공적으로 처리한다")
    void testShortenUrl() throws Exception {
        // Given
        ShortKeyCreateRequest shortKeyCreateRequest = new ShortKeyCreateRequest();
        shortKeyCreateRequest.setOriginalUrl("https://www.naver.com");
        shortKeyCreateRequest.setExpiredDate(LocalDate.now().plusDays(365));

        // Mock 동작 정의
        when(userService.validateAndExtractUserId("c0b99385-0a89-438c-98ad-d6ea6d6c579d")).thenReturn(1L);

        mockMvc.perform(
                post("/v1/api/shorten-urls")
                        .header("X-API-KEY", "c0b99385-0a89-438c-98ad-d6ea6d6c579d")
                        .content(objectMapper.writeValueAsString(shortKeyCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(serviceUrl + "/")));
    }

    @Test
    @DisplayName("단축 URL 생성 요청 시 유효하지 않은 API Key로 요청하면 401 응답이 반환된다.")
    void testShortenUrl_Invalid_API_KEY() throws Exception {
        // Given
        ShortKeyCreateRequest shortKeyCreateRequest = new ShortKeyCreateRequest();
        shortKeyCreateRequest.setOriginalUrl("https://www.naver.com");
        shortKeyCreateRequest.setExpiredDate(LocalDate.now().plusDays(365));

        // Mock 동작 정의
        when(userService.validateAndExtractUserId("12345678")).thenReturn(null);

        mockMvc.perform(
                        post("/v1/api/shorten-urls")
                                .header("X-API-KEY", "12345678")
                                .content(objectMapper.writeValueAsString(shortKeyCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("단축 URL 생성 요청 시 API_KEY가 존재하지 않으면 401을 응답한다")
    void testShortenUrl_NOT_API() throws Exception {
        // Given
        ShortKeyCreateRequest shortKeyCreateRequest = new ShortKeyCreateRequest();
        shortKeyCreateRequest.setOriginalUrl("https://www.naver.com");
        shortKeyCreateRequest.setExpiredDate(LocalDate.now().plusDays(365));

        mockMvc.perform(
                        post("/v1/api/shorten-urls")
                                .content(objectMapper.writeValueAsString(shortKeyCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("단축 URL로 원본 URL에 리다이렉션한다")
    void testRedirectToOriginal() throws Exception {
        // Mock 동작 정의
        when(shortenUrlService.getOriginalUrl("1234567")).thenReturn("https://example.com");
        // 단축 URL "1234567"가 데이터베이스에 존재한다고 가정
        mockMvc.perform(get("/1234567"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "https://example.com"));
    }
}
package kr.linkd.shortenurl.controller;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import kr.linkd.shortenurl.common.util.StringUtil;
import kr.linkd.shortenurl.controller.dto.ShortKeyCreateRequest;
import kr.linkd.shortenurl.service.ShortenUrlService;
import kr.linkd.shortenurl.service.UserService;
import kr.linkd.shortenurl.service.request.ShortKeyCreateServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;
    private final UserService userService;
    public static final String API_HEADER = "X-API-KEY";
    @Value("${service.url}") // 프로파일 값 주입
    private String serviceUrl;

    @PostMapping("/v1/api/shorten-urls")
    public ResponseEntity<String> shortenUrl(@RequestBody ShortKeyCreateRequest shortKeyCreateRequest, HttpServletRequest request) {
        String apiKey = request.getHeader(API_HEADER);

        // 인증키가 없는 경우 401 응답 처리
        if(StringUtils.isEmpty(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API_KEY IS NULL");
        }

        // 사용자 아이디 검증 처리
        Long userId = userService.validateAndExtractUserId(apiKey);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API_KEY");
        }

        byte[] hash = StringUtil.getHash(shortKeyCreateRequest.getOriginalUrl() + userId);
        String shortKey = StringUtil.encodeBase62(hash);

        ShortKeyCreateServiceRequest shortKeyCreateServiceRequest = new ShortKeyCreateServiceRequest();
        shortKeyCreateServiceRequest.setShortKey(shortKey);
        shortKeyCreateServiceRequest.setOriginalUrl(shortKeyCreateRequest.getOriginalUrl());
        shortKeyCreateServiceRequest.setUserId(userId);
        shortKeyCreateServiceRequest.setExpiredDate(shortKeyCreateRequest.getExpiredDate());

        String result = shortenUrlService.createShortUrl(shortKeyCreateServiceRequest);
        return ResponseEntity.ok(serviceUrl + "/" + result);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortKey) {
        String originalUrl = shortenUrlService.getOriginalUrl(shortKey);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", originalUrl).build();
    }

}

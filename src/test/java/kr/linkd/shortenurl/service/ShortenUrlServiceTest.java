package kr.linkd.shortenurl.service;

import kr.linkd.shortenurl.common.constant.LinkStatus;
import kr.linkd.shortenurl.common.exception.LinkExpiredException;
import kr.linkd.shortenurl.domain.UrlMapping;
import kr.linkd.shortenurl.domain.User;
import kr.linkd.shortenurl.repository.AccessLogRepository;
import kr.linkd.shortenurl.repository.UrlMappingRepository;
import kr.linkd.shortenurl.service.request.ShortKeyCreateServiceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "spring.profiles.active=local")
class ShortenUrlServiceTest {

    @Autowired
    public UrlMappingRepository urlMappingRepository;

    @Autowired
    public AccessLogRepository accessLogRepository;

    @Autowired
    public ShortenUrlService shortenUrlService;


    @BeforeEach
    void setUp() {
        accessLogRepository.deleteAllInBatch();
        urlMappingRepository.deleteAllInBatch();
    }


    @AfterEach
    void tearDown() {
        accessLogRepository.deleteAllInBatch();
        urlMappingRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("새로운 URL을 단축키로 변환하고 저장한다")
    void testCreateShortUrl_NewUrl() {
        // Given
        ShortKeyCreateServiceRequest shortKeyCreateServiceRequest = new ShortKeyCreateServiceRequest();
        shortKeyCreateServiceRequest.setUserId(1L);
        shortKeyCreateServiceRequest.setOriginalUrl("https://www.naver.com");
        shortKeyCreateServiceRequest.setExpiredDate(LocalDate.now().plusDays(10));
        shortKeyCreateServiceRequest.setShortKey("1234567");

        // When
        String result = shortenUrlService.createShortUrl(shortKeyCreateServiceRequest);

        //Then
        assertThat(result).isEqualTo(shortKeyCreateServiceRequest.getShortKey());
    }

    @Test
    @DisplayName("기존 URL이 있으면 기존 단축키를 반환한다")
    void testCreateShortUrl_ExistingUrl() {
        // Given
        String originalUrl = "https://www.naver.com";
        String shortKey = "1234567";

        UrlMapping existingMapping = UrlMapping
                .builder()
                .originalUrl(originalUrl)
                .shortKey(shortKey)
                .user(User.builder().id(1L).build())
                .build();

        urlMappingRepository.save(existingMapping);

        ShortKeyCreateServiceRequest shortKeyCreateServiceRequest = new ShortKeyCreateServiceRequest();
        shortKeyCreateServiceRequest.setUserId(1L);
        shortKeyCreateServiceRequest.setOriginalUrl(originalUrl);
        shortKeyCreateServiceRequest.setExpiredDate(LocalDate.now().plusDays(10));
        shortKeyCreateServiceRequest.setShortKey(shortKey);
        String result = shortenUrlService.createShortUrl(shortKeyCreateServiceRequest);

        assertThat(result).isEqualTo(shortKey);
    }

    @Test
    @DisplayName("단축키로 원본 URL을 성공적으로 찾는다")
    void testGetOriginalUrl_Found() {
        String shortKey = "1234567";
        String originalUrl = "https://www.naver.com";
        Long userId = 1L;

        UrlMapping mapping = UrlMapping
                .builder()
                .originalUrl(originalUrl)
                .shortKey(shortKey)
                .user(User.builder().id(userId).build())
                .build();

        urlMappingRepository.save(mapping);

        String result = shortenUrlService.getOriginalUrl(shortKey, "127.0.0.1");
        assertThat(originalUrl).isEqualTo(result);
    }

    @Test
    @DisplayName("존재하지 않는 단축키를 조회하면 예외가 발생한다")
    void testGetOriginalUrl_NotFound() {
        // Given
        String shortKey = "notfound";

        assertThatThrownBy(() -> shortenUrlService.getOriginalUrl(shortKey, "127.0.0.1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 링크가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("삭제된 단축키를 조회하면 예외가 발생한다.")
    void testGetOriginalUrl_Deleted() {
        String shortKey = "deleted";
        String originalUrl = "https://www.naver.com";
        Long userId = 1L;

        UrlMapping mapping = UrlMapping
                .builder()
                .originalUrl(originalUrl)
                .shortKey(shortKey)
                .user(User.builder().id(userId).build())
                .expiredDate(LocalDate.now().minusDays(3))
                .linkStatus(LinkStatus.DELETED)
                .build();
        urlMappingRepository.save(mapping);

        assertThatThrownBy(() -> shortenUrlService.getOriginalUrl(shortKey, "127.0.0.1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 링크가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("기한이 만료된 단축키를 조회하면 만료 예외가 발생한다.")
    void testGetOriginalUrl_Expired() {
        String shortKey = "expired";
        String originalUrl = "https://www.naver.com";
        Long userId = 1L;

        UrlMapping mapping = UrlMapping
                .builder()
                .originalUrl(originalUrl)
                .shortKey(shortKey)
                .user(User.builder().id(userId).build())
                .expiredDate(LocalDate.now().minusDays(3))
                .linkStatus(LinkStatus.EXPIRED)
                .build();
        urlMappingRepository.save(mapping);

        assertThatThrownBy(() -> shortenUrlService.getOriginalUrl(shortKey, "127.0.0.1"))
                .isInstanceOf(LinkExpiredException.class)
                .hasMessage("해당 링크는 만료된 링크입니다.");
    }

    @Test
    @DisplayName("만료일이 오늘인 링크를 만료 처리한다")
    void testExpireLinksForToday() {
        // Given
        LocalDate today = LocalDate.now();
        UrlMapping link1 = UrlMapping.builder()
                .originalUrl("https://example1.com")
                .shortKey("key1")
                .expiredDate(today) // 만료일이 오늘
                .linkStatus(LinkStatus.ACTIVE)
                .build();
        UrlMapping link2 = UrlMapping.builder()
                .originalUrl("https://example2.com")
                .shortKey("key2")
                .expiredDate(today.plusDays(1)) // 만료일이 오늘이 아님
                .linkStatus(LinkStatus.ACTIVE)
                .build();

        urlMappingRepository.saveAll(List.of(link1, link2));

        // When
        shortenUrlService.expiredUrl(today);

        // Then
        List<UrlMapping> allLinks = urlMappingRepository.findAll();
        assertThat(allLinks).filteredOn(link -> link.getShortKey().equals("key1"))
                .allMatch(link -> link.getLinkStatus() == LinkStatus.EXPIRED);
        assertThat(allLinks).filteredOn(link -> link.getShortKey().equals("key2"))
                .allMatch(link -> link.getLinkStatus() == LinkStatus.ACTIVE);
    }
}
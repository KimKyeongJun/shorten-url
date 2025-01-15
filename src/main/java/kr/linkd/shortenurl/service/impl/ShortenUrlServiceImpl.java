package kr.linkd.shortenurl.service.impl;

import kr.linkd.shortenurl.common.constant.LinkStatus;
import kr.linkd.shortenurl.common.exception.LinkExpiredException;
import kr.linkd.shortenurl.domain.AccessLog;
import kr.linkd.shortenurl.domain.UrlMapping;
import kr.linkd.shortenurl.domain.User;
import kr.linkd.shortenurl.repository.AccessLogRepository;
import kr.linkd.shortenurl.repository.UrlMappingRepository;
import kr.linkd.shortenurl.service.ShortenUrlService;
import kr.linkd.shortenurl.service.request.ShortKeyCreateServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortenUrlServiceImpl implements ShortenUrlService {

    private final UrlMappingRepository urlMappingRepository;
    private final AccessLogRepository accessLogRepository;

    @Transactional
    public String createShortUrl(ShortKeyCreateServiceRequest shortKeyCreateServiceRequest) {
        UrlMapping existing = urlMappingRepository.findByOriginalUrlAndUserId(shortKeyCreateServiceRequest.getOriginalUrl(), shortKeyCreateServiceRequest.getUserId());
        if (existing != null) {
            return existing.getShortKey();
        }
        UrlMapping mapping = UrlMapping
                .builder()
                .shortKey(shortKeyCreateServiceRequest.getShortKey())
                .originalUrl(shortKeyCreateServiceRequest.getOriginalUrl())
                .expiredDate(shortKeyCreateServiceRequest.getExpiredDate())
                .user(User.builder().id(shortKeyCreateServiceRequest.getUserId()).build())
                .build();
        urlMappingRepository.save(mapping);
        return mapping.getShortKey();
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortKey) {
        UrlMapping mapping = urlMappingRepository.findByShortKey(shortKey);

        // 링크가 존재하지 않은 경우 Not Found Exception 발생 (404.html)
        if (mapping == null || mapping.getLinkStatus() == LinkStatus.DELETED) {
            throw new NoSuchElementException("해당 링크가 존재하지 않습니다.");
        }

        // 링크가 만료된 경우 Expired Exception 발생 (expired.html)
        if(mapping.getLinkStatus().equals(LinkStatus.EXPIRED)) {
            throw new LinkExpiredException("해당 링크는 만료된 링크입니다.");
        }

        AccessLog log = AccessLog
                .builder()
                .urlMapping(mapping)
                .accessTime(LocalDateTime.now())
                .build();
        accessLogRepository.save(log);

        return mapping.getOriginalUrl();
    }
}

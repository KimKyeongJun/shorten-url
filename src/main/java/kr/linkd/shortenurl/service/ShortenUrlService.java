package kr.linkd.shortenurl.service;

import kr.linkd.shortenurl.service.request.ShortKeyCreateServiceRequest;

import java.time.LocalDate;

public interface ShortenUrlService {

    String createShortUrl(ShortKeyCreateServiceRequest shortKeyCreateServiceRequest);

    String getOriginalUrl(String shortKey, String requestIp);

    void expiredUrl(LocalDate localDate);
}

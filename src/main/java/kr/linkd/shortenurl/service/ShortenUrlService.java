package kr.linkd.shortenurl.service;

import kr.linkd.shortenurl.service.request.ShortKeyCreateServiceRequest;

public interface ShortenUrlService {

    String createShortUrl(ShortKeyCreateServiceRequest shortKeyCreateServiceRequest);

    String getOriginalUrl(String shortKey);

}

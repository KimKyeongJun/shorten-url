package kr.linkd.shortenurl.repository;


import kr.linkd.shortenurl.common.constant.LinkStatus;
import kr.linkd.shortenurl.domain.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByOriginalUrl(String originalUrl);

    UrlMapping findByShortKey(String shortKey);

    UrlMapping findByOriginalUrlAndUserId(String originalUrl, Long userId);

    List<UrlMapping> findAllByExpiredDateAndLinkStatus(LocalDate date, LinkStatus linkStatus);
}

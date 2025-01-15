package kr.linkd.shortenurl.repository;


import kr.linkd.shortenurl.domain.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByOriginalUrl(String originalUrl);

    UrlMapping findByShortKey(String shortKey);

    UrlMapping findByOriginalUrlAndUserId(String originalUrl, Long userId);

}

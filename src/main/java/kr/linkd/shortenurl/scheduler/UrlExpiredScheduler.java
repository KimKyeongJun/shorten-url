package kr.linkd.shortenurl.scheduler;

import kr.linkd.shortenurl.service.ShortenUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UrlExpiredScheduler {

    private final ShortenUrlService shortenUrlService;

    /**
     * 링크를 만료처리하는 스케줄러
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void expiredLink() {
        shortenUrlService.expiredUrl(LocalDate.now().minusDays(1));
    }


}

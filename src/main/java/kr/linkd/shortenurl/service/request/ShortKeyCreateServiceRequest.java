package kr.linkd.shortenurl.service.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShortKeyCreateServiceRequest {

    private String originalUrl;

    private String shortKey;

    private LocalDate expiredDate;

    private Long userId;

}

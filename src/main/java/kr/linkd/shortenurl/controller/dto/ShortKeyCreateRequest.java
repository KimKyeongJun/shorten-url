package kr.linkd.shortenurl.controller.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShortKeyCreateRequest {

    private String originalUrl;

    private LocalDate expiredDate;

}

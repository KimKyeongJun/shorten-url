package kr.linkd.shortenurl.service;

public interface UserService {
    Long validateAndExtractUserId(String apiKey);
}

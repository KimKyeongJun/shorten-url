package kr.linkd.shortenurl.service.impl;

import kr.linkd.shortenurl.domain.User;
import kr.linkd.shortenurl.repository.UserRepository;
import kr.linkd.shortenurl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long validateAndExtractUserId(String apiKey) {
        // API Key가 데이터베이스에 존재하는지 확인
        User user  = userRepository.findByApiKey(apiKey);
        if (user == null) {
            return null; // 유효하지 않으면 null 반환
        }
        return user.getId(); // 유효하면 ID 반환
    }
}

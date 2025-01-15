package kr.linkd.shortenurl.repository;

import kr.linkd.shortenurl.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByApiKey(String apiKey);
}

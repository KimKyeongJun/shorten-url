package kr.linkd.shortenurl.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("단축 식별자 ID")
    private UrlMapping urlMapping;

    @Column(nullable = false)
    @Comment("접속 시간")
    private LocalDateTime accessTime;

    @Column
    @Comment("요청 IP")
    private String requestIp;

}

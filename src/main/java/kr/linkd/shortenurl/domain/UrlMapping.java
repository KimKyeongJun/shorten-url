package kr.linkd.shortenurl.domain;

import jakarta.persistence.*;
import kr.linkd.shortenurl.common.constant.LinkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(unique = true, nullable = false)
    @Comment("식별자")
    private String shortKey;

    @Column(nullable = false)
    @Comment("원본 URL")
    private String originalUrl;

    @Column
    @Comment("만료일")
    private LocalDate expiredDate;

    @Column
    @Enumerated(EnumType.STRING)
    @Comment("상태 - ACTIVE(활성), DELETED(삭제), EXPIRED(만료)")
    @Builder.Default
    private LinkStatus linkStatus = LinkStatus.ACTIVE;

    // 데이터가 생성되어 저장될 때 시간이 자동 저장
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 데이터를 변경할 때 시간이 자동 저장
    @LastModifiedDate
    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

}

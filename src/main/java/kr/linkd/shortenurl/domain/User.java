package kr.linkd.shortenurl.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String apiKey;

    // 데이터가 생성되어 저장될 때 시간이 자동 저장
    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 데이터를 변경할 때 시간이 자동 저장
    @LastModifiedDate
    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

}

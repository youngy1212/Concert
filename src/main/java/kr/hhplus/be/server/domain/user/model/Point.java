package kr.hhplus.be.server.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private long id;

    private Long amount;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Integer version;

    @Builder
    private Point(Long amount, User user) {
        this.amount = amount;
        this.user = user;
    }

    public static Point create(Long amount, User user) {
        return Point.builder()
                .amount(amount)
                .user(user)
                .build();
    }

    //포인트 충전
    public void charge(Long amount) {
        this.amount += amount;
    }

}

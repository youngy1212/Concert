package kr.hhplus.be.server.infrastructure.concert;

import kr.hhplus.be.server.domain.concert.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

}

package kr.hhplus.be.server.domain.concert.repository;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.Seat;

public interface ConcertQuery {

    Optional<Concert> findById(final long id);

    List<ConcertSchedule> findAllByConcertId(long concertId);

    List<Long> findByConcertScheduleId(long concertScheduleId);

    Optional<ConcertSchedule> getConcertSchedule(long concertScheduleId);

    Optional<Seat> getSeat(long seat);
}

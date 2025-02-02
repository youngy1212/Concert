package kr.hhplus.be.server.domain.concert.service.dto;

import java.util.List;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;

public record ConcertDateInfo(List<ConcertSchedule> concertSchedules){ }

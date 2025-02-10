package kr.hhplus.be.server.domain.token.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenScheduled {

    private final QueueService queueService;

    @Transactional
    //@Scheduled(fixedDelay = 1000) //1초 간격으로 실행
    public void activeQueue(){
        queueService.transferWaitingToActive(5);
    }

}

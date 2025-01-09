package kr.hhplus.be.server.domain.token;


import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueTokenScheduled {


    private final QueueTokenReader queueTokenReader;

    @Transactional
    @Scheduled(fixedDelay = 1000) //1초 간격으로 실행
    public void processActiveTokens(){
        System.out.println("processActiveTokens 실행됨");


        List<QueueToken> tokens = queueTokenReader.findTopNOrderByEnqueuedAt(QueueTokenStatus.PENDING, 10);

        for(QueueToken token : tokens){
            token.tokenActive(LocalDateTime.now());
        }
    }

}
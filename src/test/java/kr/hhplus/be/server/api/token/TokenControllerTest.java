package kr.hhplus.be.server.api.token;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import kr.hhplus.be.server.application.ConcertQueueTokenFacade;
import kr.hhplus.be.server.application.dto.QueueTokenInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertQueueTokenFacade concertQueueTokenFacade;

    @DisplayName("토큰 발급한다. TokenResponse 리턴")
    @Test
    void issueTokenReturnsTokenResponse() throws Exception {
        // given
        Long userId = 1L;
        Long concertId = 2L;
        String queueTokenId = "TOKEN_UUID";
        LocalDateTime expiresAt = LocalDateTime.now();

        QueueTokenInfo queueTokenInfo = new QueueTokenInfo(queueTokenId, userId,concertId,expiresAt);
        when(concertQueueTokenFacade.issueQueueToken(userId, concertId)).thenReturn(queueTokenInfo);

        // when // then
        mockMvc.perform(get("/tokens/{userId}/{concertId}", userId, concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenId").value(queueTokenId));
    }



}
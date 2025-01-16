package kr.hhplus.be.server.api.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.user.dto.ChargeRequest;
import kr.hhplus.be.server.domain.user.service.UserCommandService;
import kr.hhplus.be.server.domain.user.service.UserQueryService;
import kr.hhplus.be.server.domain.user.service.dto.ChargeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserCommandService userCommandService;

    @MockitoBean
    private UserQueryService userQueryService;


    @DisplayName("포인트롤 충전한다. ChargeResponse을 응답")
    @Test
    void chargePointReturnsChargeResponse() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        ChargeRequest request = ChargeRequest.builder()
                .userId(userId)
                .amount(amount)
                .build();

        ChargeDto chargeDto = new ChargeDto(userId, amount);
        when(userCommandService.chargePoint(userId, amount)).thenReturn(chargeDto);

        // when // then
        mockMvc.perform(post("/point/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) //
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.amount").value(amount));

    }

    @DisplayName("포인트롤 조회한다. PointResponse로 응답")
    @Test
    void getPointReturnsPointResponse() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;

        ChargeDto chargeDto = new ChargeDto(userId, amount);
        when(userQueryService.getPoint(userId)).thenReturn(chargeDto);

        // when // then
        mockMvc.perform(get("/point/{userId}",userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalAmount").value(amount));

    }

}
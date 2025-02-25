package kr.hhplus.be.server.api.user;

import static org.assertj.core.api.Assertions.assertThat;

import kr.hhplus.be.server.interfaces.api.user.dto.ChargeRequest;
import kr.hhplus.be.server.interfaces.api.user.dto.ChargeResponse;
import kr.hhplus.be.server.interfaces.api.user.dto.PointResponse;
import kr.hhplus.be.server.domain.user.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.user.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PointE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @DisplayName("본인의 포인트를 조회한다. (E2E)")
    @Test
    void getPointE2E() {
        // given
        User user = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Point point = pointJpaRepository.save(Point.create(1000L, user));

        String url = "http://localhost:" + port + "/point/" + user.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<PointResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PointResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PointResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTotalAmount()).isEqualTo(point.getAmount());
    }

    @DisplayName("본인의 포인트를 충전한다.(E2E)")
    @Test
    void chargePointE2E() {
        // given
        User user = userJpaRepository.save(User.create("유저", "eamil@naemver"));
        Point point = pointJpaRepository.save(Point.create(1000L, user));


        ChargeRequest request = ChargeRequest.builder()
                .userId(user.getId())
                .amount(2000L)
                .build();

        String url = "http://localhost:" + port + "/point/charge";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChargeRequest> entity = new HttpEntity<>(request,headers);

        // When
        ResponseEntity<ChargeResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ChargeResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ChargeResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAmount()).isEqualTo(point.getAmount()+2000L);
    }

}

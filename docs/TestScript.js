import http from 'k6/http';
import {check} from 'k6';

// 테스트 데이터 생성
const users = [];
for (let i = 1; i <= 100; i++) {
    users.push({
        userId: i,
        tokenId: `token-${i}`,
    });
}


// 부하 테스트 설정
export const options = {
    vus: 100, // 가상 사용자 100명
    iterations: 1000, // 각 유저가 10번씩 호출 (100명 * 10 = 1000 호출)
};

// 테스트 실행
export default function () {
    // 랜덤 유저 데이터 선택
    const user = users[__VU % users.length];// 현재 VU(Virtual User) ID를 기반으로 유저 선택
    const seatId = Math.floor(Math.random() * 50) + 42; // 50개의 좌석
    const concertScheduleId =  1; // 1개의 콘서트

    // 요청 데이터 생성
    const payload = JSON.stringify({
        userId: user.userId,
        seatId: seatId,
        concertScheduleId: concertScheduleId,
        tokenId: user.tokenId,
    });

    // 요청 헤더 설정
    const headers = { 'Content-Type': 'application/json' };

    // API 호출
    const res = http.post('http://localhost:8087/concert/seats/reservation', payload, { headers });

    // 응답 상태 코드 확인
    check(res, {
        'is status 200': (r) => r.status === 200, // 성공 응답 확인
        'response body exists': (r) => r.body !== '', // 응답 본문 존재 확인
    });

    sleep(1); // 각 요청 간 대기
}
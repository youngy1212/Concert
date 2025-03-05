import http from 'k6/http';
import {check, sleep} from 'k6';

// 테스트 데이터 생성
export let options = {
    stages: [
        { duration: "15s", target: 2000 },   // 15초 동안 2000명의 유저 도달
        { duration: "15s", target: 5000 },  // 다음 15초 동안 5000명의 유저 도달
        { duration: "15s", target: 10000 },  // 다음 15초 동안 10000명의 유저 도달
        { duration: "15s", target: 20000 },  // 마지막 15초 동안 20000명의 유저 도달
    ],
};


function generateUniqueId() {
    return Math.floor(Math.random() * Number.MAX_SAFE_INTEGER);
}


// 부하 테스트 설정
export default function () {
    let userId = generateUniqueId();
    let concertId =  Math.floor(Math.random() * 2) + 1; //요청오는 콘서트가 한두개라고 가정

    let res = http.get(`http://localhost:8087/waitingQueue/${userId}/${concertId}`);

    check(res, {
        "is status 200": (r) => r.status === 200,
    });

    sleep(1);
}

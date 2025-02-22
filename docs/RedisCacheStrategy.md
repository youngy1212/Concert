# 캐시 보고서

## 목적 
본 보고서는 콘서트 예약 시스템에 캐시 적용 전에 고려할 캐시 전략을 전달하기 위한 것입니다. 
실제로 어떤 전략을 선택하고 시스템에 반영할지는 이후의 RedisCache.md 문서를 참고해주시기 바랍니다.


### 캐시의 이점
캐시는 자주 사용되는 데이터를 임시 저장소에 보관하여 빠르게 빠르게 접근할 수 있도록 함으로써 시스템의 성능을 향상시키고, 
응답 시간을 단축하며, DB에 가해지는 부하를 줄일 수 있습니다.

캐시를 적절히 활용하면 다음과 같은 이점을 얻을 수 있습니다.

1. 성능 향상: 메모리에 저장된 데이터를 이용하여 DB 조회 시간을 단축합니다.
2. 부하 분산: 캐시를 통해 DB에 대한 읽기 요청을 줄여 시스템 부하를 분산시킵니다.
3. 확장성 개선: 트래픽 증가에 대비하여 효율적인 리소스 활용이 가능합니다. 

하지만 캐시의 사용에는 데이터 일관성 유지, 용량 관리, 캐시 무효화 전략 등 고려해야 할 사항이 많습니다. 
이러한 사항을 위해 캐시 전략에 대해 먼저 논의하고, 콘서트 예약 시스템에 사용할 수 있는 전략을 선택하고자 합니다.


### 캐시의 고려사항

캐시를 사용할 때는 다음과 같은 요소를 고려해야 합니다.

- 어떤 데이터를 캐시에 저장할 것인가
- 얼마만큼의 데이터를 저장할 것인가
- 데이터를 얼마나 오래 저장할 것인가
이러한 요소를 효과적으로 관리하기 위해서는 **캐시 전략(Cache Strategy)**이 필요합니다.

---

캐싱 전략에 필요한 선수 지식: Cache Hit와 Cache Miss
- Cache Hit: 캐시에 원하는 데이터가 있어 즉시 데이터를 가져오는 경우로, 빠른 응답이 가능합니다.
- Cache Miss: 캐시에 데이터가 없어 DB에서 데이터를 가져와야 하는 경우로, 상대적으로 응답 시간이 길어집니다.
캐싱 전략은 Cache Hit 비율을 높이고 Cache Miss로 인한 성능 저하를 최소화하는 것을 목표로 합니다.


## 캐싱 전략 패턴 종류

### Cache Aside 
데이터를 찾을 때 우선 캐시에 저장된 데이터가 있는지 확인하는 전략입니다. 
만일 캐시에 데이터가 없으면 DB에서 조회하여 캐시에 저장합니다.

- 특징:
  - 캐시와 DB가 분리되어 있어 캐시 장애 시에도 서비스 운영이 가능합니다.
  
- 사용 사례 :
  1. 반복적인 읽기가 많은 데이터

- 주의사항:
  1. 캐시에 연결된 접속이 많다면 Redis 등이 다운될 때 순간적으로 DB에 부하가 몰릴 수 있습니다.
  2. 캐시와 DB 간의 데이터 정합성 유지에 신경 써야 합니다.

  
### Read Through
캐시를 통해서만 데이터를 읽어오는 전략입니다.
데이터 조회 요청이 있을 때 캐시에 데이터가 없으면 캐시 제공자가 DB에서 데이터를 가져와 캐시에 저장하고 반환합니다.

- 특징:
  - 캐시에 데이터가 항상 존재하도록 보장합니다.
  - 캐시와 DB 간의 데이터 일관성을 유지하기 용이합니다.

- 사용 사례 :
  1. 읽기 빈도가 매우 높은 데이터
  2. 데이터 일관성이 중요하고 캐시와 DB의 싱크를 유지해야 하는 경우

- 주의사항:
  1. 캐시 장애 시 서비스 전체에 영향을 미칠 수 있으므로 캐시의 가용성을 높여야 합니다.
  2. 캐시에 대한 의존성이 높아집니다.


### Write Through
데이터를 쓸 때 캐시와 DB에 동시에 쓰는 방식입니다.
애플리케이션은 캐시에 데이터를 쓰며, 캐시 제공자가 이를 DB에 반영합니다.

- 특징:
  - 데이터 쓰기 시 캐시와 DB의 데이터 일관성을 유지합니다.
  - 쓰기 작업의 지연이 발생할 수 있습니다.

- 사용 사례:
  1. 실시간으로 데이터 일관성이 필요한 경우
  2. 쓰기 빈도가 상대적으로 낮은 시스템

- 주의사항:
  1. 쓰기 시 지연이 발생하여 성능에 영향을 줄 수 있습니다.
  2. 캐시 제공자가 DB에 쓰기 실패 시 오류 처리가 필요합니다.


### Write Around
쓰기 시에 캐시를 생략하고 직접 DB에 데이터를 쓰는 방식입니다.
캐시는 주로 읽기 요청 시에만 관여합니다.

- 특징:
  - 쓰기 시 캐시 부하를 줄여줍니다.
  - 처음 데이터가 쓰여진 후 캐시에 데이터가 없으므로 첫 번째 읽기에서 Cache Miss가 발생합니다.

- 사용 사례:
  1. 쓰기 빈도가 높고 읽기 빈도가 낮은 경우
  2. 데이터 생성 후 즉시 읽지 않는 경우

- 주의사항:
  1. 처음 읽기 시 Cache Miss로 인한 지연이 발생합니다.
  2. 데이터 일관성 유지에 주의가 필요합니다.

### Write Back
데이터를 캐시에만 쓰고,
일정 주기나 조건에 따라 비동기로 캐시의 데이터를 DB에 반영하는 방식입니다.

- 특징:
  - 쓰기 성능이 향상됩니다.
  - 캐시와 DB 간 데이터 일관성 지연이 발생할 수 있습니다.
  - 캐시 장애 시 데이터 유실 위험이 있습니다.

- 사용 사례:
  1. 읽기 및 쓰기 빈도가 모두 높은 시스템
  2. 일시적인 데이터 불일치가 허용되는 경우

- 주의사항:
  1. 데이터 일관성 유지에 대한 추가적인 설계가 필요합니다.
  2. 캐시 장애에 대비한 복구 전략이 필요합니다.



### 캐시 스탬피드 현상

**캐시 스탬피드(Cache Stampede)**란 캐시에 저장된 데이터가 만료되었을 때 다수의 요청이 동시에 DB로 몰리는 현상을 말합니다. 
인기 있는 데이터의 캐시가 만료되면 대량의 사용자 요청이 캐시를 거치지 않고 한꺼번에 DB로 전달되어 DB 부하가 급증하고, 
이것이 서비스 장애로 이어질 수 있습니다.

- 문제점:
  1. 서버 과부하 발생: 동시 다발적인 DB 접근으로 인해 서버 자원이 고갈될 수 있습니다.
  2. 응답 시간 지연: 사용자에게 제공되는 서비스의 응답 시간이 길어집니다.
  3. 서비스 장애 위험: 최악의 경우 DB 다운 등의 심각한 장애로 이어질 수 있습니다.

- 발생 원인 분석
  1. 동일한 만료 시간 설정: 캐시 아이템들이 동일한 TTL(Time To Live)을 가지면 특정 시점에 동시에 만료됩니다.
  2. 인기 데이터의 캐시 미스: 많은 사용자가 접근하는 데이터의 캐시가 만료되면 다수의 요청이 동시에 DB로 전달됩니다.
  3. 캐시 전략 미비: 캐시 미스 시의 대처 전략이 없거나 부족하면 캐시 스탬피드 현상이 발생합니다.


- 해결 방안
  1. 랜덤한 TTL 설정:
  캐시 항목의 만료 시간을 설정할 때 랜덤성을 부여하여 캐시 만료 시점을 분산시킵니다.
  2. 캐시 미스 락(Mutex) 사용:
  캐시 미스 발생 시 첫 번째 요청만 DB에 접근하도록 하고, 다른 요청은 대기하게 합니다.
  3. 백그라운드 재계산:
  캐시 만료 시간이 다가오면 백그라운드에서 미리 캐시를 갱신합니다.

--- 
요약: 본 보고서는 캐시의 이점과 주요 캐싱 전략, 그리고 캐시 사용 시 발생할 수 있는 문제점인 캐시 스탬피드 현상에 대해 정리하였습니다.
이를 기반으로 콘서트 예약 시스템에 최적의 캐시 전략을 선택하여 적용하고자 합니다.


--- 
### 참고자료 
- https://inpa.tistory.com/entry/REDIS-%F0%9F%93%9A-%EC%BA%90%EC%8B%9CCache-%EC%84%A4%EA%B3%84-%EC%A0%84%EB%9E%B5-%EC%A7%80%EC%B9%A8-%EC%B4%9D%EC%A0%95%EB%A6%AC#write_around_%ED%8C%A8%ED%84%B4
- https://www.youtube.com/watch?v=92NizoBL4uA
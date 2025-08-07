# LEAD:ME

## 🚩프로젝트 소개

OTT 서비스 사용자들은 자신의 취향에 맞는 콘텐츠를 찾기 어렵다는 문제를 자주 경험합니다.  
LEAD:ME는 사용자의 명시적 입력 없이 **행동 데이터만으로** 개인화된 콘텐츠를 추천하는 백엔드 시스템을 설계하고 구축했습니다.

<br/>

## 🎯 개발 목표

- 사용자 행동 데이터를 실시간으로 반영하는 백엔드 시스템을 구축합니다.
- **인프라 사용 비용을 최소화**하면서도, **최소 사양 환경에서도 안정적으로 운영 가능한** 백엔드 아키텍처를 구성합니다.
- 장애나 예외 상황에서도 사용자 행동 데이터가 안전하게 수집·저장되도록 아키텍처를 설계합니다.

<br/>

<br/>

## 🏛️ 시스템 설계

### 초기 버전
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/512971dd-0e55-4f75-b2a9-86d5640e1632" />


**한계점**
- API 서버와 추천 로직이 하나의 서버에 집중되어 트래픽 증가 시 병목이 발생할 수 있습니다.
- 사용자 행동 데이터를 실시간으로 반영하므로 RDB에 과도한 부하가 발생합니다.
- MySQL은 벡터 연산에 최적화되지 않아, 유사도 검사를 반복할 경우 성능 저하 발생합니다.
- 장애 발생 시 사용자 행동 데이터가 유실될 가능성이 존재합니다.

<br/>

### 최종 버전

<img width="1400" height="1000" alt="image" src="https://github.com/user-attachments/assets/548847a9-ef59-4a32-a338-969cfbdea75b" />

**보완점**
- API 서버(Spring Boot)와 추천 로직 서버(FastAPI)를 분리하여 역할과 책임을 명확히 분리했습니다.
- MongoDB와 Redis를 함께 사용하여 RDB의 부하를 줄이고, 데이터 접근 속도를 향상시켰습니다.
- 장애 발생 시에도 사용자 행동 로그가 유실되지 않도록, 로그 기반 상태 관리 및 복구 메커니즘을 도입했습니다.
- 벡터 유사도 연산 성능 병목을 해결하기 위해 PostgreSQL(pgvector)로 전환하여, 유사도 기반 검색의 효율을 높였습니다.

<br/>

## 초기 버전과 최종 버전의 성능 비교

### 공통 사항
- 가상 사용자 수 : 100명
- 총 요청 수 : 1000회

<br/>

### 1. 시나리오 : 행동 기반 가중치 업데이트
```
사용자 행동 → 컨텐츠 메타 정보 조회 → 사용자 가중치 업데이트 → 사용자 벡터 재계산
```
<br/>

| 서버 구조   | 평균 응답 속도 |
|------------|----------------|
| 초기 버전   | 2.24s        |
| 최종 버전   | 743.71ms       |

<br/>  

초기 버전에 비해 최종 버전의 응답 시간이 약 **3.2배**가 단축되었습니다. 


<br/>

**📍초기 버전**  

<img width="1332" height="114" alt="image" src="https://github.com/user-attachments/assets/c3e23ca6-89b9-4c30-88c0-bbd5f6d24353" />

<br/>
<br/> 



**📍최종 버전**  

<img width="1370" height="112" alt="image" src="https://github.com/user-attachments/assets/7e341ba4-2176-456f-b74d-7aa9f0ad0e54" />

<br/>  
<br/>  

### 2. 시나리오 : 행동 기반 실시간 동기 처리 구조
```
사용자 행동 → DB에 가중치, 벡터값 업데이트
```
<br/>


| 서버 구조   | 실시간으로 발생하는 DB I/O | 평균 응답 시간
|------------|----------------|----------------|
| 초기 버전   | 200 ~ 250번       |     2.7s       |
| 최종 버전   | 0번               |     1.54s      |

<br/>  

초기 버전에 비해 최종 버전의 응답 시간이 **1.16초** 단축되었습니다.  
추천 서버는 벡터 값을 Redis에 먼저 저장하고, 이후 필요한 경우에만 PostgreSQL과 동기화하기 때문에, 사용자가 행동을 해도 DB I/O가 발생하지 않았습니다.  

<br/>

**📍초기 버전**  

<img width="1388" height="126" alt="image" src="https://github.com/user-attachments/assets/c7e0cd67-a9d1-4986-9098-8b7565279c4b" />

<br/>
<br/> 

**📍최종 버전**  

<img width="1361" height="115" alt="image" src="https://github.com/user-attachments/assets/feea1371-7f1d-4158-a5eb-c9a12795e6a0" />

<br/>
<br/>

### 데이터베이스 분리 이후 성능 지표

기존에는 모든 데이터를 PostgreSQL 단일 DB에 저장하여, 사용자 행동 로그 저장, 가중치 업데이트, 벡터 계산, 유사도 기반 추천을 모두 MySQL 하나에서 처리하고 있었습니다.  
이 구조는 **데이터 증가에 따라 쓰기 부하가 높아지고**, **추천 응답 속도에도 영향을 미치는 문제**가 있었습니다.  

이에 따라 구조를 다음과 같이 분리했습니다.
- **MongoDB** : 사용자 행동 로그 저장, 실패 로그 관리
- **PostgreSQL** : 사용자 벡터 저장 및 유사도 연산 기반 추천 처리  

####  MongoDB의 CPU 사용률 
CPU 사용률은 40%로 안정적입니다.  

<img width="2422" height="947" alt="mongoDBcpu" src="https://github.com/user-attachments/assets/a79f9102-f8e9-462c-9605-4f0b096974e8" />

#### PostgreDB의 CPU 사용률
CPU 사용률은 10% 미만으로 안정적입니다.

<img width="2414" height="946" alt="postgreDB_cpu" src="https://github.com/user-attachments/assets/fbf4637f-9c6a-407a-a41b-9080aa309efa" />

<br/>  
<br/>  


##  📊 플로우 차트

<img width="1400" height="1000" alt="image" src="https://github.com/user-attachments/assets/548847a9-ef59-4a32-a338-969cfbdea75b" />


### 사용자 행동이 발생한 경우
```
1. MongoDB에 사용자 행동 로그를 저장합니다.
 - 행동 정보(조회, 좋아요 등)를 이벤트 발생 시마다 기록합니다.

2. 웹 서버(Spring)에서 사용자 행동에 대한 가중치 업데이트 이벤트를 RabbitMQ로 메시지를 보냅니다.
 - 메시지에는 사용자 ID, 컨텐츠 ID, 행동 타입, 컨텐츠 메타 정보, 상태값 등이 포함됩니다.

3. 추천 서버(FastAPI)가 RabbitMQ로부터 가중치 업데이트 이벤트 메시지를 전달받습니다.

4. 추천 서버에서 사용자의 선호 메타 정보, 가중치를 조회합니다.

5. 조회된 정보와 전달받은 행동 로그 메시지를 통해 사용자의 기존 가중치를 업데이트 합니다.
 - 예시) 사용자가 '스릴러' 콘텐츠를 시청하는 경우, '스릴러'에 대한 가중치가 증가합니다.

6. 갱신된 가중치를 기반으로 사용자 벡터를 계산후, 계산된 사용자 벡터를 Redis에 캐싱합니다.
```

### 추천이 발생한 경우
```
1. 웹 서버(Spring)에서 Redis에 캐싱된 사용자 벡터를 조회합니다.

2. 조회된 벡터를 PostgreSQL에 동기화하여 정합성을 유지합니다.

3. PostgreSQL에서 벡터 유사도 기반 쿼리를 통해 추천 콘텐츠를 계산합니다.

4. 최종 추천 콘텐츠 목록을 사용자에게 응답으로 전달합니다.
```

### 실패 로그를 재실행하는 경우

```
1. 스케줄러 서버(FastAPI)는 1분 간격으로 사용자 행동 로그 중 status = FAIL로 기록된 항목을 조회합니다.
 - 행동 로그 처리 중 오류로 인해 가중치 반영에 실패한 경우, 로그는 `FAIL` 상태로 기록됩니다.
 - 스케줄러가 주기적으로 이 실패 로그를 조회하여, 정상적으로 가중치가 반영되지 않은 사용자의 상태를 복구합니다.

2. 조회된 실패 로그에 대해 가중치 업데이트 로직을 재실행하여, 누락되었던 사용자 가중치를 복구합니다.
```

## 고민한 기술 적용 사례

### 🎬 장르 임베딩 개선

기존 임베딩 모델은 일반 문맥 기반으로 학습되었기 때문에, 영화 도메인에 적합하지 않은 임베딩 결과를 보였습니다. <br/>
예를 들어, 실제로 유사한 장르인 Thriller와 Action조차 서로 전혀 다른 벡터로 표현되는 문제가 있었습니다.

이러한 한계를 해결하기 위해 영화-장르 간 관계를 그래프로 구성하고 이를 기반으로 학습한 도메인 특화 임베딩 모델을 새롭게 설계하였습니다. <br/>
해당 모델은 영화와 장르 간의 실제 연결 관계를 반영하여, 유사한 장르 간의 벡터가 더 가깝도록 임베딩되도록 학습됩니다. <br/>
실제로 Thriller와 Action은 본 모델에서 유사한 벡터를 가지며 장르 간 의미적 유사성이 잘 반영되어 있습니다.

<br/>

<img width="1185" height="528" alt="image" src="https://github.com/user-attachments/assets/70b6390d-cd3e-46f5-bde0-41b622ce1bdb" />

<br/> 
<br/> 



| Model   | Precision Test 결과 | 모델 크기 |
|------------|-----------------|---------|
| Google Word2Vec| 0.559 | 1.5 GB |
| Node2Vec| 0.718 | 2.7MB |

<br/>
정확도 측면에서 Node2Vec이 Word2Vec보다 약 28% 향상된 성능을 보였고, 모델 크기 또한 Word2Vec 대비 99% 이상 작습니다.

<br/> 
<br/> 

### 🪄스케줄러 사용

사용자 행동 로그 기반 추천 시스템에서는 두 가지 스케줄러를 사용합니다.

<img width="5504" height="2284" alt="image" src="https://github.com/user-attachments/assets/b6f67e60-2ce8-4fe5-924c-bb87b673ee2d" />

<br/>
<br/>

⭐️ 실패 로그 재처리 스케줄러(1분 주기)

사용자 행동이 발생했지만 가중치 반영에 실패한 경우, 해당 로그는 failed action log로 저장됩니다. 이를 1분마다 재처리하여 유실을 방지합니다.  

<br/>

📍 동작 흐름  
```
MongoDB에서 상태가 fail인 로그 조회 → 실패 로그의 가중치를 재계산 → 사용자 가중치 컬렉션(MongoDB)에 반영
```

이 구조는 시스템 일시적 오류나 장애로 인해 놓친 가중치 반영을 자동으로 복구하는 역할을 합니다.   

<br/>
<br/>

⭐️ 가중치 노후화 보정 스케줄러(1일 주기)  

사용자의 오래된 행동 로그는 시간이 지남에 따라 유효성이 낮아지므로, 이를 반영하여 유저 가중치를 재계산합니다.  

<br/>

📍 동작 흐름  
```
action log 조회 → 로그 기반으로 시간 가중 감쇠 함수 적용 → 감쇠된 값을 기반으로 벡터를 재계산 후 Redis에 저장
```

이 스케줄러는 장기간 사용하지 않은 행동 로그를 시간 감쇠 함수로 처리하여, 보다 실시간성 높은 추천 벡터를 유지하도록 돕습니다.  

<br/>
<br/>

### 🪄 행동 로그 관리 전략

<img width="718" height="384" alt="image" src="https://github.com/user-attachments/assets/17f40619-f8a1-4741-96be-c42b77ca62d8" />

<br/>
<br/>

#### 📍 MongoDB 행동 로그 처리 효율화를 위한 컬렉션 분리 설계  

MongoDB에 action_log 컬렉션과 managed_aciton_log 컬렉션이 존재합니다.  

초기에는 모든 사용자 행동 로그를 하나의 aciton_log 컬렉션에 저장하고, 실패 로그 재처리 시 상태(status : FAIL)를 조건으로 전체 행동 로그 데이터를 매번 필터링했습니다. 하지만, 1분 주기로 수행되는 재처리 스케줄러의 전체 로그를 대상으로 필터링하는 방식은 행동 로그가 많아질수록 실패 로그만 필터링 하는데 많은 비용이 든다는 문제가 있습니다.  


따라서, 실패한 로그만 별도로 managed_action_log 컬렉션에 저장하여, 재처리 시 해당 컬렉션만 조회하도록 구조를 분리하였습니다. 그 결과, 필터링 비용 없이 즉시 실패 로그만 조회가 가능해졌습니다.  


<br/>


#### 📍 행동 로그에 대한 상태값 관리  

사용자 행동 로그 유실을 방지 하기 위해, PROCESSING, SUCCESS, FAIL 3가지 상태값으로 관리합니다.  
```
PROCESSING : 행동 로그가 MongoDB에 저장은 되었지만, 아직 가중치 업데이트 및 유저 백터 계산이 완료되지 않은 상태입니다.

SUCCESS : 행동 로그의 가중치 업데이트 및 백터 계산이 성공적으로 완료된 상태입니다.

FAIL : 시스템 오류 또는 예외로 인해 처리에 실패한 상태로, 1분마다 실행되는 스케줄러에 의해 재시도됩니다.  
```

<br/>
<br/>

### 🍿 쇼츠 영상 추천 전략  

<img width="400" height="450" alt="image" src="https://github.com/user-attachments/assets/fc04778f-59de-41ad-874a-fdf07632b23c" />

<br/>   
<br/>  

#### 📍 무한 스크롤 처리 방식  

사용자의 빠른 소비 패턴을 고려해, 서버 부하를 줄이고 끊김 없는 경험을 제공하는 구조로 설계했습니다.  
최초 요청 시 콘텐츠 30개를 캐싱해두고, 사용자가 모두 소비하면 다음 30개를 불러와 다시 캐싱합니다. 

<br/>  

#### 📍 쇼츠 영상 추천 알고리즘 설계  

사용자 맞춤형 추천은 개인화에 효과적이지만, 너무 높은 일치율의 콘텐츠만 노출되면 사용자에게 추천되는 콘텐츠가 편향될 수 있다는 문제가 있습니다. 이를 완화하기 위해, 추천 콘텐츠 30개 중 20개는 사용자 벡터와의 유사도가 높은 콘텐츠를 기반으로 추천하고, 나머지 10개는 무작위로 선택된 콘텐츠를 제공합니다.  
이러한 방식은 개인화 추천의 정확도는 유지하면서도, 다양성 있는 콘텐츠를 노출해 사용자 이탈을 방지하고, 예상치 못한 흥미 유발 또한 유도할 수 있도록 설계했습니다.   

<br/>  
 
### 


### 🏗 시스템 아키텍처

<img width="4367" height="2397" alt="08:02_아키텍처" src="https://github.com/user-attachments/assets/c63937f9-a916-4612-b7c5-ea5619c95a85" />

##

### 🗃 ERD
<img width="3022" height="1708" alt="image" src="https://github.com/user-attachments/assets/a419a1a0-9220-42b7-945f-013a9125ae29" />


>[▶️ERD CLOUD 바로가기](https://www.erdcloud.com/d/GLGXxrdRRm9f6ZaKE)

<br/>



## 🛠 기술 스택

### 🚀 Server
- <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white"/> <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>
- <img src="https://img.shields.io/badge/MongoDB-47A248?style=flat&logo=mongodb&logoColor=white"/> <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
- <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=flat&logo=postgresql&logoColor=white"/> <img src="https://img.shields.io/badge/pgvecor-%23316192.svg?style=flat&logo=postgresql&logoColor=white"/>
- <img src="https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white"/>

### ☁ Infra
- <img src="https://img.shields.io/badge/Docker-0db7ed?style=flat&logo=docker&logoColor=white"/>
- <img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat&logo=amazonaws&logoColor=white"/> <img src="https://img.shields.io/badge/AWS RDS-527FFF?style=flat&logo=amazonaws&logoColor=white"/> ![AWS ElastiCache](https://img.shields.io/badge/AWS-ElastiCache-ff9900?logo=amazon-aws&logoColor=white)
- ![AWS S3](https://img.shields.io/badge/AWS-S3-569A31?logo=amazon-aws&logoColor=white) ![AWS MediaConvert](https://img.shields.io/badge/AWS-MediaConvert-orange?logo=amazon-aws&logoColor=white)
- [![AWS Lambda](https://custom-icon-badges.demolab.com/badge/AWS%20Lambda-%23FF9900.svg?logo=aws-lambda&logoColor=white)](#)

### 📈 Monitoring & Logging
- <img src ="https://img.shields.io/badge/-Grafana-5f5f5f?style=flat&logo=grafana&labelColor=ffffff"/> <img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=Prometheus&logoColor=white"/>
- <img src ="https://img.shields.io/badge/-Loki-5f5f5f?style=flat&logo=grafana&labelColor=ffffff"/> <img src ="https://img.shields.io/badge/-Promtail-5f5f5f?style=flat&logo=grafana&labelColor=ffffff"/>

### 📨Messaging
- <img src ="https://img.shields.io/badge/-rabbitmq-%23FF6600?style=flat&logo=rabbitmq&logoColor=white"/>

## 기술 선택 이유

### 💡 우리의 개발 철학
- 모듈 간 책임과 경계를 명확히 하기
- 근거가 있는 선택의 결정을 하기
- 완벽보다 동작하는 코드를 우선하기

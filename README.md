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

추천 서버는 벡터 값을 Redis에 먼저 저장하고, 이후 필요한 경우에만 PostgreSQL과 동기화하기 때문에, 사용자가 행동을 해도 DB I/O가 발생하지 않았습니다.  

<br/>  

초기 버전에 비해 최종 버전의 응답 시간이 **1.16초** 단축되었습니다. 

<br/>

**📍초기 버전**  

<img width="1388" height="126" alt="image" src="https://github.com/user-attachments/assets/c7e0cd67-a9d1-4986-9098-8b7565279c4b" />

<br/>
<br/> 

**📍최종 버전**  

<img width="1361" height="115" alt="image" src="https://github.com/user-attachments/assets/feea1371-7f1d-4158-a5eb-c9a12795e6a0" />

<br/>
<br/>

### MongoDB의 CPU 사용률
CPU 사용률은 40%로 안정적입니다.  

<img width="2422" height="947" alt="mongoDBcpu" src="https://github.com/user-attachments/assets/a79f9102-f8e9-462c-9605-4f0b096974e8" />

### PostgreDB의 CPU 사용률
CPU 사용률은 10% 미만으로 안정적입니다.

<img width="2414" height="946" alt="postgreDB_cpu" src="https://github.com/user-attachments/assets/fbf4637f-9c6a-407a-a41b-9080aa309efa" />

<br/>  
<br/>  


##  📊 플로우 차트

<img width="1400" height="1000" alt="image" src="https://github.com/user-attachments/assets/548847a9-ef59-4a32-a338-969cfbdea75b" />


### 🏗 시스템 아키텍처
<img width="4367" height="2397" alt="08:02_아키텍처" src="https://github.com/user-attachments/assets/c63937f9-a916-4612-b7c5-ea5619c95a85" />



##

### 🗃 ERD
<img width="3022" height="1708" alt="image" src="https://github.com/user-attachments/assets/a419a1a0-9220-42b7-945f-013a9125ae29" />


>[▶️ERD CLOUD 바로가기](https://www.erdcloud.com/d/GLGXxrdRRm9f6ZaKE)

<br/>


## 

### 🎬 장르 임베딩 개선

기존 임베딩 모델은 일반 문맥 기반으로 학습되었기 때문에, 영화 도메인에 적합하지 않은 임베딩 결과를 보였습니다. <br/>
예를 들어, 실제로 유사한 장르인 Thriller와 Action조차 서로 전혀 다른 벡터로 표현되는 문제가 있었습니다.

이러한 한계를 해결하기 위해 영화-장르 간 관계를 그래프로 구성하고 이를 기반으로 학습한 도메인 특화 임베딩 모델을 새롭게 설계하였습니다. <br/>
해당 모델은 영화와 장르 간의 실제 연결 관계를 반영하여, 유사한 장르 간의 벡터가 더 가깝도록 임베딩되도록 학습됩니다. <br/>
실제로 Thriller와 Action은 본 모델에서 유사한 벡터를 가지며 장르 간 의미적 유사성이 잘 반영되어 있습니다.

 <br/>

<img width="978" height="573" alt="추천 알고리즘 (4)" src="https://github.com/user-attachments/assets/d7fb5355-b5ba-4f8d-a921-e37818625caa" />


##

### 🌤️ 플로우 차트 

사용자 행동 로그 발생 시 최소한의 지연(latency)으로 실시간 추천을 제공하기 위해 하나의 추천 워크플로우를 여러 개의 트랜잭션으로 분리하였으며, 각 단계에 대한 보상 트랜잭션을 설계 및 구현하였습니다.

- 각 트랜잭션이 실패할 경우 실패 로그는 MongoDB에 기록됩니다.

- Log Reprocessing Scheduler가 1분 주기로 실패 로그를 조회하여 재처리를 시도합니다.

- Weight Resizing Scheduler는 매일 실행되며 과거 가중치를 재조정함과 동시에 모든 실패 로그를 다시 시도합니다.

- 재처리 스케줄러조차 실패할 경우 해당 로그는 파일로 저장되며, 이는 Promtail + Loki + Grafana를 통해 슬랙 알림으로 전송됩니다.

<br/>

<img width="5136" height="1876" alt="image" src="https://github.com/user-attachments/assets/548847a9-ef59-4a32-a338-969cfbdea75b" />

##

### 🪄스케줄러 

<img width="5504" height="2284" alt="image" src="https://github.com/user-attachments/assets/b6f67e60-2ce8-4fe5-924c-bb87b673ee2d" />




## 🛠 기술 스택

### 🚀 Server
- <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white"/> <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>
- <img src="https://shields.io/badge/MySQL-lightgrey?logo=mysql&style=plastic&logoColor=white&labelColor=blue"/> <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
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

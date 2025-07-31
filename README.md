## 📌 프로젝트 개요

### 🚩소개

OTT 서비스의 대표적인 문제인 개인 취향에 맞는 콘텐츠를 찾기 어려운 경험을 해결하고자,  
**사용자 행동 데이터 기반의 개인화 추천 시스템**을 구축합니다.

명시적 입력 없이도 행동 로그만으로 사용자 취향을 파악하고
이를 기반으로 한 정교한 콘텐츠 추천 기능을 갖춘 백엔드 아키텍처를 설계·구현합니다.

<br/>

### 🎯 개발 목표

사용자 행동을 실시간으로 반영한 추천 시스템 구축


<br/>

## ⚙️ 기술적 특징

### 🏗 시스템 아키텍처
<img width="4367" height="2397" alt="아키텍처최신화" src="https://github.com/user-attachments/assets/e7be66ff-3f4a-4ecf-8a50-577e3eb8c90d" />








##

### 🗃 ERD
<img width="3022" height="1708" alt="image" src="https://github.com/user-attachments/assets/a419a1a0-9220-42b7-945f-013a9125ae29" />


>[▶️ERD CLOUD 바로가기](https://www.erdcloud.com/d/GLGXxrdRRm9f6ZaKE)

<br/>


## 

### 🔄 모델 변경

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

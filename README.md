## 📌 프로젝트 개요

### 🚩소개

OTT 서비스의 대표적인 문제인 개인 취향에 맞는 콘텐츠를 찾기 어려운 경험을 해결하고자,  
**사용자 행동 데이터 기반의 개인화 추천 시스템**을 구축합니다.

명시적 입력 없이도 행동 로그만으로 사용자 취향을 파악하고,  
이를 기반으로 한 정교한 콘텐츠 추천 기능을 갖춘 백엔드 아키텍처를 설계·구현합니다.

<br/>

### 🎯 개발 목표

사용자 행동을 실시간으로 반영한 추천 시스템 구축
  - Redis Stream(메시징 처리)으로 실시간 가중치 계산을 비동기 처리합니다.
  - ELK를 이용하여 로그를 관리합니다.
  - 쓰기 DB와 읽기 DB를 분리하여 DB 부하를 줄입니다.


<br/>

## ⚙️ 기술적 특징

### 🏗 시스템 아키텍처
<img width="2983" height="2392" alt="mongoDB추가된아키텍처" src="https://github.com/user-attachments/assets/4805f790-6f52-4b5b-a03f-96986dd38e76" />



##

### 🗃 ERD
<img width="3022" height="1708" alt="image" src="https://github.com/user-attachments/assets/a419a1a0-9220-42b7-945f-013a9125ae29" />


>[▶️ERD CLOUD 바로가기](https://www.erdcloud.com/d/GLGXxrdRRm9f6ZaKE)

<br/>


## 

### 🔄 추천 플로우차트

<img width="1462" height="1122" alt="image" src="https://github.com/user-attachments/assets/3d835c8c-bfdc-4f63-b0bd-9e4bfeba9695" />

<img width="1000" height="960" alt="image" src="https://github.com/user-attachments/assets/dad1cc09-e8af-4c7c-937b-1123aacad454" />



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


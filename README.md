# 🎬 LEAD:ME

## 📌 프로젝트 개요

### 🚩소개

OTT 서비스의 대표적인 문제인 **개인 취향에 맞는 콘텐츠를 찾기 어려운 경험**을 해결하고자,  
**사용자 행동 데이터 기반의 개인화 추천 시스템**을 구축합니다.

명시적 입력 없이도 **행동 로그만으로 사용자 취향을 파악**하고,  
이를 기반으로 한 **정교한 콘텐츠 추천 기능**을 갖춘 백엔드 아키텍처를 설계·구현합니다.

<br/>

### 🎯 개발 동기 및 목표

- **사용자 행동 로그 수집 및 분석 인프라 구축**
  - Filebeat와 Logstash를 활용해 웹/서버에서 발생하는 행동 로그 수집
  - Elasticsearch에 **ECS(Eastic Common Schema)** 기반 인덱스 색인
  - Kibana 대시보드를 통해 **이벤트 단위 사용자 흐름 실시간 분석**
  - 추천 알고리즘에서 활용 가능한 **정형 사용자 데이터 확보**

- **성능과 확장성 중심의 백엔드 아키텍처 설계**
  - **동시 접속자 1000명 이상**을 견디는 안정적인 서버 구조 설계
  - 트래픽 급증 시에도 병목 없이 동작하도록 **스케일링 구조 반영**
  - NGrinder를 활용한 부하 테스트로 **TPS, 응답속도, 에러율 기준 수립 및 검증**

<br/>

## ⚙️ 기술적 특징

### 🏗 시스템 아키텍처
<img width="1141" height="709" alt="스크린샷 2025-07-16 오전 10 26 17" src="https://github.com/user-attachments/assets/96d68599-dd22-4f9e-bca8-5bd13b10410a" />


##

### 🗃 ERD

<img width="1542" height="858" alt="image" src="https://github.com/user-attachments/assets/b876599c-4d52-4582-84d0-b32186370787" />


>[▶️ERD CLOUD 바로가기](https://www.erdcloud.com/d/GLGXxrdRRm9f6ZaKE)

<br/>


## 

### 🔄 추천 플로우차트

<img width="1576" height="957" alt="image" src="https://github.com/user-attachments/assets/4ea067e7-e9cd-43a6-bab7-b7d07f2b10ce" />



## 🛠 기술 스택

### 🚀 Server
- <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white"/>, <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>
- <img src="https://shields.io/badge/MySQL-lightgrey?logo=mysql&style=plastic&logoColor=white&labelColor=blue"/>, <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
- <img src="https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white"/>

### ☁ Infra
- <img src="https://img.shields.io/badge/Docker-0db7ed?style=flat&logo=docker&logoColor=white"/>
- <img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat&logo=amazonaws&logoColor=white"/>, <img src="https://img.shields.io/badge/AWS RDS-527FFF?style=flat&logo=amazonaws&logoColor=white"/>,![AWS ElastiCache](https://img.shields.io/badge/AWS-ElastiCache-ff9900?logo=amazon-aws&logoColor=white)
- ![AWS S3](https://img.shields.io/badge/AWS-S3-569A31?logo=amazon-aws&logoColor=white), ![AWS MediaConvert](https://img.shields.io/badge/AWS-MediaConvert-orange?logo=amazon-aws&logoColor=white)
- [![AWS Lambda](https://custom-icon-badges.demolab.com/badge/AWS%20Lambda-%23FF9900.svg?logo=aws-lambda&logoColor=white)](#)

### 📈 Monitoring & Logging
- <img src ="https://img.shields.io/badge/-Grafana-5f5f5f?style=flat&logo=grafana&labelColor=ffffff"/>, <img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=Prometheus&logoColor=white"/>


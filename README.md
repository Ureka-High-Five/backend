# 🎬 Personalized OTT Recommendation Backend System

## 📌 프로젝트 개요

### 소개

OTT 서비스의 대표적인 문제인 **"개인 취향에 맞는 콘텐츠를 찾기 어려운 경험"**을 해결하고자,  
**사용자 행동 데이터 기반의 개인화 추천 시스템**을 구축합니다.

명시적 입력 없이도 **행동 로그만으로 사용자 취향을 파악**하고,  
이를 기반으로 한 **정교한 콘텐츠 추천 기능**을 갖춘 백엔드 아키텍처를 설계·구현합니다.


### 🎯 개발 동기 및 목표 (Backend 중심)

- **사용자 행동 로그 수집 및 분석 인프라 구축**
  - Filebeat와 Logstash를 활용해 웹/서버에서 발생하는 행동 로그 수집
  - Elasticsearch에 **ECS(Eastic Common Schema)** 기반 인덱스 색인
  - Kibana 대시보드를 통해 **이벤트 단위 사용자 흐름 실시간 분석**
  - 추천 알고리즘에서 활용 가능한 **정형 사용자 데이터 확보**

- **성능과 확장성 중심의 백엔드 아키텍처 설계**
  - **동시 접속자 1000명 이상**을 견디는 안정적인 서버 구조 설계
  - 트래픽 급증 시에도 병목 없이 동작하도록 **스케일링 구조 반영**
  - NGrinder를 활용한 부하 테스트로 **TPS, 응답속도, 에러율 기준 수립 및 검증**


## ⚙️ 기술적 특징

### 🏗 시스템 아키텍처
> (여기에 시스템 구성도 이미지 첨부: 예시 – Nginx ↔ Spring Boot ↔ Redis ↔ MySQL, Filebeat → Logstash → Elasticsearch → Kibana)

### 🗃 ERD
> (여기에 ERD 다이어그램 첨부 – 예: `User`, `Content`, `InteractionLog` 테이블 중심)

### 🔄 추천 플로우차트
> (사용자 → 행동 로그 수집 → 벡터 기반 분석 → 추천 결과 반환 흐름을 다이어그램으로 표현)


## 🛠 기술 스택

### 🚀 Server
- Java 17, **Spring Boot**
- **MySQL**, **Redis**
- **FastAPI** (추천 알고리즘 연동)

### ☁ Infra
- Docker / Docker Compose
- AWS EC2, RDS, ElastiCache
- AWS S3, MediaConvert
- AWS Lambda (비동기 처리/배치 작업)

### 📈 Monitoring & Logging
- **Grafana + Prometheus** (메트릭 수집 및 대시보드 시각화)
- **ELK Stack (Filebeat, Logstash, Elasticsearch, Kibana)**

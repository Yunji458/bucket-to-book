# Bucket to Book 🪣📖

버킷리스트를 기록하고, 자동으로 정리해 나만의 포토북으로 주문까지 연결해주는 웹 서비스입니다.

<p align="center">
  <img width="1430" height="2006" alt="image" src="https://github.com/user-attachments/assets/72d4c699-38bc-41c7-9386-4210d11be885" />
</p>

---

## 1. 서비스 소개

### 한 줄 설명
사용자가 기록한 버킷리스트를 상태·카테고리·날짜 기준으로 자동 정리하고, 선택한 항목을 기반으로 포토북을 생성하여 실물 주문까지 연결하는 서비스입니다.

### 타겟 고객
- 버킷리스트를 단순 메모가 아닌 **인생 기록으로 남기고 싶은 사용자**
- 여행·취미·성장 등 다양한 경험을 **카테고리별로 정리**하고 싶은 사용자
- 가족·친구와 함께한 기억을 **실물 포토북**으로 소장하고 싶은 사용자

### 주요 기능

| 기능 | 설명 |
|------|------|
| 버킷리스트 관리 | 항목 추가·수정·삭제, 이미지 업로드, 상태(완료/진행중/예정) 설정, 목표일 / 완료일 설정 |
| 자동 정리 | 완료(완료일 최근순) / 진행중 / 예정(목표일 오름차순)으로 자동 분류 및 카테고리 그룹화 |
| 항목 선택 | 최소 24개 이상 선택 시 책 만들기 진행 가능, 카테고리별 자동 분리 구성 |
| 책 만들기 | 4단계(생성→표지→내지→최종화) 단계별 포토북 제작 |
| 포토북 미리보기 | 섹션 표지·카테고리 표지·내지를 책처럼 펼쳐보기 |
| 주문 | 견적 확인 후 포토북 주문 생성 |

---

## 2. 기술 스택

### Frontend
- Vue 3
- Vue Router
- Axios
- Vite

### Backend
- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA

### Database
- MariaDB 또는 MySQL

### External API
- Sweetbook Book Print API
  - Books API
  - Orders API
  - Templates API
  - BookSpecs API
  - Credits API

---

## 3. 실행 방법

### 3-1. 사전 준비

- Java 17
- Node.js 18+
- MariaDB 또는 MySQL

### 3-2. DB 생성

MariaDB 또는 MySQL에서 아래 DB를 생성합니다.

```sql
CREATE DATABASE bucket_to_book;
```

> MariaDB 드라이버가 MySQL과 호환되므로 `driver-class-name`은 그대로 사용할 수 있습니다.

### 3-3. 환경변수 설정

루트에 있는 `.env.example`을 복사해 `.env`를 만들고 실제 값을 입력합니다.

```bash
cp .env.example .env
```

`.env` 파일 예시:

```
DB_URL=jdbc:mariadb://localhost:3306/bucket_to_book
DB_USERNAME=root
DB_PASSWORD=your_password

SWEETBOOK_BASE_URL=https://api-sandbox.sweetbook.com/v1
SWEETBOOK_API_KEY=your_sandbox_api_key
```

> MySQL을 사용하는 경우 `DB_URL`을 `jdbc:mysql://localhost:3306/bucket_to_book`으로 변경하세요.

IntelliJ를 사용하는 경우 `Run > Edit Configurations > Environment variables`에 위 값을 직접 입력해도 됩니다.

### 3-4. backend 설정 확인

`backend/src/main/resources/application.yaml`의 내용은 다음과 같습니다. 별도 수정 없이 사용합니다.

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

sweetbook:
  api:
    base-url: ${SWEETBOOK_BASE_URL}
    key: ${SWEETBOOK_API_KEY}
```

### 3-5. 더미 데이터

본 프로젝트는 실행 직후 서비스를 바로 확인할 수 있도록 더미 데이터를 포함하고 있습니다.

- `DataInitializer` 클래스를 통해 애플리케이션 실행 시 DB가 비어있으면 샘플 데이터(버킷리스트 50개)가 자동 생성됩니다.
- 데이터가 이미 존재하는 경우 중복 삽입을 방지하도록 처리되어 있습니다.

> ⚠️ 더미 데이터 50개는 아래 파일명을 참조합니다. 파일명이 다르면 해당 항목의 이미지가 표시되지 않습니다.  
> `default.jpg`는 이미지가 없을 때 기본값으로 사용되므로 반드시 있어야 합니다.

```
dummy-data/images/
├── default.jpg       ← 필수
├── japan1.jpg        busan1.jpg        australia1.jpg
├── jeju1.jpg         friends1.jpg      familytravel1.jpg
├── guitar1.jpg       guitar2.jpg       run1.jpg
├── night1.jpg        exercise1.jpg     photo1.jpg
├── family1.jpg       cook1.jpg         cafe1.jpg
├── blog1.jpg         book1.jpg         study1.jpg
├── exhibition1.jpg   food1.jpg         movie1.jpg
```

### 3-6. 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

### 3-7. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173` 접속

---

## 4. 사용한 API 목록

### 자체 백엔드 API (`/api`)

| 메서드 | 엔드포인트 | 용도 |
|--------|-----------|------|
| `GET` | `/api/buckets` | 전체 버킷리스트 조회 |
| `POST` | `/api/buckets` | 버킷리스트 항목 생성 (multipart) |
| `PUT` | `/api/buckets/{id}` | 버킷리스트 항목 수정 (multipart) |
| `DELETE` | `/api/buckets/{id}` | 버킷리스트 항목 삭제 |
| `GET` | `/api/buckets/organized` | 상태·카테고리별 자동 정리 조회 |
| `POST` | `/api/books/create` | 포토북 생성 (bookUid 발급) |
| `POST` | `/api/books/{bookUid}/cover` | 포토북 표지 이미지 추가 |
| `POST` | `/api/books/{bookUid}/contents-auto` | 선택 항목 내지 자동 추가 |
| `POST` | `/api/books/{bookUid}/finalize` | 포토북 최종화 (주문 가능 상태) |
| `POST` | `/api/orders/estimate` | 주문 견적 조회 |
| `POST` | `/api/orders/create` | 포토북 주문 생성 |
| `GET` | `/images/{filename}` | 이미지 파일 서빙 |

### Sweetbook 외부 API

백엔드가 프록시로 호출하는 포토북 인쇄 API입니다.

| 메서드 | 엔드포인트 | 용도 |
|--------|-----------|------|
| `GET` | `/credits` | API 크레딧 잔액 조회 |
| `GET` | `/book-specs` | 주문 가능한 책 규격 목록 조회 |
| `GET` | `/templates?bookSpecUid=...` | 책 규격별 템플릿 목록 조회 |
| `POST` | `/books` | 포토북 생성 |
| `POST` | `/books/{bookUid}/cover` | 표지 이미지 업로드 |
| `POST` | `/books/{bookUid}/contents` | 내지 콘텐츠 추가 |
| `POST` | `/books/{bookUid}/finalization` | 포토북 최종화 |
| `POST` | `/orders/estimate` | 주문 견적 계산 |
| `POST` | `/orders` | 주문 생성 |

---

## 5. AI 도구 사용 내역

| AI 도구 | 활용 내용 |
|---------|----------|
| ChatGPT | 서비스 전체 구조 설계 과정에서 UI 흐름 개선 아이디어 도출, README 문서 구조화 및 문장 다듬기, 실제 서비스처럼 보이기 위한 더미 데이터 시나리오 설계 |
| Claude Code | Vue 기반 프론트엔드 개발 중 컴포넌트 구조 리팩토링, 중복 코드 제거 및 상태 흐름 정리, 특히 책 페이지 넘김(Flip UI) 애니메이션 개선 및 구조 안정화 |
| Gemini | 포토북 및 버킷리스트 화면에 사용되는 더미 이미지 생성, 실제 서비스와 유사한 UX를 위한 콘텐츠 시각화 |

---

## 6. 설계 의도

> **핵심 한 줄 요약**  
> "버킷리스트를 기록에서 끝내지 않고, 인생의 결과물로 만드는 서비스"

### 6-1. 왜 이 서비스를 선택했는가

버킷리스트 서비스는 이미 많이 존재하지만, 대부분 단순 기록이나 체크리스트 형태에 머물러 사용자의 지속적인 참여를 이끌어내지 못하는 한계가 있습니다. 사용자는 목표를 달성해도 이를 다시 돌아보거나 기록을 재소비하는 경험이 부족합니다.

이 문제를 해결하기 위해 단순 기록이 아닌 **"경험을 결과물로 남기는 구조"** 를 설계했습니다. 완료한 버킷리스트를 자동으로 정리해 포토북으로 제작함으로써, 사용자는 "기록했다"에서 끝나는 것이 아니라 **"내 인생의 결과물을 소장한다"** 는 경험을 얻게 됩니다.

### 6-2. 문제 정의 및 해결 방식

| 기존 문제 | 해결 방식 |
|----------|----------|
| 기록만 하고 다시 보지 않음 | 완료된 버킷리스트를 카테고리별 자동 분류 |
| 목표 달성 이후 동기 유지 어려움 | 책 미리보기 UI로 성취감 시각화 |
| 데이터가 쌓여도 의미 있는 결과물로 연결되지 않음 | 사진 + 텍스트 기반 스토리화 → 실제 인쇄로 연결 |

👉 **"데이터 → 스토리 → 실물"** 로 이어지는 경험 흐름을 설계

### 6-3. 핵심 사용자 경험 (UX 설계 의도)

사용 흐름을 단순화하면서도 성취감을 극대화하는 구조로 설계했습니다.

```
버킷리스트 작성 → 목표 완료 체크 → 사진 및 기록 추가
→ 자동 정리 및 책 형태 미리보기 → 포토북 주문
```

특히 **책 페이지 넘김 미리보기 UI** 와 **단계별 진행 스텝 트래커** 를 통해 단순 기능이 아니라 감정적인 만족까지 설계했습니다.

### 6-4. 비즈니스 가능성

**1) 반복 구매 구조 (LTV 기반)**
- 버킷리스트는 지속적으로 생성되므로 일정 기간마다 책으로 묶는 구조
- 자연스럽게 연 1~3회 재구매 발생
- "한 번 쓰고 끝"이 아닌 라이프사이클 서비스

**2) 선물 시장 확장**
- 졸업 / 생일 / 커플 기념일 / 여행 기록 등 감성 소비 시장 타겟
- "나의 인생 책" → 단순 기능 서비스가 아닌 감정 기반 상품

**3) 구독 모델 (Recurring Revenue)**
- 연간 구독 시 포토북 제작권 + 할인 혜택 + 프리미엄 템플릿 제공
- SaaS + 커머스 결합 모델

**4) SNS 바이럴 구조**
- 완성된 버킷리스트를 이미지 카드 형태로 자동 생성 → 인스타 공유
- "기록 → 공유 → 유입 → 구매" 선순환 구조

### 6-5. 더 시간이 있었다면 추가했을 기능

**1) 주문 및 결제 확장**
- **주문 내역 조회**: 사용자가 생성한 포토북 주문 목록 조회, 주문 상태(제작중 / 배송중 / 완료) 확인, 주문 상세 보기(포토북 미리보기 포함)
  - 단순 주문 → 이력 관리 서비스로 확장
- **여러 권 주문**: 수량 선택 옵션 제공, 할인 정책 적용(예: 3권 이상 할인), 선물용 다중 배송 확장 가능
  - 커머스 기능 강화 → 객단가 상승 구조

**2) 사용자 계정 및 데이터 확장**
- **소셜 로그인 및 회원 기능**: Google / Kakao 로그인 연동, 사용자별 버킷리스트 저장, 클라우드 기반 데이터 관리
  - 단순 데모 → 실제 서비스 전환 핵심 기능
- **공유 버킷리스트**: 친구·연인·가족과 공동 리스트 생성, 각자 진행 상태 반영, 공동 포토북 제작 가능
  - 개인 서비스 → 소셜 서비스로 확장

**3) AI 기반 기능 고도화**
- **AI 설명 생성**: 버킷리스트 제목 입력 시 자동으로 스토리형 설명 생성, 감성 문장 / 여행 후기 형태 생성 가능
  - 데이터 → 콘텐츠로 확장 (사용자 입력 부담 감소)
- **자동 스토리북 생성**: 여러 항목을 하나의 스토리로 연결, 여행 흐름 자동 구성
  - 단순 리스트 → 내러티브 콘텐츠

**4) UX 및 시각화 기능**
- **템플릿 선택 UI**: Sweetbook 템플릿 목록 조회, 썸네일 기반 선택 UI, 스타일별 미리보기 제공
  - 사용자 커스터마이징 경험 강화

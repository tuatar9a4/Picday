# 📸 Picday (픽데이)

> **당신의 하루를 한 장의 사진과 짧은 글로 간직하는 멀티미디어 다이어리**  
> "복잡한 글 대신, 직관적인 커스텀 캘린더를 통해 나의 지난 일상을 1:1 또는 9:16 비율로 생생하게 되돌아보세요."

[![PlayStore Badge](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.devd.picday)

## 📖 프로젝트 개요
Picday는 매일 한 장의 사진(16:9 비율 크롭)과 짧은 문장으로 하루를 기록하는 다이어리 애플리케이션입니다. 사용자 기기의 용량 부족 문제를 해결하기 위해 클라우드 스토리지를 도입하였으며, **단순한 뷰어(Viewer)를 넘어 스와이프 제스처에 따라 캘린더 셀이 동적으로 확장/축소되는 고도화된 인터랙션**을 제공합니다.

- **개발 기간:** 2025.12 ~ 2026.04
- **개발 인원:** 1인 개발 (기획, 클라우드 아키텍처 설계, 클라이언트 개발 전담)

<br>

## 🛠 Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM, Multi-Module, Clean Architecture
- **UI:** Jetpack Compose (100%)
- **Asynchronous & Reactive:** Coroutines, Flow
- **DI (Dependency Injection):** Dagger-Hilt
- **Local Storage:** RoomDB
- **Cloud & Infra:** Oracle Cloud Infrastructure (OCI) Object Storage, Serverless (OCI Functions)
- **Push Notification:** FCM (Firebase Cloud Messaging)

<br>

## 🏗 Architecture & Multi-Module Strategy

프로젝트의 확장성과 각 도메인 간의 강결합을 방지하기 위해 **Multi-Module 아키텍처**를 기반으로 기능(Feature)과 코어(Core)를 철저히 분리했습니다.

```text
📦 프로젝트 구조
 ┣ 📂 app (Hilt Application)
 ┣ 📂 core
 ┃  ┣ 📂 commonsystem (공통 UI, Theme, Base Component)
 ┃  ┣ 📂 data (Repository 구현체, API/DB/Cloud 연동)
 ┃  ┣ 📂 database (RoomDB, DAO)
 ┃  ┣ 📂 datastore (DataStore)
 ┃  ┣ 📂 firebase (FCM)
 ┃  ┣ 📂 model (Data Class)
 ┃  ┣ 📂 network (Api 통신 관련)
 ┃  ┗ 📂 permission (권한 설정 기능)
 ┗ 📂 feature
    ┣ 📂 bookcase (일기장 리스트)
    ┣ 📂 calendar (달력 페이지)
    ┣ 📂 diary (일기 리스트)
    ┣ 📂 editor (일기 작성)
    ┣ 📂 home (메인 피드)
    ┣ 📂 intro (인트로, 권한 확인)
    ┣ 📂 setting (설정 페이지)
    ┗ 📂 calendar (커스텀 캘린더 및 일기 조회)
```

## 🤔 Decision Log & Troubleshooting

## 1. 보안과 용량을 모두 잡은 클라우드 아키텍처 (OCI Serverless & PAR)

### 이슈
다이어리 앱 특성상 고화질 이미지가 지속적으로 누적되면 사용자의 기기 로컬 용량을 크게 점유하게 됩니다. 이를 클라우드 스토리지를 통해 해결하려 했으나, 클라이언트(앱) 앱 내부에 Cloud SDK 인증 키(Secret Key)를 하드코딩하면 보안 위협이 발생합니다.
### 해결
- OCI Functions (Serverless) 를 중계기로 활용하는 아키텍처를 설계했습니다.
- 앱에서 이미지 업로드를 요청할 때마다 서버리스 함수가 작동하여, 해당 업로드 건에만 유효한 임시 PAR(Pre-Authenticated Request) URL을 동적으로 발급합니다.
- 이를 통해 앱 내부에 인증 키를 노출하지 않고도 안전하게 Object Storage에 대용량 이미지를 적재하는 무결성 높은 업로드 환경을 구축했습니다.

## 2. Compose 렌더링 최적화: GPU 가속과 리컴포지션 방어
### 이슈
Picday의 핵심 UI인 '커스텀 캘린더'는 사용자가 스와이프하면 1:1 비율의 썸네일이 9:16 비율의 상세 이미지로 전환되는 동적 레이아웃입니다. 초기 구현 시 스크롤 위치에 따라 UI 상태가 실시간으로 변경되면서 과도한 리컴포지션(Recomposition)이 발생해 프레임 드랍이 일어났습니다. 
### 해결
- AnchoredDraggableState 도입: 스와이프 제스처에 반응하는 가변 레이아웃 상태를 효율적으로 관리했습니다.
- GPU 가속 (graphicsLayer): Layout 영역을 직접 재계산(Modifier.size 등)하지 않고, graphicsLayer 내에서 scale과 rotation 등 그래픽 계층의 속성만 변경하여 UI 스레드의 부하를 줄이고 GPU 가속을 활용했습니다.
- derivedStateOf 활용: 스와이프 위치(offset)에 따른 이미지의 투명도(Alpha)와 텍스트 노출 여부를 derivedStateOf로 감싸, 불필요한 리컴포지션 전파를 차단하고 60fps의 부드러운 애니메이션 전환을 달성했습니다.




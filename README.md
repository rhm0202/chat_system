# chat_system

> TCP + UDP 혼합 채팅 시스템 | Java | 네트워크 프로그래밍 수업 과제

UDP 기반 등록 서버와 TCP 기반 P2P 채팅을 결합한 콘솔 채팅 시스템입니다.

---

## 프로젝트 개요

| 항목 | 내용 |
|---|---|
| 언어 | Java |
| 통신 방식 | UDP (사용자 등록/조회) + TCP (채팅) |
| 분류 | 네트워크 프로그래밍 수업 과제 |
| 기간 | 2025.07 |

---

## 동작 방식

```
[등록 서버 (RegisterServer)]
        ↑ UDP
[클라이언트 A] ──── TCP ────▶ [클라이언트 B]
```

1. 클라이언트가 UDP로 등록 서버에 접속해 사용자 ID와 채팅 포트 등록
2. 채팅 상대 조회 시 등록 서버에서 상대방 IP/Port 수신 (UDP)
3. 해당 정보로 상대방과 TCP 직접 연결 후 채팅 시작

---

## 주요 기능

- 사용자 등록 / 전체 조회 / 삭제 / 수정
- 상대방 ID 검색 후 1:1 채팅 시작
- 채팅 중 실시간 수신 (별도 스레드로 처리)
- `exit` 입력 시 채팅 종료 후 메뉴 복귀

---

## 클래스 구성

| 클래스 | 역할 |
|---|---|
| `UserClient` | 메인 클라이언트 (메뉴, 채팅 흐름 제어) |
| `RegisterServer` | UDP 기반 사용자 등록 서버 |
| `UdpHelper` | UDP 송수신 헬퍼 |
| `TcpHelper` | TCP 송수신 헬퍼 |
| `TcpReceiverHelper` | 채팅 수신 전용 백그라운드 스레드 |
| `TcpReceiverThread` | TCP 수신 처리 스레드 |
| `MyStreamSocket` | TCP 소켓 래퍼 |
| `MyClientDatagramSocket` | UDP 클라이언트 소켓 래퍼 |
| `MyServerDatagramSocket` | UDP 서버 소켓 래퍼 |
| `DatagramMessage` | UDP 메시지 포맷 |

---

## 기술 스택

- **Language**: Java
- **통신**: java.net (Socket, DatagramSocket, DatagramPacket)
- **동시성**: Thread, volatile

---

## GitHub

[https://github.com/rhm0202/chat_system](https://github.com/rhm0202/chat_system)


import java.io.*;

public class UserClient {

    public static volatile boolean isChatting = false;

    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);

        try {
            // 기본적인 포트 설정
            System.out.println("채팅 시스템에 어서오세요.");

            // 1. 등록 서버에 접속
            System.out.print("사용자 등록을 위해 등록 서버에 연결하여야 합니다.\n");
            System.out.print("서버의 주소를 입력하여 주세요 (엔터시 localhost): "); // 레지스트 서버의 주소 입력
            String hostName = br.readLine();
            if (hostName.length() == 0) {
                hostName = "localhost";  //기본이면 로컬 호스트
            }

            System.out.print("서버 포트 번호를 입력해주세요. (엔터시 7): ");
            String portNum = br.readLine();
            if (portNum.length() == 0) {
                portNum = "7";          // default port number
            }

            // 2. 내가 사용할 채팅 포트 번호 지정
            System.out.print("채팅 수신 포트 번호를 입력하세요: ");
            String portStr = br.readLine();
            int myChatPort = Integer.parseInt(portStr);

            // 3. 채팅 수신기를 백그라운드 스레드로 시작 (항상 내 채팅 포트 대기)
            TcpReceiverHelper receiver = new TcpReceiverHelper(myChatPort);
            receiver.setDaemon(true); //메인 종료시 종료
            receiver.start();

            //4. UDP 통신을 통한 등록 서버 사용자 정보 등록
            UdpHelper helper = new UdpHelper(hostName, portNum); //UDP 통신을 위한 헬퍼 클래스 생성
            boolean done = false;
            while (!done) {

                if (isChatting) {
                    try {
                        Thread.sleep(1000); // 1초 대기
                    } catch (InterruptedException e) {
                        // 예외는 그냥 무시해도 무방
                    }
                    continue;
                }

                while (br.ready()) {
                    br.readLine();
                }

                System.out.println("\n==== 메뉴 ====");
                System.out.println("1. 사용자 등록");
                System.out.println("2. 전체 사용자 조회");
                System.out.println("3. 사용자 정보 삭제");
                System.out.println("4. 사용자 정보 수정(재등록)");
                System.out.println("5. 사용자와 채팅 시작");
                System.out.println("6. 프로그램 종료");
                System.out.println("==============\n");

                System.out.print("번호를 선택하세요: ");
                String choice = br.readLine();

                String message = "";
                String response = "";

                switch (choice) {
                    case "1": // 등록
                        System.out.print("등록할 사용자 ID를 입력하세요: ");
                        String regId = br.readLine();
                        message = "Register," + regId + "," + myChatPort;
                        response = helper.sendCommand(message);
                        break;
                    case "2": // 전체 조회
                        message = "Query";
                        response = helper.sendCommand(message);
                        break;

                    case "3": // 삭제
                        System.out.print("등록할 사용자 ID를 입력하세요: ");
                        String delId = br.readLine();
                        message = "Delete," + delId;
                        response = helper.sendCommand(message);
                        break;
                    case "4": // 수정(재등록)
                        System.out.print("등록할 사용자 ID를 입력하세요: ");
                        String updId = br.readLine();
                        message = "Update," + updId + "," + myChatPort;
                        response = helper.sendCommand(message);
                        break;
                    case "5":
                        System.out.print("채팅할 상대방 ID를 입력하세요: ");
                        // 등록 서버에서 채팅할 상대의 정보를 받아옴(UDP)
                        String chatId = br.readLine();
                        String qMsg = "Query_User," + chatId;
                        String qResp = helper.sendCommand(qMsg);

                        if (qResp.startsWith("FOUND")) {
                            String[] info = qResp.split(",");
                            String peerIp = info[2];
                            int peerPort = Integer.parseInt(info[3]);
                            System.out.println("상대방 정보: IP=" + peerIp + ", Port=" + peerPort);

                            try { //TCP 통신을 통한 상대방과의 채팅 시작
                                UserClient.isChatting = true;
                                TcpHelper chatTcpHelper = new TcpHelper(peerIp, String.valueOf(peerPort));
                                isChatting = true;
                                System.out.println("상대방과 채팅을 시작합니다. (exit 입력시 종료)");

                                // 수신 스레드: 상대방 메시지 실시간 출력
                                Thread recvThread = new Thread(() -> {
                                    try {
                                        String line;
                                        while ((line = chatTcpHelper.receiveMessage()) != null) {
                                            if ("exit".equalsIgnoreCase(line)) {
                                                if (isChatting) {
                                                    System.out.println("상대방과의 연결이 종료되었습니다.1 엔터를 입력하면 메뉴로 돌아갑니다.");
                                                }
                                                chatTcpHelper.done();
                                                isChatting = false;
                                                break;
                                            }
                                            System.out.println("[상대] " + line);
                                        }
                                    } catch (IOException e) {
                                        if (isChatting) {
                                            System.out.println("상대방과의 연결이 종료되었습니다.2");
                                        }
                                    }
                                });
                                recvThread.setDaemon(true);
                                recvThread.start();

                                // 송신: 내 입력을 계속 보냄
                                String msg;

                                while ((msg = br.readLine()) != null) {
                                    if ("exit".equalsIgnoreCase(msg) || msg == "") {
                                        chatTcpHelper.sendMessage(msg);
                                        if (isChatting) {
                                            System.out.println("상대방과의 연결이 종료되었습니다.3");
                                        }
                                        chatTcpHelper.done();
                                        isChatting = false;
                                        break;
                                    }
                                    chatTcpHelper.sendMessage(msg);
                                }

                            } catch (IOException e) {
                                System.out.println("채팅 연결 오류: " + e.getMessage());
                            }

                        } else {
                            System.out.println("상대방을 찾을 수 없습니다: " + qResp);
                        }
                        break;
                    case "6": // 종료
                        done = true;
                        helper.close();
                        System.out.println("프로그램을 종료합니다.");
                        continue;

                    case "":
                        continue;

                    default:
                        System.out.println("잘못된 번호입니다.");
                        continue;
                }

                if (response != null && !response.trim().isEmpty()) {
                    System.out.println("서버 응답: " + response);
                }
            }
        } // end try  
        catch (IOException ex) {
            ex.printStackTrace();
        } // end catch
    } //end main
} // end class      


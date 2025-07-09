
import java.io.*;

class TcpReceiverThread implements Runnable {

    MyStreamSocket myDataSocket;

    TcpReceiverThread(MyStreamSocket myDataSocket) {
        this.myDataSocket = myDataSocket;
    }

    public void run() {
        boolean done = false;
        String message;

        try {
            UserClient.isChatting = true;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

            // 상대방 메시지 별도 스레드로 수신
            Thread recvThread = new Thread(() -> {
                try {
                    String recvMsg;
                    while ((recvMsg = myDataSocket.receiveMessage()) != null) {
                        System.out.println("[상대] " + recvMsg);
                        if (recvMsg.equalsIgnoreCase("exit")) {
                            myDataSocket.close();
                            break;
                        }
                    }
                    if(UserClient.isChatting) System.out.println("상대방이 채팅을 종료했습니다. 엔터를 입력하면 메뉴로 돌아갑니다.1");
                    UserClient.isChatting = false;
                } catch (IOException e) {
                    if(UserClient.isChatting) System.out.println("상대방과의 연결이 종료되었습니다. 엔터를 입력하면 메뉴로 돌아갑니다.2");
                    UserClient.isChatting = false;
                }
            });
            recvThread.setDaemon(true);
            recvThread.start();

            // 내 메시지 보내기 루프
            while (!done) {
                message = userInput.readLine();
                // 상대방이 나가서 플래그가 내려가면 안내하고 탈출
                if (!UserClient.isChatting) {
                    break;
                }
                if (message == null) {
                    break;
                }
                if (message.trim().equalsIgnoreCase("exit")) {
                    myDataSocket.sendMessage(message);
                    if(UserClient.isChatting) System.out.println("연결 종료");
                    UserClient.isChatting = false;
                    myDataSocket.close();
                    done = true;
                } else {
                    myDataSocket.sendMessage(message);
                }
            }
        } catch (Exception ex) {
            System.out.println("채팅 중 예외가 발생했습니다: " + ex);
        }
    } //end run
} //end class 

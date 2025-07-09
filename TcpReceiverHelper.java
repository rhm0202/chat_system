
import java.net.*;

public class TcpReceiverHelper extends Thread {

    private int serverPort;

    public TcpReceiverHelper(int serverPort) {
        this.serverPort = serverPort;
    }

    public void run() {
        try (ServerSocket myConnectionSocket = new ServerSocket(serverPort)) {
            while (true) {
                if (UserClient.isChatting) {
                    // 이미 채팅 중이면 1초 대기 (추가 연결 안받음)
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    continue;
                }
                System.out.println("\n상대방의 채팅 연결을 기다리는 중...");
                MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept());
                System.out.println("\n채팅이 연결되었습니다. 엔터를 한번 누른 후 채팅을 입력해주세요.");

                UserClient.isChatting = true;
                Thread theThread = new Thread(new TcpReceiverThread(myDataSocket));
                theThread.start();
                

                // 현재 채팅이 끝날 때까지 대기
                while (UserClient.isChatting) {
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } //end main
} // end class

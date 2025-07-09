// 등록 서버의 역할을 구현하는 코드입니다.

import java.io.IOException;
import java.util.*;

public class RegisterServer {

    public static void main(String[] args) {
        int serverPort = 7; // 기본 포트
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]); // 커스텀 포트
        }

        try {
            MyServerDatagramSocket mySocket = new MyServerDatagramSocket(serverPort); // 포트 열기
            Map<String, String> userMap = new HashMap<>(); // 유저 정보를 저장
            System.out.println("Server ready."); // 서버가 준비 완료됨을 표시

            while (true) {  // 받기 대기
                DatagramMessage request = mySocket.receiveMessageAndSender(); // 데이터를 받아옴
                System.out.println("Request received.");
                String message = request.getMessage();
                System.out.println("Received message: " + message);

                String[] parts = request.getMessage().trim().split(","); // 메세지 받은 걸 ',' 기준으로 나눔
                if (parts[0].equalsIgnoreCase("Register") && parts.length == 3) { // 들어온 요청이 유저 등록이면
                    String id = parts[1];
                    String ip = request.getAddress().getHostAddress();
                    String port =  parts[2];
                    String info = ip + "," + port; // 받은 정보 다시 합치기

                    // 이미 동일한 ip,port가 등록되어 있나?
                    boolean alreadyRegistered = false;
                    for (String existingInfo : userMap.values()) {
                        if (existingInfo.equals(info)) {
                            alreadyRegistered = true;
                            break;
                        }
                    }
                    if (alreadyRegistered) {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "ERROR,AlreadyRegisteredUser");
                        System.out.println("중복 등록 거부: " + info);
                    } else {
                        userMap.put(id, info); // 유저 정보 등록
                        System.out.println(id + " Registered: " + info); // 등록 완료 프린트
                        //등록 완료 메시지 응답
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "ID registration complete," + id);
                    }
                } else if (parts[0].equalsIgnoreCase("Query") && parts.length == 1) {// 조회 요청
                    if (userMap.isEmpty()) {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "No connected users.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        int i = 1;
                        for (String id : userMap.keySet()) {
                            sb.append(i).append(". ").append(id);
                            if (i != userMap.size()) {
                                sb.append("\n"); // 줄바꿈(엔터)로 구분
                            }
                            i++;
                        }
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "\nUSER LIST\n" + sb.toString());
                    }
                } else if (parts[0].equalsIgnoreCase("Query_User") && parts.length == 2) { // 연결할 유저 정보 요청
                    String id = parts[1];
                    String info = userMap.get(id);
                    if (info != null) {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "FOUND," + id + "," + info);
                    } else {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "ERROR,NotFound");
                    }
                } else if (parts[0].equalsIgnoreCase("Delete") && parts.length == 2) { //데이터 삭제 요청시
                    String id = parts[1];
                    if (userMap.containsKey(id)) {
                        userMap.remove(id); // 정보 삭제
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "Deleted," + id);
                    } else {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "ERROR,NotFound");
                    }
                } else if (parts[0].equalsIgnoreCase("Update") && parts.length == 3) { // 데이터 업데이트 요청시
                    String id = parts[1];
                    if (userMap.containsKey(id)) {
                        String ip = request.getAddress().getHostAddress();
                        String port = parts[2];
                        String info = ip + "," + port;
                        userMap.put(id, info); // 실제 보낸 주소로 덮어쓰기
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "UPDATED," + id);
                    } else {
                        mySocket.sendMessage(request.getAddress(), request.getPort(), "ERROR,NotFound");
                    }
                } else {
                    // (옵션) 잘못된 메시지 응답
                    mySocket.sendMessage(request.getAddress(), request.getPort(), "ERROR,UnknownCommand");
                }
            } //end while
        } // end try
        catch (IOException ex) {
            ex.printStackTrace();
        } // end catch
    } //end main
} // end class      

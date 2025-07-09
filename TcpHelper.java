
import java.io.*;
import java.net.*;

public class TcpHelper {

    static final String endMessage = "exit";
    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    public TcpHelper(String hostName,
            String portNum) throws SocketException,
            UnknownHostException, IOException {

        this.serverHost = InetAddress.getByName(hostName);
        this.serverPort = Integer.parseInt(portNum);
        this.mySocket = new MyStreamSocket(this.serverHost, this.serverPort);
        System.out.println("Connection request made");
    } // end constructor

    // 메시지 보내기 
    public void sendMessage(String message) throws IOException {
        mySocket.sendMessage(message);
    }

    // 메시지 받기
    public String receiveMessage() throws IOException {
        return mySocket.receiveMessage();
    }

    public void done() throws SocketException,
            IOException {
        mySocket.close();
    } // end done 
} //end class

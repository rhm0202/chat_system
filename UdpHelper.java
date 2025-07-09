
import java.io.*;
import java.net.*;

public class UdpHelper {

    private MyClientDatagramSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    UdpHelper(String hostName, String portNum)
            throws SocketException, UnknownHostException {
        this.serverHost = InetAddress.getByName(hostName);
        this.serverPort = Integer.parseInt(portNum);
        this.mySocket = new MyClientDatagramSocket();
    }

    public String sendCommand(String command)
            throws IOException {
        mySocket.sendMessage(serverHost, serverPort, command);
        return mySocket.receiveMessage();
    }

    public void close() {
        mySocket.close();
    }

} //end class

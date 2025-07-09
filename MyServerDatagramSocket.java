import java.io.*;
import java.net.*;

public class MyServerDatagramSocket extends DatagramSocket {
static final int MAX_LEN = 1024; 
   MyServerDatagramSocket(int portNo) throws SocketException{
     super(portNo);
   }
   public void sendMessage(InetAddress receiverHost,
                           int receiverPort,
                           String message)
   		          throws IOException {	
         byte[] sendBuffer = message.getBytes("UTF-8");                                
         DatagramPacket datagram = 
            new DatagramPacket(sendBuffer, sendBuffer.length, 
                                  receiverHost, receiverPort);
         this.send(datagram);
   } // end sendMessage

   public String receiveMessage( )
		throws IOException {		
         byte[ ] receiveBuffer = new byte[MAX_LEN];
         DatagramPacket datagram =
            new DatagramPacket(receiveBuffer, MAX_LEN);
         this.receive(datagram);
         String message = new String(receiveBuffer, 0, datagram.getLength(), "UTF-8");
         return message;
   } //end receiveMessage

   public DatagramMessage receiveMessageAndSender( )
		throws IOException {		
         byte[ ] receiveBuffer = new byte[MAX_LEN];
         DatagramPacket datagram =
            new DatagramPacket(receiveBuffer, MAX_LEN);
         this.receive(datagram);
         // create a DatagramMessage object, to contain message
         //   received and sender's address
         DatagramMessage returnVal = new DatagramMessage( );
         returnVal.putVal(new String(receiveBuffer, 0, datagram.getLength(), "UTF-8"),
                          datagram.getAddress( ),
                          datagram.getPort( ));
         return returnVal;
   } //end receiveMessage
} //end class

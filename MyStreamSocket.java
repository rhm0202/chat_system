
import java.io.*;
import java.net.*;

public class MyStreamSocket /*extends Socket*/ {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    MyStreamSocket(InetAddress acceptorHost,
            int acceptorPort) throws SocketException,
            IOException {
        socket = new Socket(acceptorHost, acceptorPort);
        setStreams();

    }

    MyStreamSocket(Socket socket) throws IOException {
        this.socket = socket;
        setStreams();
    }

    private void setStreams() throws IOException {
        InputStream inStream = socket.getInputStream();
        input
                = new BufferedReader(new InputStreamReader(inStream));
        OutputStream outStream = socket.getOutputStream();
        output
                = new PrintWriter(new OutputStreamWriter(outStream));
    }

    public void sendMessage(String message)
            throws IOException {
        output.println(message);
        output.flush();
    } // end sendMessage

    public String receiveMessage()
            throws IOException {
        String message = input.readLine();
        return message;
    } //end receiveMessage

    public void close() throws IOException {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ignore) {
        }
        if (output != null) {
            output.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

} //end class

package XFactHD.mineduino.common.utils.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread extends Thread {
    private Server server = null;
    private Socket socket = null;
    private int ID = -1;
    private PrintWriter streamOut = null;
    private BufferedReader streamIn = null;
    private boolean stop = false;

    private int user_id = -1;

    public ChatServerThread(Server server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        ID = socket.getPort();
    }

    public void send(String msg) {
        streamOut.println(msg);
    }

    public int getID() {
        return ID;
    }

    @Override
    public void run() {
        while (true) {
            try {
                server.handle(ID, streamIn.readLine());
            } catch (IOException ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop = true;
            }
            if (stop)
                break;
        }
    }

    public void open() throws IOException {
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamOut = new PrintWriter(socket.getOutputStream(), true);
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }

    public void end() {
        stop = true;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
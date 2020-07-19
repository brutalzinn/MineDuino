package XFactHD.mineduino.common.utils.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ChatServerThread extends Thread {
    private Server server = null;
    private Socket socket = null;
    private int ID = -1;
    private OutputStream streamOut = null;
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
        try {
            streamOut.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String read(){

String result = null;
        try {
            result= streamIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }



    public int getID() {
        return ID;
    }
    private long time = 0;
    @Override
    public void run() {
        while (true) {



                ThreadCommHandler.executeQueuedTasks();

                    SerialHandler.getSerialHandler().serialEvent();
                 //   sleep(Math.abs((System.currentTimeMillis() - time)));



            try {




             //   server.handle(ID, streamIn.readLine());
            } catch (Exception ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
            //    stop = true;
            }
            //if (stop)
              //  break;
        }
    }

    public void open() throws IOException {
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      //  streamOut = new PrintWriter(socket.getOutputStream());
        streamOut = socket.getOutputStream();
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }

    public void cliente_flush(){
       // streamOut.flush();

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
package XFactHD.mineduino.common.utils.serial;

import XFactHD.mineduino.common.utils.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    private Thread thread = null;
    private boolean stop = false;

    private List<ChatServerThread> clients = new ArrayList<>();
    private ChatServerThread client = null;

    public Server() {
        try {
            serverSocket = new ServerSocket(8888);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        start();
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(serverSocket.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        client = new ChatServerThread(this, socket);
        try {
            client.open();
            client.start();
            clients.add(client);
        } catch (IOException ioe) {
            System.out.println("Error opening thread: " + ioe);
        }
        afficheClients();
    }

    private int findClient(int ID) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == ID)
                return i;
        }
        return -1;
    }
    private int findClientByUserId(int id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUser_id() == id)
                return i;
        }
        return -1;
    }
    public void SyncronizeSendMessage(String message){

        ChatServerThread participant = null;

        for (int i = 0; i < clients.size(); i++) {

            participant = clients.get(findClientByUserId(clients.get(i).getID()));

            if (participant != null) {
                participant.send(message);

            }
        }
    }
    private long time = 0;
    public synchronized void handle(int ID, String input) throws IOException {



       if(clients.get(findClient(ID)).getUser_id()==-1){
            try {
              //  int user_id = obj.getInt("user_id");
                clients.get(findClient(ID)).setUser_id(ID);
            } catch (Exception ignored) {}
        }
        time = System.currentTimeMillis();
        ThreadCommHandler.executeQueuedTasks();
        try { sleep(50 - (System.currentTimeMillis() - time));


            SerialHandler.getSerialHandler().serialEvent(input);



        }
        catch (InterruptedException e)
        {
            LogHelper.error("Thread '" + Thread.currentThread().getName() + "' was interrupted!");
            e.printStackTrace();
        }
       // SerialHandler.getSerialHandler().SerialEvent(input);
//        else{
//            try {
//
//                ChatServerThread participant = null;
//
//                for (int i = 0; i < clients.size(); i++) {
//                try{
// if(clients.get(i).getUser_id() != ID) {
//
//     participant = clients.get(findClientByUserId(clients.get(i).getID()));
//
//     if (participant != null) {
//         participant.send(input);
//
//     }
// }
//
//
//                }catch (Exception ignored){}
//
//
//
//
//            }
//            } catch (Exception ignored) {
//                ignored.printStackTrace();
//
//            }
//        }*/
      //  SyncronizeSendMessage(input);
        System.out.println("User " + clients.get(findClient(ID)).getUser_id() + ": " + input);
    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            ChatServerThread toTerminate = clients.get(pos);
            clients.remove(toTerminate);
            toTerminate.end();
        }
        afficheClients();
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        stop = true;
    }

    void afficheClients(){
        for(ChatServerThread c : clients){
            System.out.println("thread ID: "+c.getID()+" | User Id: " + c.getUser_id());
        }
    }
}

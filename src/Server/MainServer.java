package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MainServer {
    private static BufferedReader bufferedReader = null;
    private static PrintWriter printWriter = null;
    private static int studentId;
    private static ArrayList<Worker> db = new ArrayList<Worker>();

    public static void main(String args[])
    {

        try
        {
            ServerSocket serverSocket = new ServerSocket(5555);
            System.out.println("Server has been started successfully.");


            while(true)
            {
                Socket socket = serverSocket.accept();		//TCP Connection
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                studentId = Integer.valueOf(bufferedReader.readLine());
                System.out.println(studentId);
                    Worker wt = new Worker(socket, studentId);
                    db.add(wt);
                    Thread t = new Thread(wt);
                    t.start();
                    //printWriter.println("Access Denied to the Server. Connection Terminating... .... ....");
                    //printWriter.flush();
//                WorkerCount++;
//                System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + WorkerCount);

            }
        }
        catch(Exception e)
        {
            System.err.println("Problem in ServerSocket operation. Exiting main.");
        }
    }
}

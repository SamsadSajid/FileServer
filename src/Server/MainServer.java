package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainServer {
    private static BufferedReader bufferedReader = null;
    private static PrintWriter printWriter = null;
    private static int studentId;
    private static String ipAddress;
    private static int port;
    private static ArrayList<Worker> db = new ArrayList<Worker>();
    private static Map<Integer, NetworkAddress> map = new HashMap<Integer, NetworkAddress>();

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
                ipAddress = socket.getInetAddress().toString();
                port = socket.getPort();
                System.out.println("main server got "+studentId+" on ip "+ipAddress+" on port "+port);
                NetworkAddress networkAddress = new NetworkAddress(ipAddress, port);
                boolean a = map.containsKey(studentId);
                System.out.println(a);
//                for (Map.Entry<Integer, NetworkAddress> entry : map.entrySet()) {
//                    int key = entry.getKey();
//                    NetworkAddress studnetworkaddress = entry.getValue();
//                    System.out.println("Id " + key + " in map with ip " + studnetworkaddress.ipAddress + " , port " + studnetworkaddress.port);
//                }
                if(map.containsKey(studentId)){
                    System.out.println("User is already logged in");
                    String response = "logged in";
                    printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(response);
                    printWriter.flush();
                }
                else {
                    map.put(studentId, networkAddress);
//                    for (Map.Entry<Integer, NetworkAddress> entry : map.entrySet()) {
//                        int key = entry.getKey();
//                        NetworkAddress studnetworkaddress = entry.getValue();
//                        System.out.println("Id " + key + " in map with ip " + studnetworkaddress.ipAddress + " , port " + studnetworkaddress.port);
//                    }
                    Worker wt = new Worker(socket, studentId, map);
                    //db.add(wt);
                    Thread t = new Thread(wt);
                    t.start();
                }
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

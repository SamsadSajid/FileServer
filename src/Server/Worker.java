package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Worker implements Runnable{
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private int studentId = 0;
    private String fileName;
    private long fileSize;
    private String ipAddress;
    private int port;
    private static ArrayList<String> fileChunkList = new ArrayList<>();
    private String fileDestination = "/home/saad/IdeaProjects/filehsharing/out/production/filehsharing";

    public Worker(Socket s, int id)
    {
        this.socket = s;

        try
        {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.studentId = id;
        this.ipAddress = s.getInetAddress().toString();
        this.port = s.getPort();
    }

    public void run()
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter printWriter = new PrintWriter(this.os);

        printWriter.println("Welcome " + this.studentId + " with IP Address " + this.ipAddress + " on Port " +this.port);
        printWriter.flush();
        System.out.println("Welcome " + this.studentId + " with IP Address " + this.ipAddress + " on Port " +this.port);

        try {
            String elements;
            while((elements = bufferedReader.readLine()) != null) {
                fileChunkList.add(elements);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveFile(fileChunkList);

        try
        {
            this.is.close();
            this.os.close();
            this.socket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

//        MainServer.workerThreadCount--;
//        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
//                + MainServer.workerThreadCount);
    }

    private void saveFile(ArrayList<String> fileChunkList) {
        File[] file = new File[fileChunkList.size()];
        byte AllContents[] = null;
        
        int FILE_NUMBER = fileChunkList.size();
        int FILE_LENGTH = 0;
        int CURRENT_LENGTH=0;
        int TOTAL_FILE_SIZE = 0;

        for ( int i=0; i<FILE_NUMBER; i++)
        {
            file[i] = new File (fileChunkList.get(i));
            TOTAL_FILE_SIZE+=file[i].length();
        }

        try {
            InputStream inputStream = null;
            AllContents= new byte[TOTAL_FILE_SIZE]; // Length of All Files, Total Size
            
            for ( int num=0;num<FILE_NUMBER; num++)
            {
                inputStream = new BufferedInputStream ( new FileInputStream( file[num] ));
                FILE_LENGTH = (int) file[num].length();
                inputStream.read(AllContents, CURRENT_LENGTH, FILE_LENGTH);
                CURRENT_LENGTH+=FILE_LENGTH;
                inputStream.close();
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found " + e);
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while reading the file " + ioe);
        }
        finally
        {
            write (AllContents, fileDestination);
        }

        System.out.println("Merge was executed successfully.!");
    }

    private void write(byte[] allContents, String fileDestination) {
        try {
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(fileDestination));
                outputStream.write(allContents);
                System.out.println("Writing Process Was Performed");
            }
            finally {
                outputStream.close();
            }
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

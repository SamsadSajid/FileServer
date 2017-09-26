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
    private String fileDestination = "C:\\Users\\User\\IdeaProjects\\FileServer\\out\\production\\filehsharing\\Server";
    private int chunkSize;
    private String fileChunkName;
    private int totalBytesRead = 0;

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

        //mergeFile(fileChunkList);



        try {
            fileName = bufferedReader.readLine();
            fileSize = Integer.valueOf(bufferedReader.readLine());
            System.out.println("File name received "+fileName+" file size "+fileSize);
            while (totalBytesRead < fileSize) {
                chunkSize = Integer.valueOf(bufferedReader.readLine());
                System.out.println(chunkSize);
                fileChunkName = bufferedReader.readLine();
                System.out.println("Size " + chunkSize + " name " + fileChunkName);
                fileChunkList.add(fileChunkName);
                System.out.println("call hoise");
                receiveFile(chunkSize, fileChunkName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            String elements;
//            while((elements = bufferedReader.readLine()) != null) {
//                fileChunkList.add(elements);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    private void receiveFile(int chunkSize, String fileChunkName) throws IOException {
        System.out.println("1");
        int bytesRead;
        System.out.println("2");
        byte[] storage = new byte[chunkSize];
        System.out.println("3");
        bytesRead = is.read(storage);
        for(int i=0; i<storage.length; i++){
            System.out.println(storage[i]);
        }
        System.out.println("File received "+bytesRead);
        totalBytesRead+=bytesRead;
        System.out.println("So far read " + totalBytesRead);
        write(storage, fileChunkName);
        storage = null;
    }

    private void write(byte[] allContents, String fileChunkName) {
        try {
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(fileDestination + fileChunkName));
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


    private void mergeFile(ArrayList<String> fileChunkList) {
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

}

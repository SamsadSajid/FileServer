package Server;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Runnable{
    private Socket socket;
    private InputStream is = null;
    private OutputStream os;
    private BufferedReader bufferedReader;
    private static BufferedInputStream bufferedInputStream = null;
    private PrintWriter printWriter;

    private int studentId = 0;
    private int receiverId;
    private String fileName;
    private int fileSize;
    private String fileId;
    private String ipAddress;
    private int port;
    private static ArrayList<String> fileChunkList = new ArrayList<>();
    private String fileDestination = "/home/shamsad/IdeaProjects/FileServer/out/production/filehsharing/Server/";
    private int chunkSize;
    private String fileChunkName;
    private int totalBytesRead = 0;
    private int numberOfChunks = 0;
    private static Map<Integer, NetworkAddress> mapLog = new HashMap<Integer, NetworkAddress>();

    private String head ="";
    private String tail ="";
    private String frame ="";
    private String sequenceNumber = "";
    private String payload = "";
    private String checkSum = "";
    private static int framelength;

    public Worker(Socket s, int id, Map<Integer, NetworkAddress> map)
    {
        this.socket = s;

        try
        {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
            this.studentId = id;
            this.ipAddress = s.getInetAddress().toString();
            this.port = s.getPort();

            mapLog.putAll(map);

            for (Map.Entry<Integer, NetworkAddress> entry : mapLog.entrySet()) {
                System.out.println("ready");
                int key = entry.getKey();
                NetworkAddress studnetworkaddress = entry.getValue();
                System.out.println("Id " + key + " in map with ip " + studnetworkaddress.ipAddress + " , port " + studnetworkaddress.port);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }


    }

    public void run()
    {
        bufferedReader = new BufferedReader(new InputStreamReader(this.is));
        printWriter = new PrintWriter(this.os);

        printWriter.println("Welcome " + this.studentId + " with IP Address " + this.ipAddress + " on Port " +this.port);
        printWriter.flush();
        System.out.println("Welcome " + this.studentId + " with IP Address " + this.ipAddress + " on Port " +this.port);

        //mergeFile(fileChunkList);



        try {
            receiverId = Integer.valueOf(bufferedReader.readLine());
            if (!(mapLog.containsKey(receiverId))){
                System.out.println("Receiver is offline");
                String response = "offline";
                printWriter.println(response);
                printWriter.flush();
            }
            else {
                String response = "online";
                printWriter.println(response);
                printWriter.flush();
                fileName = bufferedReader.readLine();
                fileSize = Integer.valueOf(bufferedReader.readLine());
                fileId = generateFileId(fileName, studentId, receiverId);
                System.out.println("File name received " + fileName + " file size " + fileSize);
                int p=0;
                //int tot = 0;
                String ss;
                ss = bufferedReader.readLine();
                chunkSize = Integer.valueOf(ss);
                System.out.println(chunkSize);

                while (totalBytesRead < fileSize) {
                    if ( fileSize-totalBytesRead < chunkSize ){
                        chunkSize = fileSize-totalBytesRead;
                    }

//                    System.out.println("fucking reader "+ss);
//                    ss = ss.replaceAll("\\D+", "");
//                    System.out.println("Buffer reader in string after taking only int " + ss);


                    //fileChunkName = bufferedReader.readLine();
                    fileChunkName="metadata_"+numberOfChunks+".bin";
                    System.out.println("Size " + chunkSize + " name " + fileChunkName);
//                    checkSum = bufferedReader.readLine();
//                    checkSum = checkSum.replaceAll("\\D+", "");
//                    System.out.println("server checksum "+checkSum);
                    framelength = Integer.valueOf(bufferedReader.readLine());
                    System.out.println("server e frame length "+ framelength);
                    fileChunkList.add(fileDestination+fileChunkName);
                    System.out.println(fileChunkList.get(p));
                    System.out.println("call hoise");
                    receiveFile(fileId, chunkSize, fileChunkName);
                    p++;
                }
                response = bufferedReader.readLine();
                System.out.println("last e resp"+response);
                if (response.contains("File sent successfully")){
                    mergeFile(fileChunkList, fileId);
                }
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

//        try
//        {
//            this.is.close();
//            this.os.close();
//            this.socket.close();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }

//        MainServer.workerThreadCount--;
//        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
//                + MainServer.workerThreadCount);
    }

    private String generateFileId(String fileName, int studentId, int receiverId) {
        String setter = String.valueOf(studentId);
        String getter = String.valueOf(receiverId);
        return setter+"-"+getter+"_"+fileName;
    }

    private void receiveFile(String fileId, int chunkSize, String fileChunkName) throws IOException {
        head="";
        sequenceNumber="";
        checkSum="";
        payload="";
        tail="";
        System.out.println("echo 1");
        int bytesRead;
        System.out.println("echo 2");

        System.out.println("echo 3");
//          InputStream inputStream = socket.getInputStream();
        // framelength /= 8;
//        byte[] storage = new byte[framelength];
        char[] storage = new char[framelength];
        bytesRead = bufferedReader.read(storage, 0, framelength);
//          bytesRead = is.read(storage, 0, framelength);
//        bytesRead = inputStream.read(storage,0, framelength);
//        for(int i=0; i<storage.length; i++){
//            System.out.println(storage[i]);
//        }
        String frame = "";
//        for(byte b: storage){
//            // System.out.println("b "+ b +"... "+Integer.toBinaryString(b &255 | 256).substring(1));
//            // tmp
//            frame += Integer.toBinaryString(b &255 | 256).substring(1);
//            // System.out.println("payload is "+frame);
//        }
//        for (int j=0; j< storage.length; j++){
//            System.out.println("byte me lust "+storage[j]);
//            frame += storage[j];
//            if(j>=0 && j<8){
//                head+=storage[j];
//            }
//            else if(j>=8 && j<16){
//                sequenceNumber+=storage[j];
//            }
//        }
        for (int j=0;j<8;j++){
            head+=storage[j];
        }
        System.out.println("head is "+head);
        for (int j=8; j<16;j++){
            sequenceNumber+=storage[j];
        }
        System.out.println("sequencenumber is "+sequenceNumber);

//        for (int j=storage.length -1; j>=0; j--){
//            if(j<=storage.length-1 && j>=storage.length -8){
//                System.out.println(j);
//                tail+=storage[j];
//            }
//            else if(j<=storage.length-9 && j>=storage.length-16){
//                System.out.println(j);
//                checkSum+=storage[j];
//            }
//        }
        for (int j=storage.length -1; j>=storage.length-8;j--){
            tail+=storage[j];
        }
        System.out.println("tail is "+tail);
        for (int j=storage.length -16; j<=storage.length-9;j++){
            checkSum+=storage[j];
        }
        System.out.println("checksum is "+checkSum);

        int track1 = head.length()+sequenceNumber.length();
        int track2 = checkSum.length()+tail.length();

        for (int j=track1; j<storage.length-track2; j++){
            payload+=storage[j];
        }

        System.out.println("pyload is "+payload);
        bytesRead = payload.length()/8;

        System.out.println("File received "+bytesRead);
        totalBytesRead+=bytesRead;
        numberOfChunks++;
        System.out.println("So far read " + totalBytesRead);
        CharBuffer charBuffer = CharBuffer.wrap(storage);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        write(fileId, bytes, fileChunkName);
        //storage = null;
    }

    private void write(String fileId, byte[] allContents, String fileChunkName) {
        try {
            BufferedOutputStream outputStream =  new BufferedOutputStream(new FileOutputStream(fileDestination + fileId + "_" + fileChunkName));;
            PrintWriter printWriter = new PrintWriter(this.os);
            try {
                //1-2_datapath.pptx_metadata_0.bin
                outputStream.write(allContents);
                outputStream.flush();
                System.out.println("Writing Process Was Performed");
                printWriter.println("Writing Process Was Performed");
                printWriter.flush();
                System.out.println("write e "+fileChunkName);
                //printWriter.close();
            }
            catch (FileNotFoundException fl){
                fl.printStackTrace();
            }
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }


    private void mergeFile(ArrayList<String> fileChunkList, String fileId) {
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
            writeFullFile (fileId, AllContents, fileDestination);
        }

        System.out.println("Merge was executed successfully.!");
    }

    private void writeFullFile(String fileId, byte[] allContents, String fileDestination){
        try {
            OutputStream outputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(fileDestination + fileId ));
                outputStream.write(allContents);
                System.out.println("Writing Process Was Performed");
            }
            finally {
                outputStream.flush();
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
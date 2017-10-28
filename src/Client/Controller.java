package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Controller {

    public TextField tStudentId;
    public TextField tFileName;
    public TextField tFileSize;
    public Label lStudentId;
    public Label lFileName;
    public Label lFileSize;
    public Button bLogIn;
    public Button bSend;
    public Button bLogOut;
    public TextArea textAreaMsg;


    private static Socket socket = null;
    private static BufferedReader bufferedReader = null;
    private static PrintWriter printWriter ;

    private static String storageFolder = "/home/shamsad/IdeaProjects/FileServer/out/production/filehsharing/Server/";

    private String head = "01111110";
    private String tail = "01111110";
    private String frame;
    private String sequenceNumber = "";
    private String payload = "";
    private String checkSum = "";
    private int frameLength;

    @FXML
    public void initialize() throws IOException {
        textAreaMsg.setEditable(false);
        textAreaMsg.appendText("\n");
        lFileName.setVisible(false);
        tFileName.setVisible(false);
        lFileSize.setVisible(false);
        tFileSize.setVisible(false);
        bSend.setVisible(false);
        bLogOut.setVisible(false);
    }

    public void LogInOnClickListener(ActionEvent actionEvent) {
        try {
            socket = new Socket("localhost", 5555);


            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(tStudentId.getText());
            printWriter.flush();
            String feed = bufferedReader.readLine();
            if (feed.contains("logged in")){
                textAreaMsg.appendText("You cannot access Server from multiple Ip address\n");
                socket.close();
            }else {
                textAreaMsg.appendText("Connected to the Server!!!\n");
                lStudentId.setVisible(true);
                tStudentId.setVisible(true);
                bLogIn.setVisible(false);
                lFileName.setVisible(true);
                tFileName.setVisible(true);
                lFileSize.setVisible(true);
                tFileSize.setVisible(true);
                bSend.setVisible(true);
                tStudentId.clear();
            }
            //textAreaMsg.appendText(feed);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem in connecting with the server. Process Terminating... .... ....");
            System.exit(1);
        }

    }

    public void SendOnClickListener(ActionEvent actionEvent) throws IOException {
        String fileName = tFileName.getText();
        //int fileSize = Integer.valueOf(tFileSize.getText())
        File fil = new File(fileName);
        int fileSize = (int) fil.length();
        System.out.println("Client e fileSize "+fileSize);
        int receiverId = Integer.valueOf(tStudentId.getText());
        //System.out.println("1");
        //printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(receiverId);
        printWriter.println(fileName);
        printWriter.println(fileSize);
        //System.out.println("2");
        printWriter.flush();
        int chunkSize = 32; //will come from server
        //System.out.println("3");
        String feed = bufferedReader.readLine();
        System.out.println(feed);
        //System.out.println("4");
        if (feed.contains("offline")){
            textAreaMsg.appendText("Receiver is currently offline. Please try again later\n");
            tStudentId.clear();
            tFileName.clear();
            tFileSize.clear();
        }
        else {
            //System.out.println("baal");
            sendFile(fileName, fileSize, chunkSize);
        }
    }

    private void sendFile(String file, int fileSize, int chunkSize) throws IOException {
        //InputStream inputStream = null;
        OutputStream outputStream = socket.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream inputStream = new  BufferedInputStream(fileInputStream);
        //printWriter = new PrintWriter(socket.getOutputStream());
        byte [] storage = null;
        // byte [] tmp = null;
        int numberOfChunks = 0;
        int totalBytesRead = 0;
        ArrayList<String> fileChunkList = new ArrayList<>();
        printWriter.println(chunkSize);
        printWriter.flush();

        while(totalBytesRead < fileSize){
            String fileChunkName ="metadata_"+numberOfChunks+".bin";
            sequenceNumber = Integer.toBinaryString(numberOfChunks &255 | 256).substring(1);
            payload = "";
            System.out.println("sequence number "+sequenceNumber);
            int bytesRemaining = fileSize - totalBytesRead;
            if ( bytesRemaining < chunkSize ){
                chunkSize = bytesRemaining;
            }
            storage = new byte[chunkSize]; //Temporary Byte Array
            // tmp = new byte[chunkSize];
            System.out.println("From Client, chunk size "+chunkSize+" chunk name "+fileChunkName);
            //printWriter.flush();

            //printWriter.flush();
            //printWriter.println(fileChunkName);
            //printWriter.flush();
            //printWriter.flush();
            //System.out.println("baal");
            int bytesRead = inputStream.read(storage, 0, chunkSize);
            System.out.println("bytesRead "+bytesRead);
            for(byte b: storage){
                // System.out.println("b "+ b +"... "+Integer.toBinaryString(b &255 | 256).substring(1));
                // tmp
                payload += Integer.toBinaryString(b &255 | 256).substring(1);
                System.out.println("payload is "+payload);
            }

            System.out.println("length of payload "+payload.length());
            System.out.println("before payload "+payload);
            int ones = payload.length() - payload.replaceAll("1", "").length();
            System.out.println("After payload  "+payload);
            System.out.println("num of 1 "+ones);

            checkSum = Integer.toBinaryString((ones) &255 | 256).substring(1);
            System.out.println("checksum "+checkSum);

//            printWriter.println(checkSum);
//            printWriter.flush();

            String body = sequenceNumber + payload + checkSum;
            String match = "11111";
            int firstIndex = body.indexOf(match);
            if(firstIndex == -1){
                System.out.println("No consecutive 5 ones in frame.");
            }
            else{
                body = body.substring(0, firstIndex+5)+"0"+body.substring(firstIndex+5, body.length());
            }
            frame = head + body + tail;
            System.out.println("frame is "+frame);
            frameLength = frame.length();
            int sendLength = frameLength / 8;

            printWriter.println(frameLength);
            printWriter.flush();

            System.out.println("frame length "+frameLength);
            System.out.println("send len "+ sendLength);

            byte [] byteArr = frame.getBytes();

            System.out.println("byte array length "+byteArr.length);
            String yoo="";
//            for(byte b: byteArr){
//                // System.out.println("b "+ b +"... "+Integer.toBinaryString(b &255 | 256).substring(1));
//                // tmp
//                yoo += Integer.toBinaryString(b &255 | 256).substring(1);
//                // System.out.println("payload is "+frame);
//            }
            for(int j=0; j<byteArr.length; j++){
                System.out.println("byte me "+byteArr[j]);
            }
            // System.out.println("yoo is "+yoo);

            //byte[] clientStorage = new byte[sendLength];

            if ( bytesRead > 0) // If bytes read is not empty
            {
                totalBytesRead += bytesRead;
                numberOfChunks++;
                System.out.println("Number of Chunks "+numberOfChunks);
            }

//            for(int j=0; j < sendLength; j++){
//                for (int )
//            }

            //printWriter.println(numberOfChunks);
            //printWriter.flush();

            // outputStream.write(storage);
            outputStream.write(byteArr);
            outputStream.flush();

            for(int i=0; i<storage.length; i++) {
                System.out.println(storage[i]);
            }

//          writeToServer(storage, "C:\\Users\\User\\IdeaProjects\\FileServer\\out\\production\\filehsharing\\Client"+fileChunkName);
            fileChunkList.add(storageFolder+fileChunkName);
            System.out.println("Total Bytes Read: "+totalBytesRead);
            String serverFeed = bufferedReader.readLine();
            System.out.println("Acknowledgement: "+serverFeed);
            if (!(serverFeed.contains("Writing Process Was Performed"))){
                textAreaMsg.appendText("Error occurred while transferring file. Process is being terminated. Please try again\n");
            }
            else{
                System.out.println("Transmitting File... .... .... .... ....");
                textAreaMsg.appendText("Transmitting File... .... .... .... ....\n ");
            }
        }
        for(int i=0; i<fileChunkList.size();i++){
            System.out.println(fileChunkList.get(i));
        }
        outputStream.flush();

        //fileChunkList might return to somebody???
        System.out.println("File sent successfully");
        printWriter.println("File sent successfully");
        printWriter.flush();

    }

    public void LogOutOnClickListener(ActionEvent actionEvent) {
        // code
    }

//    public void writeToServer(byte[] fileStorage, String Destination) throws IOException {
//        OutputStream outputStream = null;
//        outputStream = new BufferedOutputStream(new FileOutputStream(Destination));
//        outputStream.write(fileStorage);
//        System.out.println("Writing Process Was Performed");
//        textAreaMsg.appendText("File has been sent to server\n");
//        outputStream.close();
//    }
}
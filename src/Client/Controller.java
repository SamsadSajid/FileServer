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
    private static PrintWriter printWriter = null;

    private static String storageFolder = "C:\\Users\\User\\IdeaProjects\\FileServer\\out\\production\\filehsharing\\Server";


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
        long fileSize = Long.valueOf(tFileSize.getText());
        int receiverId = Integer.valueOf(tStudentId.getText());
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(receiverId);
        printWriter.println(fileName);
        printWriter.println(fileSize);
        printWriter.flush();
        long chunkSize = 100; //will come from server
        String feed = bufferedReader.readLine();
        if (feed.contains("offline")){
            textAreaMsg.appendText("Receiver is currently offline. Please try again later\n");
            tStudentId.clear();
            tFileName.clear();
            tFileSize.clear();
        }
        else {
            sendFile(fileName, fileSize, chunkSize);
        }
    }

    private void sendFile(String file, long fileSize, long chunkSize) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = socket.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        inputStream = new  BufferedInputStream(fileInputStream);
        printWriter = new PrintWriter(socket.getOutputStream());
        byte [] storage = null;
        int numberOfChunks = 0;
        long totalBytesRead = 0;
        ArrayList<String> fileChunkList = new ArrayList<>();

        while(totalBytesRead < fileSize){
            String fileChunkName ="metadata_"+numberOfChunks+".bin";
            long bytesRemaining = fileSize - totalBytesRead;
            if ( bytesRemaining < chunkSize ){
                chunkSize = bytesRemaining;
            }
            storage = new byte[(int) chunkSize]; //Temporary Byte Array
            printWriter.println(chunkSize);
            printWriter.println(fileChunkName);
            printWriter.flush();
            int bytesRead = inputStream.read(storage, 0, (int)chunkSize);
            System.out.println("bytesRead "+bytesRead);
            if ( bytesRead > 0) // If bytes read is not empty
            {
                totalBytesRead += bytesRead;
                numberOfChunks++;
            }

            //printWriter.println(numberOfChunks);
            //printWriter.flush();

            outputStream.write(storage);
            outputStream.flush();

            for(int i=0; i<storage.length; i++) {
                System.out.println(storage[i]);
            }

//          writeToServer(storage, "C:\\Users\\User\\IdeaProjects\\FileServer\\out\\production\\filehsharing\\Client"+fileChunkName);
            fileChunkList.add(storageFolder+fileChunkName);
            System.out.println("Total Bytes Read: "+totalBytesRead);
        }
        outputStream.flush();

        //fileChunkList might return to somebody???

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

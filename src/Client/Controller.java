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
    public TextField tPort;
    public TextField tFileName;
    public TextField tFileSize;
    public Label lStudentId;
    public Label lPort;
    public Label lFileName;
    public Label lFileSize;
    public Button bLogIn;
    public Button bSend;
    public TextArea textAreaMsg;

    private static Socket socket = null;
    private static BufferedReader bufferedReader = null;
    private static PrintWriter printWriter = null;

    @FXML
    public void initialize() throws IOException {
        textAreaMsg.setEditable(false);
        textAreaMsg.appendText("\n");
        lFileName.setVisible(false);
        tFileName.setVisible(false);
        lFileSize.setVisible(false);
        tFileSize.setVisible(false);
        lPort.setVisible(false);
        tPort.setVisible(false);
        bSend.setVisible(false);
    }

    public void LogInOnClickListener(ActionEvent actionEvent) {
        try {
            socket = new Socket("localhost", 5555);
            textAreaMsg.appendText("Connected to the Server!!!\n");

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(tStudentId.getText());
            printWriter.flush();
            String feed = bufferedReader.readLine();
            //textAreaMsg.appendText(feed);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem in connecting with the server. Process Terminating... .... ....");
            System.exit(1);
        }
        lStudentId.setVisible(false);
        tStudentId.setVisible(false);
        bLogIn.setVisible(false);
        lFileName.setVisible(true);
        tFileName.setVisible(true);
        lFileSize.setVisible(true);
        tFileSize.setVisible(true);
        bSend.setVisible(true);
    }

    public void SendOnClickListener(ActionEvent actionEvent) throws IOException {
        String fileName = tFileName.getText();
        long fileSize = Long.valueOf(tFileSize.getText());
        long chunkSize = 100; //will come from server
        sendFile(fileName, fileSize, chunkSize);
    }

    private void sendFile(String file, long fileSize, long chunkSize) throws IOException {
        InputStream inputStream = null;
        FileInputStream fileInputStream = new FileInputStream(file);
        inputStream = new  BufferedInputStream(fileInputStream);
        byte [] storage = null;
        int numberOfChunks = 0;
        long totalBytesRead = 0;
        ArrayList<String> fileChunkList = new ArrayList<>();

        while(totalBytesRead < fileSize){
            String fileChunkName ="metadata"+numberOfChunks+".bin";
            long bytesRemaining = fileSize - totalBytesRead;
            if ( bytesRemaining < chunkSize ){
                chunkSize = bytesRemaining;
            }
            storage = new byte[(int) chunkSize]; //Temporary Byte Array
            int bytesRead = inputStream.read(storage, 0, (int)chunkSize);

            if ( bytesRead > 0) // If bytes read is not empty
            {
                totalBytesRead += bytesRead;
                numberOfChunks++;
            }

            writeToServer(storage, "/home/saad/IdeaProjects/filehsharing/out/production/filehsharing"+fileChunkName);
            fileChunkList.add("/home/saad/IdeaProjects/filehsharing/out/production/filehsharing"+fileChunkName);
            System.out.println("Total Bytes Read: "+totalBytesRead);
        }

        //fileChunkList might return to somebody???
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(fileChunkList);
        printWriter.flush();

    }
    
    public void writeToServer(byte[] fileStorage, String Destination) throws IOException {
        OutputStream outputStream = null;
        outputStream = new BufferedOutputStream(new FileOutputStream(Destination));
        outputStream.write(fileStorage);
        System.out.println("Writing Process Was Performed");
        textAreaMsg.appendText("File has been sent to server\n");
        outputStream.close();
    }
}

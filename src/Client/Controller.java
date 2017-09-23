package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
    }

    public void SendOnClickListener(ActionEvent actionEvent) {
    }
}

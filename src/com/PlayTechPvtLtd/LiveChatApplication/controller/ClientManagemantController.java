package com.PlayTechPvtLtd.LiveChatApplication.controller;

import animatefx.animation.FadeIn;
import com.PlayTechPvtLtd.LiveChatApplication.model.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static com.PlayTechPvtLtd.LiveChatApplication.controller.CreateNewUserAccountController.users;

/**
 * @author : Nimesh Piyumantha
 * @since : 0.1.0
 **/
public class ClientManagemantController extends Thread implements Initializable {

    public Label clientName;
    public ImageView imgMenu;
    public AnchorPane profilePane;
    public ImageView imgPhotoMain;
    public Label lblUserName;
    public Label lblEmail;
    public Label lblPhone;
    public Label lblGender;
    public TextField fileChoosePath;
    public AnchorPane chatPane;
    public TextField txtMassage;
    public TextArea txtArea;
    public ImageView imgSend;
    public ImageView imgPhoto;
    public Circle showProPic;

    public boolean toggleChat = false, toggleProfile = false;
    public boolean saveControl = false;

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    private File filePath;

    public void connectSocket() {
        try {
            socket = new Socket("localhost", 8000);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = reader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];
                System.out.println(cmd);
                StringBuilder fullMsg = new StringBuilder();
                for (int i = 1; i < tokens.length; i++) {
                    fullMsg.append(tokens[i]);
                }
                System.out.println(fullMsg);
                if (cmd.equalsIgnoreCase(CreateNewUserAccountController.username + ":")) {
                    continue;
                } else if (fullMsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
                txtArea.appendText(msg + "\n");
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProfile() {
        for (User user : users) {
            if (CreateNewUserAccountController.username.equalsIgnoreCase(user.name)) {
                lblUserName.setText(user.name);
                lblUserName.setOpacity(1);
                lblEmail.setText(user.email);
                lblEmail.setOpacity(1);
                lblPhone.setText(user.phoneNo);
                lblGender.setText(user.gender);
            }
        }
    }

    public void imgMenuOnAction(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().equals(imgMenu) && !toggleProfile) {
            new FadeIn(profilePane).play();
            profilePane.toFront();
            chatPane.toBack();
            toggleProfile = true;
            toggleChat = false;
            setProfile();
        } else if (mouseEvent.getSource().equals(imgMenu) && toggleProfile) {
            new FadeIn(chatPane).play();
            chatPane.toFront();
            toggleProfile = false;
            toggleChat = false;
        }
    }

    public void imgCamaraOnAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
    }

    public void chooseImageButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    public void saveImageOnAction(ActionEvent actionEvent) {
        if (saveControl) {
            try {
                BufferedImage bufferedImage = ImageIO.read(filePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imgPhotoMain.setImage(image);
                showProPic.setFill(new ImagePattern(image));
                saveControl = false;
                fileChoosePath.setText("");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendMessageByKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    public void send() {
        String msg = txtMassage.getText();
        writer.println(CreateNewUserAccountController.username + ": " + msg);
        txtArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        txtArea.appendText("Me: " + msg + "\n");
        txtMassage.setText("");
        if (msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
            System.exit(0);
        }
    }

    public void btnSendOnAction(MouseEvent mouseEvent) {
        send();
        for (User user : users) {
            System.out.println(user.name);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        if (CreateNewUserAccountController.gender.equalsIgnoreCase("Male")) {
            image = new Image("com/PlayTechPvtLtd/LiveChatApplication/view/assests/images/userMale.png", false);
        } else {
            image = new Image("com/PlayTechPvtLtd/LiveChatApplication/view/assests/images/userFemale.png", false);
            imgPhotoMain.setImage(image);
        }
        showProPic.setFill(new ImagePattern(image));
        clientName.setText(CreateNewUserAccountController.username);
        connectSocket();
    }
}

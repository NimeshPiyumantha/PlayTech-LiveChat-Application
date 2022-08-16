package com.PlayTechPvtLtd.LiveChatApplication.controller;

import animatefx.animation.FadeIn;
import com.PlayTechPvtLtd.LiveChatApplication.model.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    public ImageView imgSend;
    public ImageView imgPhoto;
    public Circle showProPic;

    public boolean toggleChat = false, toggleProfile = false;
    public boolean saveControl = false;
    public AnchorPane emojiAnchorePane;
    public ImageView emojiBtn;
    public VBox vBox;

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    private File filePath;

    /**
     * Client Socket Code
     */
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

    /**
     * Messages send using 2D array with Code
     */
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

                String[] massageAr = msg.split(" ");
                String string = "";
                for (int i = 0; i < massageAr.length - 1; i++) {
                    string += massageAr[i + 1] + " ";
                }

                Text text = new Text(string);
                String fChar = "";

                if (string.length() > 3) {
                    fChar = string.substring(0, 3);
                }

                if (fChar.equalsIgnoreCase("img")) {
                    string = string.substring(3, string.length() - 1);

                    File file = new File(string);
                    Image image = new Image(file.toURI().toString());

                    ImageView imageView = new ImageView(image);

                    imageView.setFitWidth(70);
                    imageView.setFitHeight(70);

                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.BOTTOM_RIGHT);

                    if (!cmd.equalsIgnoreCase(clientName.getText())) {
                        vBox.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);

                        Text text1 = new Text("  " + cmd + " :");
                        hBox.getChildren().add(text1);
                        hBox.getChildren().add(imageView);
                    } else {
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(imageView);
                        Text text1 = new Text(": Me ");
                        hBox.getChildren().add(text1);
                    }

                    Platform.runLater(() -> vBox.getChildren().addAll(hBox));

                } else {
                    TextFlow tempTextFlow = new TextFlow();

                    if (!cmd.equalsIgnoreCase(clientName.getText() + ":")) {
                        Text name = new Text(cmd + " ");
                        name.getStyleClass().add("name");
                        tempTextFlow.getChildren().add(name);
                    }

                    tempTextFlow.getChildren().add(text);
                    tempTextFlow.setMaxWidth(120);

                    TextFlow textFlow = new TextFlow(tempTextFlow);
                    textFlow.setStyle("-fx-background-color:#ff6b81;" + "-fx-background-radius: 20px;"+"-fx-font-size: 17px;");
                    textFlow.setPadding(new Insets(5, 10, 5, 10));
                    HBox hBox = new HBox(10);
                    hBox.setPadding(new Insets(5));

                    if (!cmd.equalsIgnoreCase(clientName.getText() + ":")) {
                        vBox.setAlignment(Pos.TOP_LEFT);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.getChildren().add(textFlow);
                    } else {
                        Text text1 = new Text(fullMsg + ": Me");
                        TextFlow textFlow1 = new TextFlow(text1);
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);
                        hBox.getChildren().add(textFlow1);
                        textFlow1.setStyle("-fx-background-color:#7bed9f;" + "-fx-background-radius: 20px;"+"-fx-font-size: 17px;");
                        textFlow1.setPadding(new Insets(5, 10, 5, 10));
                    }
                    Platform.runLater(() -> vBox.getChildren().addAll(hBox));
                }

                System.out.println(fullMsg);
                if (cmd.equalsIgnoreCase(CreateNewUserAccountController.username + ":")) {
                    continue;
                } else if (fullMsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Set Profile Details Code
     */
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

    /**
     * Menu Controller Code
     */
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

    /**
     * Send Images chooser Code
     */
    public void imgCamaraOnAction(MouseEvent mouseEvent) {
        try {
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            this.filePath = fileChooser.showOpenDialog(stage);
            writer.println(clientName.getText() + " " + "img" + filePath.getPath());
            writer.flush();
        } catch (NullPointerException e) {
            System.out.println("Image is not Selected!");
        }
    }

    /**
     * Profile image chooser Code
     */
    public void chooseImageButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    /**
     * Profile choose image save Code
     */
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

    /**
     * Send Messages Enter press Code
     */
    public void sendMessageByKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    /**
     * Messages send Code
     */
    public void send() {
        String msg = txtMassage.getText();
        emojiAnchorePane.getChildren().clear();
        emojiAnchorePane.toBack();
        writer.println(CreateNewUserAccountController.username + ": " + msg);
        txtMassage.setText("");
        if (msg.equalsIgnoreCase("BYE") || (msg.equalsIgnoreCase("logout"))) {
            System.exit(0);
        }
    }

    /**
     * Send Button Action Code
     */
    public void btnSendOnAction(MouseEvent mouseEvent) {
        send();
        for (User user : users) {
            System.out.println(user.name);
        }
    }

    /**
     * Male/Female 4to Initialize Code
     */
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
        emojiAnchorePane.toBack();
    }

    /**
     * Select Emoji Pane Load
     */
    public void emojiBtnOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/EmojiController.fxml");
        Parent load = FXMLLoader.load(resource);
        emojiAnchorePane.getChildren().clear();
        emojiAnchorePane.getChildren().add(load);

        emojiAnchorePane.toFront();
    }
}

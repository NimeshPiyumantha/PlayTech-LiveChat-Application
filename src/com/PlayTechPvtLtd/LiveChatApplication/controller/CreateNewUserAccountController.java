package com.PlayTechPvtLtd.LiveChatApplication.controller;

import animatefx.animation.FadeIn;
import com.PlayTechPvtLtd.LiveChatApplication.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author : Nimesh Piyumantha
 * @since : 0.1.0
 **/
public class CreateNewUserAccountController {
    public static String username, password, gender;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();

    public TextField txtUserName;
    public PasswordField txtPassword;
    public TextField txtEmail;
    public TextField txtPhoneNumber;
    public RadioButton male;
    public RadioButton female;
    public ToggleGroup Gender;
    public Label checkEmail;
    public Label success;
    public Label goBack;
    public Button btnLogin;
    public ImageView btnBack;
    public Label nameExists;
    public Label controlRegLabel;
    public Label checkEmail1;
    public Label nameExists1;
    public TextField userName;
    public PasswordField passWord;
    public Button btnSignUp;
    public Label loginNotifier;
    public AnchorPane loginPane;
    public AnchorPane signUpPane;

    public void CreateAccountOnAction(ActionEvent actionEvent) {
        if (!txtUserName.getText().equalsIgnoreCase("")
                && !txtPassword.getText().equalsIgnoreCase("")
                && !txtEmail.getText().equalsIgnoreCase("")
                && !txtPhoneNumber.getText().equalsIgnoreCase("")
                && (male.isSelected() || female.isSelected())) {
            if (checkUser(txtUserName.getText())) {
                if (checkEmail(txtEmail.getText())) {
                    User newUser = new User();
                    newUser.name = txtUserName.getText();
                    newUser.password = txtPassword.getText();
                    newUser.email = txtEmail.getText();
                    newUser.phoneNo = txtPhoneNumber.getText();
                    if (male.isSelected()) {
                        newUser.gender = "Male";
                    } else {
                        newUser.gender = "Female";
                    }
                    users.add(newUser);
                    goBack.setOpacity(1);
                    success.setOpacity(1);
                    makeDefault();
                    if (controlRegLabel.getOpacity() == 1) {
                        controlRegLabel.setOpacity(0);
                    }
                    if (nameExists.getOpacity() == 1) {
                        nameExists.setOpacity(0);
                    }
                } else {
                    checkEmail.setOpacity(1);
                    setOpacity(nameExists, goBack, controlRegLabel, success);
                }
            } else {
                nameExists.setOpacity(1);
                setOpacity(success, goBack, controlRegLabel, checkEmail);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists, checkEmail);
        }
    }

    private void setOpacity(Label a, Label b, Label c, Label d) {
        if (a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1 || d.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
            d.setOpacity(0);
        }
    }

    private void setOpacity(Label controlRegLabel, Label checkEmail, Label nameExists) {
        controlRegLabel.setOpacity(0);
        checkEmail.setOpacity(0);
        nameExists.setOpacity(0);
    }

    private void makeDefault() {
        txtUserName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhoneNumber.setText("");
        male.setSelected(true);
        setOpacity(controlRegLabel, checkEmail, nameExists);
    }

    private boolean checkUser(String username) {
        for (User user : users) {
            if (user.name.equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkEmail(String email) {
        for (User user : users) {
            if (user.email.equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("/com/PlayTechPvtLtd/LiveChatApplication/view/ClientManagemant.fxml"));
            stage.setScene(new Scene(root, 330, 560));
            stage.setTitle(username + "");
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginOnAction(ActionEvent actionEvent) {
        username = userName.getText();
        password = passWord.getText();
        boolean login = false;
        for (User x : users) {
            if (x.name.equalsIgnoreCase(username) && x.password.equalsIgnoreCase(password)) {
                login = true;
                loggedInUser.add(x);
                System.out.println(x.name);
                gender = x.gender;
                break;
            }
        }
        if (login) {
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void backButtonOnAction(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == btnBack) {
            new FadeIn(loginPane).play();
            loginPane.toFront();
        }
        txtUserName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
    }

    public void signUpInOnAction(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(btnSignUp)) {
            new FadeIn(signUpPane).play();
            signUpPane.toFront();
        }
        if (actionEvent.getSource().equals(btnLogin)) {
            new FadeIn(loginPane).play();
            loginPane.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }
}

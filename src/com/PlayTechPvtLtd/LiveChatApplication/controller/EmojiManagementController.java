package com.PlayTechPvtLtd.LiveChatApplication.controller;

import com.PlayTechPvtLtd.LiveChatApplication.main.java.com.madeorsk.emojisfx.EmojisLabel;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * @author : Nimesh Piyumantha
 * @since : 0.1.0
 **/
public class EmojiManagementController implements Initializable {
    public AnchorPane emojiAnchorePane;

    /**
     * Emoji add Vbox label
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox box = new VBox();
        box.prefHeightProperty().bind(emojiAnchorePane.heightProperty());
        box.prefWidthProperty().bind(emojiAnchorePane.widthProperty());
        emojiAnchorePane.getChildren().add(box);

        String testedString = new String("🙃 ☺".getBytes(), StandardCharsets.UTF_8);

        EmojisLabel emojisLabel = new EmojisLabel(testedString);
        emojisLabel.setPrefWidth(320);
        emojisLabel.setFont(Font.font("Aller Light", 32));
        emojisLabel.setTextFill(Color.BLACK);
        emojisLabel.setSelectionFill(Color.BLACK);
        emojisLabel.setSelectedTextFill(Color.WHITE);

        box.getChildren().add(emojisLabel);
    }
}

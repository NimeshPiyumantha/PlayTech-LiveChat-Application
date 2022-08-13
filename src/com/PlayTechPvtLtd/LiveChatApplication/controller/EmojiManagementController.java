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

        String testedString = new String(" \uD83D\uDE00 \uD83D\uDE03 \uD83D\uDC4B \uD83E\uDD1A \uD83D\uDE04 \uD83D\uDE01 \uD83D\uDE06 \uD83D\uDE05 \uD83D\uDE02 \uD83E\uDD23 \uD83D\uDE0A \uD83D\uDE07 \uD83D\uDE42 \uD83D\uDE43 \uD83D\uDE09 \uD83D\uDE0C \uD83D\uDE0D \uD83D\uDE18 \uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE1A \uD83D\uDE0B \uD83D\uDE1B \uD83D\uDE1D \uD83D\uDE1C \uD83E\uDD13 \uD83D\uDE0E \uD83D\uDE0F \uD83D\uDE12 \uD83D\uDE1E \uD83D\uDE14 \uD83D\uDE1F \uD83D\uDE15 \uD83D\uDE41 \uD83D\uDE16 \uD83D\uDE2B \uD83D\uDE29  \uD83D\uDE22 \uD83D\uDE2D \uD83D\uDE24 \uD83D\uDE20 \uD83D\uDE21 \uD83D\uDE33  \uD83D\uDE31 \uD83D\uDE28 \uD83D\uDE30 \uD83D\uDE25  \uD83D\uDE13 \uD83E\uDD17 \uD83E\uDD14  \uD83E\uDD25 \uD83D\uDE36  \uD83D\uDE10 \uD83D\uDE11 \uD83D\uDE2C \uD83D\uDE44 \uD83D\uDE2F \uD83D\uDE26 \uD83D\uDE27 \uD83D\uDE2E  \uD83D\uDE32  \uD83D\uDE34 \uD83E\uDD24 \uD83D\uDE2A \uD83D\uDE35  \uD83E\uDD10  \uD83E\uDD22 \uD83E\uDD27 \uD83D\uDE37 \uD83E\uDD12 \uD83E\uDD15 \uD83E\uDD11 \uD83E\uDD20 \uD83D\uDE08 \uD83D\uDC7F \uD83D\uDC79 \uD83D\uDC7A \uD83E\uDD21 \uD83D\uDCA9 \uD83D\uDC7B \uD83D\uDC80 ☠ \uD83D\uDC7D \uD83D\uDC7E \uD83E\uDD16 \uD83C\uDF83 \uD83D\uDE3A \uD83D\uDE38 \uD83D\uDE39 \uD83D\uDE3B \uD83D\uDE3C \uD83D\uDE3D \uD83D\uDE40 \uD83D\uDE3F \uD83D\uDE3E ".getBytes(), StandardCharsets.UTF_8);

        EmojisLabel emojisLabel = new EmojisLabel(testedString);
        emojisLabel.setPrefWidth(320);
        emojisLabel.setFont(Font.font("Aller Light", 32));
        emojisLabel.setTextFill(Color.BLACK);
        emojisLabel.setSelectionFill(Color.BLACK);
        emojisLabel.setSelectedTextFill(Color.WHITE);

        box.getChildren().add(emojisLabel);
    }
}

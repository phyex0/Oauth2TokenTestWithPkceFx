package com.phyex.oauth2tokentestwithpkcefx.pages;

import com.phyex.oauth2tokentestwithpkcefx.HelloApplication;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PageTwo extends VBox {
    // 1. Declare as a field so other methods can access it
    private final TextArea tokenDisplay;

    public PageTwo(HelloApplication app) {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        Label label = new Label("Authentication Successful");
        label.setFont(new Font("Arial", 30));

        // 2. Using a TextArea is better for long JSON tokens
        tokenDisplay = new TextArea("Waiting for token data...");
        tokenDisplay.setEditable(false);
        tokenDisplay.setWrapText(true);
        tokenDisplay.setMaxWidth(800);
        tokenDisplay.setPrefHeight(400);

        // Custom styling for a "Premium" look
        tokenDisplay.setStyle("-fx-font-family: 'Monospaced'; -fx-control-inner-background: #f4f4f4;");

        Button backBtn = new Button("Sign Out / Back");
        backBtn.setOnAction(e -> app.showPageOne());

        // 3. Add to the layout in a logical order
        getChildren().addAll(label, tokenDisplay, backBtn);
    }

    // 4. This method is called by HelloApplication
    public void setTokenData(String data) {
        tokenDisplay.setText(data);
    }
}
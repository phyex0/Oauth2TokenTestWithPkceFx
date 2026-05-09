package com.phyex.oauth2tokentestwithpkcefx;

import com.phyex.oauth2tokentestwithpkcefx.pages.PageOne;
import com.phyex.oauth2tokentestwithpkcefx.pages.PageTwo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showPageOne();
        primaryStage.setTitle("JavaFX WebView App");
        primaryStage.show();
    }

    public void showPageOne() {
        PageOne page1 = new PageOne(this);
        primaryStage.setScene(new Scene(page1, 1024, 768));
    }

    public void showPageTwo(String tokenData) {
        PageTwo page2 = new PageTwo(this);
        page2.setTokenData(tokenData); // Pass the data here
        primaryStage.setScene(new Scene(page2, 1024, 768));
    }
}

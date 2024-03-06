package com.example.demo1;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        TextField addressBar = new TextField();
        addressBar.setOnAction(e -> loadURL(webEngine, addressBar.getText()));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
            }
        });

        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(e -> {
            if (webEngine.getHistory().getCurrentIndex() < webEngine.getHistory().getEntries().size() - 1) {
                webEngine.getHistory().go(1);
            }
        });

        Button reloadButton = new Button("Reload");
        reloadButton.setOnAction(e -> loadURL(webEngine, webEngine.getLocation())); // Reload the current page

        BorderPane root = new BorderPane();
        root.setTop(addressBar);
        root.setCenter(webView);
        root.setBottom(backButton);
        root.setRight(forwardButton);
        root.setLeft(reloadButton);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Browser");
        primaryStage.setScene(scene);
        primaryStage.show();

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                addressBar.setText(webEngine.getLocation());
            } else if (newValue == Worker.State.FAILED) {
                // Display an error page when the webpage cannot be loaded
                webEngine.loadContent("<h1>Error: Page Unreachable</h1>");
            }
        });
    }

    private void loadURL(WebEngine webEngine, String input) {
        String url = input.trim();

        try {
            URI uri = new URI(url);

            // Check if it's a valid URL or IP address with port
            if (uri.getHost() == null && uri.getPort() == -1) {
                // If it's not a valid URL, assume it's an IP address with port
                url = "http://" + url;
            }
        } catch (URISyntaxException e) {
            // If there's an exception, assume it's not a valid URL and treat it as an IP address with port
            url = "http://" + url;
        }

        webEngine.load(url);
    }
}

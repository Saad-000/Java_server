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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        Button forwardButton = new Button("Send GET Request");
        forwardButton.setOnAction(e -> sendGETRequest(webEngine, addressBar.getText()));

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
            }
        });
    }

    private void loadURL(WebEngine webEngine, String input) {
        String url = input.trim();

        try {
            // Check if it's a valid URL
            new URL(url).toURI();
            webEngine.load(url);
        } catch (Exception e) {
            // If the URL creation fails, treat it as an invalid URL and load "Page Unreachable" content
            webEngine.loadContent("<h1>Error: Page Unreachable</h1>");
        }
    }

    private void sendGETRequest(WebEngine webEngine, String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    webEngine.loadContent(response.toString());
                }
            } else {
                webEngine.loadContent("<h1>Error: HTTP " + responseCode + "</h1>");
            }
        } catch (Exception e) {
            // If an exception occurs during the request, load "Page Unreachable" content
            webEngine.loadContent("<h1>Error: Page Unreachable</h1>");
        }
    }
}

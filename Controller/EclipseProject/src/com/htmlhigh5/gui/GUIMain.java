package com.htmlhigh5.gui;

import java.net.URL;

import com.htmlhigh5.debug.Debug;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {
	@Override
	public void start(Stage stage) throws InterruptedException {
		GridPane gridPane = new GridPane();

        stage.setTitle("Web Map");
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.show();
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        URL url = getClass().getResource("googlemap.html");
        webEngine.load(url.toExternalForm());

        webView.maxWidthProperty().bind(gridPane.widthProperty().divide(4));
        webView.maxHeightProperty().bind(gridPane.heightProperty().divide(3));
        
        Media media = new Media("http://99.9.152.182:8554");
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        MediaView mediaView = new MediaView(mediaPlayer);
        
        gridPane.add(webView, 0, 0);
        gridPane.add(mediaView, 0, 1);
        mediaPlayer.play();
	}
	
	public static void startGUI(){
		launch();
	}
}

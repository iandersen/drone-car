package com.htmlhigh5.gui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.htmlhigh5.debug.Debug;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {
	@Override
	public void start(Stage stage) throws InterruptedException, URISyntaxException {
		Pane pane = new Pane();
		BorderPane borderPane = new BorderPane();

        stage.setTitle("Web Map");
        Scene scene = new Scene(pane, 1500, 800);
        stage.setScene(scene);
        stage.show();
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        URL url = getClass().getResource("googlemap.html");
        webEngine.load(url.toExternalForm());

        webView.maxWidthProperty().bind(pane.widthProperty().divide(4));
        webView.maxHeightProperty().bind(pane.heightProperty().divide(3));
        
        Media media = new Media("file:///D:/RC/Controller/EclipseProject/src/com/htmlhigh5/gui/small.mp4");
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.fitWidthProperty().bind(scene.widthProperty());
        
        GridPane controls = new GridPane();
        
        controls.add(new Button("Horn"), 0, 0);
        controls.add(new Button("Forward"), 1, 0);
        controls.add(new Button("Lights"), 2, 0);
        controls.add(new Button("Left"), 0, 1);
        controls.add(new Button("Backward"), 1, 1);
        controls.add(new Button("Right"), 2, 1);

        controls.getStylesheets().add("file:///D:/RC/Controller/EclipseProject/src/com/htmlhigh5/gui/controlStyle.css");
        controls.setHgap(scene.getWidth() / 30);
        controls.setVgap(scene.getHeight() / 30);

        pane.getChildren().add(mediaView);
        pane.getChildren().add(borderPane);
        
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        
        borderPane.setBottom(controls);
        borderPane.setLeft(webView);
        borderPane.setPadding(new Insets(10, 20, 10, 20));
        
        mediaPlayer.play();
	}
	
	public static void startGUI(){
		launch();
	}
}

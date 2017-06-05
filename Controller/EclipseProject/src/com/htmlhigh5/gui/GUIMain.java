package com.htmlhigh5.gui;

import java.net.URL;

import com.htmlhigh5.debug.Debug;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {

//D:/RC/Controller/EclipseProject/src/com/htmlhigh5/gui/googlemaps.html
	 @Override
	 public void start(Stage stage) throws InterruptedException {
        // create web engine and view
		GridPane gridPane = new GridPane();
		Pane pane = new Pane();
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        URL url = getClass().getResource("googlemap.html");
        webEngine.load(url.toExternalForm());
        pane.getChildren().add(webView);
        gridPane.add(pane, 0, 0);
        // create scene
        stage.setTitle("Web Map");
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        System.out.println("test");
        stage.show();
	 }
	
	public static void startGUI(){
		launch();
	}
}

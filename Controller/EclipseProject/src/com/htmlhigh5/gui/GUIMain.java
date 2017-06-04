package com.htmlhigh5.gui;

import com.htmlhigh5.debug.Debug;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {

//D:/RC/Controller/EclipseProject/src/com/htmlhigh5/gui/googlemaps.html
	 @Override
	 public void start(Stage stage) {
        // create web engine and view
		Pane temp = new Pane();
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/googlemaps.html").toExternalForm());
        temp.getChildren().add(webView);
        // create scene
        stage.setTitle("Web Map");
        Scene scene = new Scene(temp,1000,700);
        stage.setScene(scene);
        stage.show();
	 }
	
	public static void startGUI(){
		launch();
	}
}

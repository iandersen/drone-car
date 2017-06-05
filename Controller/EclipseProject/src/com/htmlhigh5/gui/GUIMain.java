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
		//BorderPane borderPane = new BorderPane();
		Pane pane = new Pane();

        stage.setTitle("Web Map");
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        URL url = getClass().getResource("googlemap.html");
        webEngine.load(url.toExternalForm());

        webView.maxWidthProperty().bind(pane.widthProperty().divide(4));
        webView.maxHeightProperty().bind(pane.heightProperty().divide(3));
        
        pane.getChildren().add(webView);
	 }
	
	public static void startGUI(){
		launch();
	}
}

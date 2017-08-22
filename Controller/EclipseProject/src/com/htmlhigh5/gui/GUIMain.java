package com.htmlhigh5.gui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.vehicle.BadGPIOValueException;
import com.htmlhigh5.vehicle.GPIOComponent;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {
	private ArrayList<GPIOComponent> devices = Main.vehicle.getDevices();
	
	@Override
	public void start(Stage stage) throws InterruptedException, URISyntaxException {
		Pane pane = new Pane();
		StackPane stackPane = new StackPane();
		ArrayList<String> keysDown = new ArrayList<String>();

		// Add a keyboard listener
		pane.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				String text = ke.getText();
				Main.userControl.keyPressed(text);
				if (!keysDown.contains(text))
					keysDown.add(text);
			}
		});

		// Listen for releases
		pane.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (keysDown.contains(ke.getText()))
					keysDown.remove(ke.getText());
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (Main.vehicle.isRunning()) {
						Main.userControl.keysDown(keysDown);
						Thread.sleep(30);
					}
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();

		stage.setTitle("Web Map");
		Scene scene = new Scene(pane, 1500, 800);
		stage.setScene(scene);
	    stage.setResizable(false);
		stage.show();
		
		//We need this so that if the user is pressing keys and then minimize the window, they keys will count as released
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean previous,
			        Boolean current) {
				if (!current) {
					keysDown.clear();
				}
			}
		});
		
		//TO DO - Use an overlaying webview to use bootstrap with javafx - https://stackoverflow.com/questions/21268062/bootstrap-with-javafx
		
		/*ScrollBar speedScroller = new ScrollBar();
		speedScroller.setMax(devices.get(0).config.getInt("MAX_PW"));
		System.out.println(devices.get(0).config.getInt("MAX_PW"));
		speedScroller.setMin(devices.get(0).config.getInt("MIN_PW"));
		speedScroller.setValue(speedScroller.getMax());
		
		speedScroller.valueProperty().addListener(ov -> {
			devices.get(0).config.setProperty("MAX_PW", (int)speedScroller.getValue());
			System.out.println(devices.get(0).config.getProperty("MAX_PW"));
	    });
		
		ToolBar settings = new ToolBar(speedScroller);
		settings.setStyle("-fx-background-color: rgba(0,0,0,.12)");*/

		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();

		URL url = getClass().getResource("controls.html");
		webEngine.load(url.toExternalForm());

		webView.maxWidthProperty().bind(pane.widthProperty());
		webView.maxHeightProperty().bind(pane.heightProperty());
		
		WebView videoStream = new WebView();
		WebEngine videoEngine = videoStream.getEngine();
		
		//URL streamUrl = new URL("https://wowza.jwplayer.com/live/jelly.stream/playlist.m3u8");
		try {
			videoEngine.load((new URL("http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8").toExternalForm()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*Media media = new Media(
		        "file:///C:/Users/Ian/Documents/GitHub/drone-car/Controller/EclipseProject/src/com/htmlhigh5/gui/small.mp4");
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);

		MediaView mediaView = new MediaView(mediaPlayer);
		mediaView.fitWidthProperty().bind(scene.widthProperty());*/

		//pane.getChildren().add(mediaView);

		stackPane.prefWidthProperty().bind(scene.widthProperty());
		stackPane.prefHeightProperty().bind(scene.heightProperty());

		stackPane.getChildren().addAll(webView);
		//borderPane.setRight(settings);
		
		pane.getChildren().add(stackPane);

		//mediaPlayer.play();
	}

	public static void startGUI() {
		launch();
	}
}

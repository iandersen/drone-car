package com.htmlhigh5.gui;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.vehicle.GPIOComponent;
import com.htmlhigh5.vehicle.GPIOType;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMain extends Application {
	private ArrayList<GPIOComponent> devices = Main.vehicle.getDevices();
	private HashMap<GPIOComponent, ScrollBar> speedControllers = new HashMap<GPIOComponent, ScrollBar>();
	private HashMap<GPIOComponent, CheckBox> toggleControllers = new HashMap<GPIOComponent, CheckBox>();
	private Stage stage;
	private Pane toolbarPane;
	GoogleMapView mapView;
	MapPane mapPane;
	GoogleMap map;

	private void forceUpdate() {
		for (GPIOComponent device : this.devices) {
			if (device.getType() == GPIOType.TOGGLE) {
				CheckBox cb = toggleControllers.get(device);
			} else {
				ScrollBar sb = speedControllers.get(device);
			}
		}
	}

	@Override
	public void start(Stage stage) throws InterruptedException, URISyntaxException {
		this.stage = stage;
		Pane pane = new Pane();
		BorderPane borderPane = new BorderPane();
		ArrayList<String> keysDown = new ArrayList<String>();
		mapView = new GoogleMapView();
		mapView.setPrefHeight(400);
		mapView.setPrefWidth(400);
		mapPane = new MapPane(map, mapView);
		Main.vehicle.latLonProperty.addListener(ov -> {
			Platform.runLater(() -> {
				mapPane.lat = Main.vehicle.getLat();
				mapPane.lon = Main.vehicle.getLon();
				mapPane.updateMap();
			});
		});
		mapView.addMapInializedListener(mapPane);

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
		GUIMain self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (Main.vehicle.isRunning()) {
						Main.userControl.keysDown(keysDown);
						self.forceUpdate();
						Thread.sleep(30);
					}
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();

		stage.setTitle("Car Controller");
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.setResizable(true);
		stage.show();

		// We need this so that if the user is pressing keys and then minimize
		// the window, they keys will count as released
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean previous, Boolean current) {
				if (!current) {
					keysDown.clear();
				}
			}
		});

		// TO DO - Use an overlaying webview to use bootstrap with javafx -
		// https://stackoverflow.com/questions/21268062/bootstrap-with-javafx
		//
		VBox allToolbars = new VBox();
		this.addAllToolbarsToPane(allToolbars);
		this.toolbarPane = allToolbars;

		// WebView webView = new WebView();
		// webView.maxWidth(400);
		// webView.maxHeight(400);
		// webView.setMaxHeight(400);
		// webView.setMaxWidth(400);
		// WebEngine webEngine = webView.getEngine();

		// URL url = getClass().getResource("controls.html");
		// webEngine.load(url.toExternalForm());
		//
		// WebView videoStream = new WebView();
		// WebEngine videoEngine = videoStream.getEngine();

		// URL streamUrl = new
		// URL("https://wowza.jwplayer.com/live/jelly.stream/playlist.m3u8");
		// try {
		// videoEngine.load((new
		// URL("http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8").toExternalForm()));
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		borderPane.prefWidthProperty().bind(scene.widthProperty());
		borderPane.prefHeightProperty().bind(scene.heightProperty());

		borderPane.setTop(mapView);
		borderPane.setLeft(new VehicleStatusPanel());
		borderPane.setCenter(allToolbars);
		borderPane.setStyle("-fx-background-color: #333");

		pane.getChildren().add(borderPane);

		// mediaPlayer.play();
	}

	private void addAllToolbarsToPane(Pane pane) {
		for (GPIOComponent device : this.devices) {
			Node switcher = null;
			ToolBar settings;
			ComponentStatusPanel panel = new ComponentStatusPanel(device);
			pane.getChildren().addAll(panel.getChildren());
		}
	}

	public static void startGUI() {
		launch();
	}
}

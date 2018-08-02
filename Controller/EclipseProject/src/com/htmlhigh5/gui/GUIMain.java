package com.htmlhigh5.gui;

import java.net.URISyntaxException;
import java.util.ArrayList;
import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.vehicle.GPIOComponent;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUIMain extends Application {
	private ArrayList<GPIOComponent> devices = Main.vehicle.getDevices();
	GoogleMapView mapView;
	MapPane mapPane;
	GoogleMap map;

	@Override
	public void start(Stage stage) throws InterruptedException, URISyntaxException {
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
		Button resetMapButton = new Button("Reset Map");
		resetMapButton.setOnAction(ev -> {
			mapPane.clearPath();
		});

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
					while (true) {
						if(Main.vehicle.isRunning()){
							Main.userControl.keysDown(keysDown);
						}
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

		VBox allToolbars = new VBox();
		this.addAllToolbarsToPane(allToolbars);
		VBox console = new VBox();
		ScrollPane consoleContainer = new ScrollPane(console);
		consoleContainer.setPrefHeight(150);
		console.setStyle("-fx-background-color: #333");
		consoleContainer.setStyle("-fx-background: #333");
		consoleContainer.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		Text t = new Text(Debug.getLogText());
		t.setFill(Color.WHITE);
		console.getChildren().add(t);
		Debug.logText.addListener(ev -> {
			Platform.runLater(() -> {
				Text txt = new Text(Debug.getLogText());
				txt.setFill(Color.WHITE);
				console.getChildren().add(txt);
				consoleContainer.setVvalue(1.0);
			});
		});


		borderPane.prefWidthProperty().bind(scene.widthProperty());
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		mapView.setStyle("-fx-background: #333");
		borderPane.setTop(new VBox(mapView, resetMapButton));
		borderPane.setLeft(new VehicleStatusPanel());
		borderPane.setRight(allToolbars);
		borderPane.setBottom(consoleContainer);
		borderPane.setStyle("-fx-background-color: #333");

		pane.getChildren().add(borderPane);

		// mediaPlayer.play();
	}

	private void addAllToolbarsToPane(Pane pane) {
		for (GPIOComponent device : this.devices) {
			ComponentStatusPanel panel = new ComponentStatusPanel(device);
			pane.getChildren().addAll(panel.getChildren());
		}
	}

	public static void startGUI() {
		launch();
	}
}

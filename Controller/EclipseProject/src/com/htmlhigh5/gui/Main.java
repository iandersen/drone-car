package com.htmlhigh5.gui;

import com.htmlhigh5.debug.Debug;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch();
		Debug.init();
	}

	@Override
	public void start(Stage primaryStage) {
		GUI gui = new GUI();

		Scene scene = new Scene(gui.getMainPane(), 800, 600);
		primaryStage.setTitle("Assignment 16.17"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
}

package com.htmlhigh5.gui;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.GPIOComponent;
import com.htmlhigh5.vehicle.Vehicle;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class VehicleStatusPanel extends VBox {
	public GPIOComponent device;
	public ToolBar toolbar;
	
	public VehicleStatusPanel(){
		this.setPrefWidth(300);
		Vehicle vehicle = Main.vehicle;
		Text speedText = new Text("Speed (MPH): " + prettify(vehicle.getMPH()));
		Text altitudeText = new Text("Altitude (M): " + prettify(vehicle.getAltitude()));
		Text maxSpeedText = new Text("Top Speed: " + prettify(vehicle.topSpeed));
		Text highestAltText = new Text("Highest Altitude: " + prettify(vehicle.highestAltitude));
		Text lowestAltText = new Text("Lowest Altitude: " + prettify(vehicle.lowestAltitude));
		Text latencyText = new Text("Latency: " + Main.receiver.getLatency()+"ms");
		Button resetButton = new Button("Reset");
		speedText.setFill(Color.WHITE);
		altitudeText.setFill(Color.WHITE);
		maxSpeedText.setFill(Color.WHITE);
		highestAltText.setFill(Color.WHITE);
		lowestAltText.setFill(Color.WHITE);
		latencyText.setFill(Color.LIGHTSALMON);
		this.getChildren().addAll(speedText, altitudeText, maxSpeedText,highestAltText,lowestAltText,latencyText,resetButton);
		vehicle.mphProperty.addListener(ov -> {
			speedText.setText("Speed (MPH): " + prettify(vehicle.getMPH()));
			maxSpeedText.setText("Top Speed: " + prettify(vehicle.topSpeed));
		});
		vehicle.altitudeProperty.addListener(ov->{
			altitudeText.setText("Altitude (M): " + prettify(vehicle.getAltitude()));
			highestAltText.setText("Highest Altitude: " + prettify(vehicle.highestAltitude));
			lowestAltText.setText("Lowest Altitude: " + prettify(vehicle.lowestAltitude));
		});
		Main.receiver.latencyProperty.addListener(ev -> {
			latencyText.setText("Latency: " + Main.receiver.getLatency()+"ms");
		});
		resetButton.setOnAction(ev -> {
			vehicle.resetStats();
		});
	}
	
	private String prettify(Double number){
		number = Math.round(number * 100)/100.0;
		return ""+number;
	}
}

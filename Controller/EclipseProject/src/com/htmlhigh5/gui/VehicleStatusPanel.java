package com.htmlhigh5.gui;

import com.htmlhigh5.Main;
import com.htmlhigh5.network.CustomPacket;
import com.htmlhigh5.vehicle.GPIOComponent;
import com.htmlhigh5.vehicle.Vehicle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
	    NumberAxis xAxis = new NumberAxis();
	    xAxis.setAutoRanging(false);
	    @SuppressWarnings("unchecked")
		LineChart<Number, Number> chart = new LineChart<>(xAxis, new NumberAxis(), FXCollections.observableArrayList(series));
		TextField speechBox = new TextField();
		speechBox.setPromptText("Say something through the car");
		speechBox.focusedProperty().addListener(ev->{
			if(speechBox.isFocused())
				GUIMain.instance.focus(this);
			else if(GUIMain.focusedNode.getId() == this.getId())
				GUIMain.instance.focus(null);
		});
		Button speakButton = new Button("Send message");
		speakButton.setOnAction(ev -> {
			String message = speechBox.getText();
			Main.transmitter.sendCustomPacket(new CustomPacket("speech|" + message));
			speechBox.clear();
		});
		Button resetButton = new Button("Reset");
		speedText.setFill(Color.WHITE);
		altitudeText.setFill(Color.WHITE);
		maxSpeedText.setFill(Color.WHITE);
		highestAltText.setFill(Color.WHITE);
		lowestAltText.setFill(Color.WHITE);
		latencyText.setFill(Color.LIGHTSALMON);
		this.getChildren().addAll(speedText, altitudeText, maxSpeedText,highestAltText,lowestAltText,chart,latencyText,resetButton,speechBox,speakButton);
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
			Platform.runLater(() -> {
				if(series.getData().size() > 20)
					series.getData().remove(0);
				if(series.getData().size() > 0){
					xAxis.setLowerBound(-10+Double.parseDouble(""+series.getData().get(0).getXValue()));
					xAxis.setUpperBound(10+Double.parseDouble(""+series.getData().get(series.getData().size()-1).getXValue()));
				}
				series.getData().add(new XYChart.Data<>(Main.receiver.connectionTime, Main.receiver.getLatency()));
			});
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

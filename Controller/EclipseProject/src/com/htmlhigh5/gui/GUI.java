package com.htmlhigh5.gui;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
//import javafx.scene.layout.Rectangle;

public class GUI{
	private GridPane mainPane = new GridPane();
	
	public GUI(){
		paint();
	}
	
	public GridPane getMainPane(){
		return mainPane;
	}
	
	public void paint(){
		Pane mapPane = new Pane();
		Rectangle rect = new Rectangle(200, 150, Color.GREEN);
		mapPane.getChildren().add(rect);
		mainPane.add(mapPane, 1, 1);
	}
	
	public void makeMap(){
		//timed loop
		updateMap();
	}
	
	public void updateMap(){
		
	}
	
	public void startCameraFeed(){
		
	}
	
	public void takeScreenshot(){
		
	}
}

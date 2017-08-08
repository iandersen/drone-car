package com.htmlhigh5.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.lang.Thread;

public class Assignment18_35 extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {       
    HPane pane = new HPane(); 
    TextField tfOrder = new TextField(); 
    tfOrder.setOnAction(
      e -> pane.setDepth(Integer.parseInt(tfOrder.getText())));
    tfOrder.setPrefColumnCount(4);
    tfOrder.setAlignment(Pos.BOTTOM_RIGHT);

    // Pane to hold label, text field, and a button 
    HBox hBox = new HBox(10);
    hBox.getChildren().addAll(new Label("Enter an order: "), tfOrder);
    hBox.setAlignment(Pos.CENTER);
    
    BorderPane borderPane = new BorderPane();
    borderPane.setCenter(pane);
    borderPane.setBottom(hBox);
        
    // Create a scene and place it in the stage
    Scene scene = new Scene(borderPane, 200, 210);
    primaryStage.setTitle("Assignment 18.35"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    scene.widthProperty().addListener(ov -> pane.paint());
    scene.heightProperty().addListener(ov -> pane.paint());
  }

  /** Pane for displaying triangles */
  static class HPane extends Pane {
    private int depth = 0;

    public void setDepth(int depth) {
      this.depth = depth;
      paint();
    }

    public void paint() {
      getChildren().clear();
      
      paintH(depth, getWidth() / 2, getHeight() / 2, getWidth() / 4, getHeight() / 4);
    }

    public void paintH(int depth, double x, double y,
        double width, double height) {
        if (depth >= 0) {

          getChildren().add(new Line(x + width, y + height, x + width, y - height));
          getChildren().add(new Line(x - width, y + height, x - width, y - height));
          getChildren().add(new Line(x + width, y, x - width, y));

          paintH(depth - 1, x - width, y + height, width / 2, height / 2);
          paintH(depth - 1, x - width, y - height, width / 2, height / 2);
          paintH(depth - 1, x + width, y + height, width / 2, height / 2);
          paintH(depth - 1, x + width, y - height, width / 2, height / 2);
        }
    }
  }
  
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
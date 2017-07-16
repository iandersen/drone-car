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

//import com.lynden.gmapsfx.GoogleMapView;
//import com.lynden.gmapsfx.MapComponentInitializedListener;
//import com.lynden.gmapsfx.javascript.object.DirectionsPane;
//import com.lynden.gmapsfx.javascript.object.GoogleMap;
//import com.lynden.gmapsfx.javascript.object.InfoWindow;
//import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
//import com.lynden.gmapsfx.javascript.object.LatLong;
//import com.lynden.gmapsfx.javascript.object.MapOptions;
//import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
//import com.lynden.gmapsfx.javascript.object.Marker;
//import com.lynden.gmapsfx.javascript.object.MarkerOptions;
//import com.lynden.gmapsfx.service.directions.DirectionsService;
//import com.teamdev.jxmaps.javafx.MapView;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class GUI extends Exception{
    private GridPane mainPane = new GridPane();
//    private MapView mapView = new MapView();
    
    public GUI(){
        paint();
    }
    
    public GridPane getMainPane(){
        return mainPane;
    }
    
    public void paint(){
        //mapInitialized();
        Pane mapPane = new Pane();
        Rectangle rect = new Rectangle(200, 150, Color.GREEN);
//        mapPane.getChildren().add(mapView);
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
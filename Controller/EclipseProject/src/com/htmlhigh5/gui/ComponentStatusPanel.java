package com.htmlhigh5.gui;

import com.htmlhigh5.vehicle.GPIOComponent;
import com.htmlhigh5.vehicle.GPIOType;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ComponentStatusPanel extends Pane {
	public GPIOComponent device;
	public ToolBar toolbar;
	
	public ComponentStatusPanel(GPIOComponent d){
		this.device = d;
		if(device.getType() == GPIOType.TOGGLE){
			CheckBox checkBox = new CheckBox("Activated");
			checkBox.setSelected(device.isOn());
			checkBox.selectedProperty().addListener(ov -> {
				device.toggle();
			});
			this.getChildren().add(new ToolBar(new Text(device.getName()),checkBox));
		}else{
			ScrollBar speedScroller = new ScrollBar();
			
			speedScroller.setMax(device.getMax());
			speedScroller.setMin(device.getMin());
			speedScroller.setValue(device.getValue());
			
			speedScroller.valueProperty().addListener(ov -> {
				device.setRestValue((int)speedScroller.getValue());
		    });
			Text valueText = new Text(""+device.getValue());
			valueText.setStyle("-fx-font-weight: bold");
			device.valueProperty.addListener(ov -> {
				valueText.setText(""+device.getValue());
			});
			this.getChildren().add(new ToolBar(new Text(device.getName()), valueText,
					new Text("Min: " + device.getMin()),
					new Text("Max: " + device.getMax()),
					speedScroller));
		}
	}

	@Override
	protected boolean impl_computeContains(double arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BaseBounds impl_computeGeomBounds(BaseBounds arg0, BaseTransform arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object impl_processMXNode(MXNodeAlgorithm arg0, MXNodeAlgorithmContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}

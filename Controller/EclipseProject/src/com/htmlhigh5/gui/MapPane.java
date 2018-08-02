package com.htmlhigh5.gui;

import java.util.ArrayList;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.Vehicle;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.lynden.gmapsfx.shapes.MapShapeOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

public class MapPane implements MapComponentInitializedListener {

	GoogleMapView mapView;
	GoogleMap map;
	public double lat = 0;
	public double lon = 0;
	public double lastLat = 0;
	public double lastLon = 0;
	private CircleOptions locationMarkerOptions; 
	private Circle locationMarker;
	ArrayList<MapShape> allShapes = new ArrayList<MapShape>();

	public MapPane(GoogleMap map, GoogleMapView mapView) {
		this.map = map;
		this.mapView = mapView;
		this.lat = Main.vehicle.getLat();
		this.lon = Main.vehicle.getLon();
	}

	public void updateMap() {
		PolylineOptions lineOptions = new PolylineOptions();
		if (this.lastLon != 0) {
			MVCArray lastTwoPoints = new MVCArray();
			lastTwoPoints.push(new LatLong(this.lastLat, this.lastLon));
			lastTwoPoints.push(new LatLong(this.lat, this.lon));
			lineOptions.path(lastTwoPoints).strokeColor("#FF0000").visible(true).strokeWeight(2).draggable(false);
			Polyline line = new Polyline(lineOptions);
			allShapes.add(line);
			this.map.addMapShape(line);
		}
		this.lastLat = this.lat;
		this.lastLon = this.lon;
		locationMarker.setCenter(new LatLong(this.lat, this.lon));
		this.map.setCenter(new LatLong(this.lat, this.lon));
	}
	
	public void clearPath(){
		for(MapShape s : allShapes){
			map.removeMapShape(s);
		}
		allShapes = new ArrayList<MapShape>();
	}

	@Override
	public void mapInitialized() {
		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(new LatLong(34.438, -118.553)).mapType(MapTypeIdEnum.ROADMAP).overviewMapControl(false)
				.panControl(false).rotateControl(false).scaleControl(false).streetViewControl(false).zoomControl(false)
				.zoom(17).styleString("[{'featureType':'all','elementType':'labels.text.fill','stylers':[{'color':'#ffffff'}]},{'featureType':'all','elementType':'labels.text.stroke','stylers':[{'color':'#000000'},{'lightness':13}]},{'featureType':'administrative','elementType':'geometry.fill','stylers':[{'color':'#000000'}]},{'featureType':'administrative','elementType':'geometry.stroke','stylers':[{'color':'#144b53'},{'lightness':14},{'weight':1.4}]},{'featureType':'landscape','elementType':'all','stylers':[{'color':'#08304b'}]},{'featureType':'poi','elementType':'geometry','stylers':[{'color':'#0c4152'},{'lightness':5}]},{'featureType':'road.highway','elementType':'geometry.fill','stylers':[{'color':'#000000'}]},{'featureType':'road.highway','elementType':'geometry.stroke','stylers':[{'color':'#0b434f'},{'lightness':25}]},{'featureType':'road.arterial','elementType':'geometry.fill','stylers':[{'color':'#000000'}]},{'featureType':'road.arterial','elementType':'geometry.stroke','stylers':[{'color':'#0b3d51'},{'lightness':16}]},{'featureType':'road.local','elementType':'geometry','stylers':[{'color':'#000000'}]},{'featureType':'transit','elementType':'all','stylers':[{'color':'#146474'}]},{'featureType':'water','elementType':'all','stylers':[{'color':'#021019'}]}]");
		this.map = mapView.createMap(mapOptions);
		locationMarkerOptions = new CircleOptions().visible(true).fillColor("#ff0000").radius(2).draggable(false);
		locationMarker = new Circle(locationMarkerOptions);
		this.map.addMapShape(locationMarker);
	}

}

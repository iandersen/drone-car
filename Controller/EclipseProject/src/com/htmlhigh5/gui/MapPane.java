package com.htmlhigh5.gui;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.Vehicle;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
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

	public MapPane(GoogleMap map, GoogleMapView mapView) {
		this.map = map;
		this.mapView = mapView;
		this.lat = Main.vehicle.getLat();
		this.lon = Main.vehicle.getLon();
	}

	public void updateMap() {
		CircleOptions shapeOptions = new CircleOptions();
		PolylineOptions lineOptions = new PolylineOptions();
		if (this.lastLon != 0) {
			MVCArray lastTwoPoints = new MVCArray();
			lastTwoPoints.push(new LatLong(this.lastLat, this.lastLon));
			lastTwoPoints.push(new LatLong(this.lat, this.lon));
			lineOptions.path(lastTwoPoints).strokeColor("#FF0000").visible(true).strokeWeight(2);
			this.map.addMapShape(new Polyline(lineOptions));
		}
		this.lastLat = this.lat;
		this.lastLon = this.lon;
		shapeOptions.center(new LatLong(this.lat, this.lon)).visible(true).fillColor("#ff0000").radius(10);
		this.map.addMapShape(new Circle(shapeOptions));
		this.map.setCenter(new LatLong(this.lat, this.lon));
	}

	@Override
	public void mapInitialized() {
		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(new LatLong(34.438855, -118.553971)).mapType(MapTypeIdEnum.ROADMAP).overviewMapControl(false)
				.panControl(false).rotateControl(false).scaleControl(false).streetViewControl(false).zoomControl(false)
				.zoom(12);
		this.map = mapView.createMap(mapOptions);
	}

}

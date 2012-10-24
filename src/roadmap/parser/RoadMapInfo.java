package roadmap.parser;

import java.util.HashMap;

import roadmap.Intersection;
import roadmap.Road;


public class RoadMapInfo {

	// Configuration variables
	private int mapWidth;
	private int mapHeight;
	private int defaultSpeed;
	private int defaultRoadCapacity;
	private int defaultNumberCars;
	private HashMap<Integer, Road> roads;
	private HashMap<Integer, Intersection> intersections;
	
	public RoadMapInfo() {
		roads = new HashMap<Integer, Road>();
		intersections = new HashMap<Integer, Intersection>();
	}
	
	public HashMap<Integer, Road> getRoads() {
		return roads;
	}

	public void setRoads(HashMap<Integer, Road> roads) {
		this.roads = roads;
	}

	public HashMap<Integer, Intersection> getIntersections() {
		return intersections;
	}

	public void setIntersections(HashMap<Integer, Intersection> intersections) {
		this.intersections = intersections;
	}

	public int getMapWidth() {
		return mapWidth;
	}
	
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	public int getDefaultSpeed() {
		return defaultSpeed;
	}
	
	public void setDefaultSpeed(int defaultSpeed) {
		this.defaultSpeed = defaultSpeed;
	}
	
	public int getDefaultRoadCapacity() {
		return defaultRoadCapacity;
	}
	
	public void setDefaultRoadCapacity(int defaultRoadCapacity) {
		this.defaultRoadCapacity = defaultRoadCapacity;
	}
	
	public int getDefaultNumberCars() {
		return defaultNumberCars;
	}
	
	public void setDefaultNumberCars(int defaultNumberCars) {
		this.defaultNumberCars = defaultNumberCars;
	}
		
}
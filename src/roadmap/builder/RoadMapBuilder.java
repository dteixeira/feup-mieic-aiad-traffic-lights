package roadmap.builder;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import roadmap.Connection;
import roadmap.Coordinates;
import roadmap.Intersection;
import roadmap.Orientation;
import roadmap.Road;
import roadmap.TrafficLight;
import roadmap.parser.RoadMapInfo;

public class RoadMapBuilder {
	
	private static int CELL_SIZE = 50;
	private static String MAP_FLOOR_OUTER_FILE = "textures/tree.png";
	private static String MAP_FLOOR_INNER_FILE = "textures/house.png";
	private static String MAP_INTERSECTION_FILE = "textures/intersection.png";
	private static String MAP_ROAD_SINGLE_VERTICAL_FILE = "textures/dashed-line-vert.png";
	private static String MAP_ROAD_SINGLE_HORIZONTAL_FILE = "textures/dashed-line-horiz.png";
	private static String MAP_ROAD_DOUBLE_VERTICAL_FILE = "textures/full-line-vert.png";
	private static String MAP_ROAD_DOUBLE_HORIZONTAL_FILE = "textures/full-line-horiz.png";

	public static RoadMapInfo buildAdvancedInfo(RoadMapInfo roadMapInfo) {
		try {
			connectionBuilder(roadMapInfo);
			connectionChecker(roadMapInfo);
			graphicBuilder(roadMapInfo);
			File outputfile = new File("saved.png");
		    ImageIO.write(roadMapInfo.getBackgroundImage(), "png", outputfile);
			return roadMapInfo;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void connectionChecker(RoadMapInfo roadMapInfo) throws Exception {
		for(Entry<Integer, Intersection> entry : roadMapInfo.getIntersections().entrySet()) {
			if(entry.getValue().getInboundConnections().size() <= 0)
				throw new Exception();
		}
	}
	
	private static void graphicBuilder(RoadMapInfo roadMapInfo) throws Exception {
		BufferedImage background = new BufferedImage(CELL_SIZE * (3 + 3 * (roadMapInfo.getMapWidth() - 1)), 
				CELL_SIZE * (3 + 3 *(roadMapInfo.getMapHeight() - 1)),
				BufferedImage.TYPE_INT_RGB);
		
		// Draw image
		paintFloor(roadMapInfo, background);
		paintIntersections(roadMapInfo, background);
		paintRoads(roadMapInfo, background);
		roadMapInfo.setBackgroundImage(background);
	}
	
	private static void paintFloor(RoadMapInfo roadMapInfo, BufferedImage background) throws Exception {
		BufferedImage outerFloor = ImageIO.read(new File(MAP_FLOOR_OUTER_FILE));
		BufferedImage innerFloor = ImageIO.read(new File(MAP_FLOOR_INNER_FILE));
		Graphics2D graphics = background.createGraphics();
		for(int y = 0; y < background.getHeight(); y += CELL_SIZE) {
			for(int x = 0; x < background.getWidth(); x += CELL_SIZE) {
				if(x == 0 || y == 0 || x >= background.getWidth() - CELL_SIZE || y >= background.getHeight() - CELL_SIZE) 
					graphics.drawImage(outerFloor, x, y, null);
				else
					graphics.drawImage(innerFloor, x, y, null);
			}
		}
	}
	
	private static void paintIntersections(RoadMapInfo roadMapInfo, BufferedImage background) throws Exception {
		BufferedImage intersection = ImageIO.read(new File(MAP_INTERSECTION_FILE));
		Graphics2D graphics = background.createGraphics();
		for(Entry<Integer, Intersection> entry : roadMapInfo.getIntersections().entrySet()) {
			Coordinates coord = convertCoordinates(entry.getValue().getCoordinates());
			graphics.drawImage(intersection, coord.getxCoord(), coord.getyCoord(), null);
			int[] xPoints = {coord.getxCoord(), coord.getxCoord(), coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE};
			int[] yPoints = {coord.getyCoord(), coord.getyCoord() + CELL_SIZE, coord.getyCoord() + CELL_SIZE, coord.getyCoord()};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			entry.getValue().setHitBox(hitBox);
		}
	}
	
	private static void buildTrafficLightPosition(Connection connect) {
		// TODO
	}
	
	private static void paintRoads(RoadMapInfo roadMapInfo, BufferedImage background) throws Exception {
		BufferedImage single_horizontal = ImageIO.read(new File(MAP_ROAD_SINGLE_HORIZONTAL_FILE));
		BufferedImage double_horizontal = ImageIO.read(new File(MAP_ROAD_DOUBLE_HORIZONTAL_FILE));
		BufferedImage single_vertical = ImageIO.read(new File(MAP_ROAD_SINGLE_VERTICAL_FILE));
		BufferedImage double_vertical = ImageIO.read(new File(MAP_ROAD_DOUBLE_VERTICAL_FILE));
		Graphics2D graphics = background.createGraphics();
		for(Entry<Integer, Intersection> entry : roadMapInfo.getIntersections().entrySet()) {
			Coordinates coord = convertCoordinates(entry.getValue().getCoordinates());
			for(Connection connect : entry.getValue().getInboundConnections()) {
				
				// Determines the traffic light drawing position
				buildTrafficLightPosition(connect);
				
				// Paints each road
				Road road = connect.getConnectedRoad();
				Coordinates source = convertCoordinates(road.getStartIntersection().getCoordinates());
				if(road.getRoadOrientation() == Orientation.LEFT || road.getRoadOrientation() == Orientation.RIGHT) {
					if(connect.getConnectedRoad().isSingleDirection()) {
						road.setHitBox(paintHorizontalSingleRoad(road.getRoadOrientation(), coord, source, graphics, single_horizontal));
					} else {
						road.setHitBox(paintHorizontalDoubleRoad(road.getRoadOrientation(), coord, source, graphics, double_horizontal));
					}
				} else {
					if(connect.getConnectedRoad().isSingleDirection()) {
						road.setHitBox(paintVerticalSingleRoad(road.getRoadOrientation(), coord, source, graphics, single_vertical));
					} else {
						road.setHitBox(paintVerticalDoubleRoad(road.getRoadOrientation(), coord, source, graphics, double_vertical));
					}
				}
			}
		}
	}
	
	private static Coordinates convertCoordinates(Coordinates realCoordinates) {
		Coordinates convertedCoordinates = new Coordinates();
		convertedCoordinates.setxCoord(CELL_SIZE + CELL_SIZE * 3 * realCoordinates.getxCoord());
		convertedCoordinates.setyCoord(CELL_SIZE + CELL_SIZE * 3 * realCoordinates.getyCoord());
		return convertedCoordinates;
	}
	
	private static Polygon paintHorizontalSingleRoad(Orientation orientation, Coordinates coord, Coordinates source, Graphics2D graphics, BufferedImage image) {
		if(orientation == Orientation.LEFT) {
			for(int x = source.getxCoord() + CELL_SIZE; x < coord.getxCoord(); x += CELL_SIZE) {
				graphics.drawImage(image, x, coord.getyCoord(),	null);
			}
			int[] xPoints = {source.getxCoord() + CELL_SIZE, source.getxCoord() + CELL_SIZE, coord.getxCoord(), coord.getxCoord()};
			int[] yPoints = {coord.getyCoord(), coord.getyCoord() + CELL_SIZE, coord.getyCoord() + CELL_SIZE, coord.getyCoord()};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
		else {
			for(int x = coord.getxCoord() + CELL_SIZE; x < source.getxCoord(); x += CELL_SIZE) {
				graphics.drawImage(image, x, coord.getyCoord(),	null);
			}
			int[] xPoints = {coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE, source.getxCoord(), source.getxCoord()};
			int[] yPoints = {coord.getyCoord(), coord.getyCoord() + CELL_SIZE, coord.getyCoord() + CELL_SIZE, coord.getyCoord()};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
	}
	
	private static Polygon paintHorizontalDoubleRoad(Orientation orientation, Coordinates coord, Coordinates source, Graphics2D graphics, BufferedImage image) {
		if(orientation == Orientation.LEFT) {
			for(int x = source.getxCoord() + CELL_SIZE; x < coord.getxCoord(); x += CELL_SIZE) {
				graphics.drawImage(image, x, coord.getyCoord(),	null);
			}
			int[] xPoints = {source.getxCoord() + CELL_SIZE, source.getxCoord() + CELL_SIZE, coord.getxCoord(), coord.getxCoord()};
			int[] yPoints = {coord.getyCoord() + CELL_SIZE / 2, coord.getyCoord() + CELL_SIZE, coord.getyCoord() + CELL_SIZE, coord.getyCoord() + CELL_SIZE / 2};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
		else {
			for(int x = coord.getxCoord() + CELL_SIZE; x < source.getxCoord(); x += CELL_SIZE) {
				graphics.drawImage(image, x, coord.getyCoord(),	null);
			}
			int[] xPoints = {coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE, source.getxCoord(), source.getxCoord()};
			int[] yPoints = {coord.getyCoord(), coord.getyCoord() + CELL_SIZE / 2, coord.getyCoord() + CELL_SIZE / 2, coord.getyCoord()};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
	}
	
	private static Polygon paintVerticalSingleRoad(Orientation orientation, Coordinates coord, Coordinates source, Graphics2D graphics, BufferedImage image) {
		if(orientation == Orientation.UP) {
			for(int y = source.getyCoord() + CELL_SIZE; y < coord.getyCoord(); y += CELL_SIZE) {
				graphics.drawImage(image, coord.getxCoord(), y,	null);
			}
			int[] xPoints = {coord.getxCoord(), coord.getxCoord(), coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE};
			int[] yPoints = {source.getyCoord() + CELL_SIZE, coord.getyCoord(), coord.getyCoord(), source.getyCoord() + CELL_SIZE};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
		else {
			for(int y = coord.getyCoord() + CELL_SIZE; y < source.getyCoord(); y += CELL_SIZE) {
				graphics.drawImage(image, coord.getxCoord(), y,	null);
			}
			int[] xPoints = {coord.getxCoord(), coord.getxCoord(), coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE};
			int[] yPoints = {coord.getyCoord() + CELL_SIZE, source.getyCoord(), source.getyCoord(), coord.getyCoord() + CELL_SIZE};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
	}
	
	private static Polygon paintVerticalDoubleRoad(Orientation orientation, Coordinates coord, Coordinates source, Graphics2D graphics, BufferedImage image) {
		if(orientation == Orientation.UP) {
			for(int y = source.getyCoord() + CELL_SIZE; y < coord.getyCoord(); y += CELL_SIZE) {
				graphics.drawImage(image, coord.getxCoord(), y,	null);
			}
			int[] xPoints = {coord.getxCoord(), coord.getxCoord(), coord.getxCoord() + CELL_SIZE / 2, coord.getxCoord() + CELL_SIZE / 2};
			int[] yPoints = {source.getyCoord() + CELL_SIZE, coord.getyCoord(), coord.getyCoord(), source.getyCoord() + CELL_SIZE};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
		else {
			for(int y = coord.getyCoord() + CELL_SIZE; y < source.getyCoord(); y += CELL_SIZE) {
				graphics.drawImage(image, coord.getxCoord(), y,	null);
			}
			int[] xPoints = {coord.getxCoord() + CELL_SIZE / 2, coord.getxCoord() + CELL_SIZE / 2, coord.getxCoord() + CELL_SIZE, coord.getxCoord() + CELL_SIZE};
			int[] yPoints = {coord.getyCoord() + CELL_SIZE, source.getyCoord(), source.getyCoord(), coord.getyCoord() + CELL_SIZE};
			Polygon hitBox = new Polygon(xPoints, yPoints, 4);
			return hitBox;
		}
	}
	
	private static void connectionBuilder(RoadMapInfo roadMapInfo) throws Exception {
		for(Road road : roadMapInfo.getRoads()) {
			
			// Get list of possible connections
			ArrayList<Road> possibleConnections = possibleConnections(road, roadMapInfo);
			if(possibleConnections.size() == 0)
				throw new Exception();
			boolean validConnections = false;
			
			// Check valid connections
			for(Road destinationRoad : possibleConnections) {
				Orientation orientation1 = road.getRoadOrientation();
				Orientation orientation2 = destinationRoad.getRoadOrientation();
				
				boolean valid = false;
				valid = valid || (orientation1 == Orientation.DOWN && orientation2 == Orientation.DOWN);
				valid = valid || (orientation1 == Orientation.UP && orientation2 == Orientation.UP);
				valid = valid || (orientation1 == Orientation.LEFT && orientation2 == Orientation.LEFT);
				valid = valid || (orientation1 == Orientation.RIGHT && orientation2 == Orientation.RIGHT);
				valid = valid || (orientation1 == Orientation.DOWN && orientation2 == Orientation.LEFT);
				valid = valid || (orientation1 == Orientation.UP && orientation2 == Orientation.RIGHT);
				valid = valid || (orientation1 == Orientation.LEFT && orientation2 == Orientation.UP);
				valid = valid || (orientation1 == Orientation.RIGHT && orientation2 == Orientation.DOWN);
				if(valid) {
					buildConnection(road, destinationRoad);
					validConnections = true;
				}
			}
			// Throw an exception if the road is a dead end
			if(! validConnections)
				throw new Exception();
		}
	}
	
	/*private static Orientation determineRoadOrientation(Road road) {
		return road.getStartIntersection().getCoordinates().getxCoord()
				== road.getFinishIntersection().getCoordinates().getxCoord()
				? Orientation.VERTICAL
				: Orientation.HORIZONTAL;
	}*/
	
	private static void buildConnection(Road sourceRoad, Road destinationRoad) {
		// Build traffic light
		TrafficLight trafficLight = new TrafficLight();
		trafficLight.setTrafficAllowed(false);
		trafficLight.setDestinationRoad(destinationRoad);
		
		// Build connection
		Connection connection =  new Connection();
		connection.setConnectedRoad(sourceRoad);
		connection.setTrafficLight(trafficLight);
		
		// Relate connection to intersection
		sourceRoad.getFinishIntersection().getInboundConnections().add(connection);
	}
	
	private static ArrayList<Road> possibleConnections(Road road, RoadMapInfo roadMapInfo) {
		ArrayList<Road> possibleConnections = new ArrayList<Road>();
		for(Road entry : roadMapInfo.getRoads()) {
			if(entry.getStartIntersection().getIntersectionId() == road.getFinishIntersection().getIntersectionId()
					&& entry.getFinishIntersection().getIntersectionId() == road.getStartIntersection().getIntersectionId())
				continue;
			if(entry.getStartIntersection().getIntersectionId() == road.getFinishIntersection().getIntersectionId())
				possibleConnections.add(entry);
		}
		return possibleConnections;
	}
	
}

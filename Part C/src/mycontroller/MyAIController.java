package mycontroller;

import controller.CarController;
import strategies.Explorer;
import navigation.Navigator;
import navigation.SimpleNavigator;
import strategies.SimpleWallFollower;
import world.Car;

import java.util.*;

import tiles.MapTile;
import tiles.LavaTrap;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;


public class MyAIController extends CarController{

// 	Shows the last turn direction the car takes
	private WorldSpatial.RelativeDirection lastTurnDirection = null; 

//	Keeps track of the previous state
	private WorldSpatial.Direction previousState = null; 

// 	To store the total no. of keys in the game (Key number car starts with) 
	private final int TOTAL_KEYS; 

//  Store coordinates of the key locations, along with their numbers
	private HashMap<Coordinate, Integer> keyLocs = new HashMap<>();
	
//  Boolean variable to check if the car has traversed the map to find the keys 
	private boolean hasTraversed;

	private Sensor sensor;
	private Explorer explorer;
	private Navigator navigator;
	
	
	public MyAIController(Car car) {
		super(car);
		this.TOTAL_KEYS = car.getKey();
		navigator = new SimpleNavigator(this);
		
		this.hasTraversed = false;
		this.setSensor(new Sensor(this));
		this.explorer = new SimpleWallFollower();
	}
	

	
	@Override
	public void update(float delta) {
		
		
		explorer.update(this, navigator, delta, sensor);

		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		ArrayList<Coordinate> keyCoordinates = new ArrayList<>();
		keyCoordinates = keyFinder(currentView);
		
		
	}
	
	
	/*
	 * Finds and stores key coordinates in the map.
	 * @param currentView The 9x9 view of the car
	 * @return Coordinates of keys stored in an arraylist.
	 */
	public ArrayList<Coordinate> keyFinder(HashMap<Coordinate, MapTile> currentView){
		
		ArrayList<Coordinate> coords = new ArrayList<>();
		
//		For each coordinate in the view of the car, check if it is a lava trap tile.
		for (Coordinate coord: currentView.keySet()){
			
			MapTile value = currentView.get(coord); 
		    
//			If it is a lava trap, retrieve the coordinates of the key if it exists.
			if (value.getType().equals(MapTile.Type.TRAP) && ((TrapTile) value).getTrap().equals("lava")){ 
		    
				int keyVal = ((LavaTrap) value).getKey(); 
				
//				Check if coordinates for all the keys has been stored. 
//				If yes, store all the keys into the ArrayList in order, and
//				flag the hasTraversed to be true.
		    	if(keyLocs.size() == this.TOTAL_KEYS-1) {
		    		coords = sortKeys(keyLocs);
        			hasTraversed = true;
        			break;
	        	}
	            if (keyVal > 0) {
	            	if(!keyLocs.containsKey(coord) && keyVal!=0) {
		        		keyLocs.put(coord,keyVal);
		        	}
		        }
		        
		    }
		} 
		return coords;
	}
	
	
	
	/*
	 * @param keyCoordinates HashMap of the key coordinates with their 
	 * respective values
	 * @return ArrayList of all the key coordinates in descending order
	 */
	public ArrayList<Coordinate> sortKeys(HashMap<Coordinate, Integer> keyCoordinates){
		
		ArrayList<Coordinate> coords = new ArrayList<>();
		
//		Sorts the coordinates from the hashmap based on the key values, 
//		in descending order. Then adds them to an ArrayList.
		keyCoordinates.entrySet().stream()
        .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
        .forEach(k -> coords.add(k.getKey()));		
		
		return coords;
		
	}
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	public void checkStateChange() {
		if(previousState == null){
			previousState = getOrientation();
		}
		else{
			if(previousState != getOrientation()){
				if(getSensor().isTurningLeft()){
					getSensor().setTurningLeft(false);
				}
				if(getSensor().isTurningRight()){
					getSensor().setTurningRight(false);
				}
				previousState = getOrientation();
			}
		}
	}

	public WorldSpatial.RelativeDirection getLastTurnDirection() {
		return lastTurnDirection;
	}

	public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		this.lastTurnDirection = lastTurnDirection;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}

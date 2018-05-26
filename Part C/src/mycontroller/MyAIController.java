package mycontroller;

import controller.CarController;
import explore.Explorer;
import navigation.Navigator;
import world.Car;

import java.util.*;

import tiles.MapTile;
import tiles.LavaTrap;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;


public class MyAIController extends CarController{

		// How many minimum units the wall is away from the player.
		
		

		private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.

		private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
		
		// Car Speed to move at
		public final float CAR_SPEED = 3;
		
		// Offset used to differentiate between 0 and 360 degrees
		private int EAST_THRESHOLD = 3;
		
		private final int TOTAL_KEYS;
	
		private HashMap<Coordinate, Integer> keyLocs = new HashMap<>();
	
		private Navigator navigator;
		
		private boolean hasTraversed;
		
		private Sensor sensor;
		
		private Explorer explorer;
		
		public MyAIController(Car car) {
			super(car);
			this.TOTAL_KEYS = car.getKey();
			navigator = new Navigator(this, null, false, this.getHealth(), 0);
			this.hasTraversed = false;
			this.sensor = new Sensor(this);
			this.explorer = new Explorer(sensor, navigator,this);
		}
		
		Coordinate initialGuess;
		boolean notSouth = true;
		
		
		@Override
		public void update(float delta) {
			
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			ArrayList<Coordinate> keyCoordinates = new ArrayList<>();
			keyCoordinates.add(new Coordinate("19,2"));
			checkStateChange();
			
			if(!hasTraversed) {
				explorer.explore(delta, currentView);
				keyCoordinates = keyFinder(currentView);
			}
			else {
				this.applyReverseAcceleration();
				System.out.println("Moving forward");
				navigator.update(delta, keyCoordinates);
			}
			navigator.update(delta, keyCoordinates); 
			System.out.println("FOLLOWING COORDINATES NOW ");
		
			
		}
		
		public ArrayList<Coordinate> keyFinder(HashMap<Coordinate, MapTile> currentView){
			ArrayList<Coordinate> coords = new ArrayList<>();
			for (Coordinate name: currentView.keySet()){ 
				MapTile value = currentView.get(name); 

			    if (value.getType().equals(MapTile.Type.TRAP) && ((TrapTile) value).getTrap().equals("lava")){ 
			    	int keyVal = ((LavaTrap) value).getKey(); 
			    	if(keyLocs.size() == this.TOTAL_KEYS-1) {
		        		for(Coordinate c : keyLocs.keySet()) {
		        			coords.add(c);
		        			this.applyBrake();
		        			hasTraversed = true;
		        			break;
		        		}
		        		break;
		        	}   
			        if (keyVal > 0) {
			            System.out.println("Key: " + keyVal + " Found at: " + name.x+ "," + name.y); 
			        	if(!keyLocs.containsKey(name) && keyVal!=0) {
			        		keyLocs.put(name,keyVal);
			        	}
			        }
			        
			    }
			} 
			System.out.println(keyLocs.entrySet());
			return coords;
		}
		
		


		
		/**
		 * Checks whether the car's state has changed or not, stops turning if it
		 *  already has.
		 */
		private void checkStateChange() {
			if(previousState == null){
				previousState = getOrientation();
			}
			else{
				if(previousState != getOrientation()){
					if(sensor.isTurningLeft()){
						sensor.setTurningLeft(false);
					}
					if(sensor.isTurningRight()){
						TurningRight = false;
					}
					previousState = getOrientation();
				}
			}
		}

		public boolean isFollowingWall() {
			return isFollowingWall;
		}

		public void setFollowingWall(boolean isFollowingWall) {
			this.isFollowingWall = isFollowingWall;
		}

		public WorldSpatial.RelativeDirection getLastTurnDirection() {
			return lastTurnDirection;
		}

		public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
			this.lastTurnDirection = lastTurnDirection;
		}
		
		

	}

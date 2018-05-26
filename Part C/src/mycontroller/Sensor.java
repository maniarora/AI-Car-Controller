package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class Sensor {
	
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private int wallSensitivity = 2;
	private MyAIController myAIController;
	
	public Sensor(MyAIController myAIController) {
		this.myAIController = myAIController;
	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	 public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		
		}
	}
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	 public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}
		
	}
	

	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(myAIController.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(myAIController.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(myAIController.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(myAIController.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}

	public boolean isTurningLeft() {
		return isTurningLeft;
	}

	public void setTurningLeft(boolean isTurningLeft) {
		this.isTurningLeft = isTurningLeft;
	}

	public boolean isTurningRight() {
		return isTurningRight;
	}

	public void setTurningRight(boolean isTurningRight) {
		this.isTurningRight = isTurningRight;
	}

	public boolean isFollowingWall() {
		return isFollowingWall;
	}

	public void setFollowingWall(boolean isFollowingWall) {
		this.isFollowingWall = isFollowingWall;
	}
	

}

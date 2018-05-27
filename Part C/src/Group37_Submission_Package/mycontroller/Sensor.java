package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
/**
 * This class provides all the sensing functionalities of the vehicle, 
 * detecting its surroundings. 
 * @author Manindra Arora (827703), Ninad Kavi (855506), Ujashkumar Patel (848395)
 * */
public class Sensor {
	
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private int wallSensitivity = 2;
	
	private MyAIController myAIController;
	 private HashMap<Coordinate, MapTile> currentView;
	
	public Sensor(MyAIController myAIController) {
		this.myAIController = myAIController;
	}

	/**
	 * Check if car has a wall infront.
	 * @param orientation the current orientation of the car
	 * @param currentView what the car can currently see
	 * @return
	 */
	
	public void update(float delta) {
		this.currentView = myAIController.getView();     
		
	}
	
	 public boolean checkWallAhead(WorldSpatial.Direction orientation){
		switch(orientation){
		case EAST:
			return checkEast();
		case WEST:
			return checkWest();
		case NORTH:
			return checkNorth();
		case SOUTH:
			return checkSouth();
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
	 public boolean checkFollowingWall(WorldSpatial.Direction orientation) {
		
		switch(orientation){
		case EAST:
			return checkNorth();
		case NORTH:
			return checkWest();
		case SOUTH:
			return checkEast();
		case WEST:
			return checkSouth();
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
	public boolean checkEast(){
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
	
	public boolean checkWest(){
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
	
	public boolean checkNorth(){
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
	
	public boolean checkSouth(){
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

	
	/*************************************************************************
								Helper Methods 				
	 ************************************************************************/
	
	
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

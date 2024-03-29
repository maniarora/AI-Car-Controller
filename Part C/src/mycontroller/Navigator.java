package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;


/**
 * This abstract class implements the methods that enable the car to navigate 
 * from its current position to the desired coordinates. It is primarily
 * responsible for moving the car, and adjusting the alignment of the car 
 * based on its orientation.
 * @author Manindra Arora (827703), Ninad Kavi (855506), Ujashkumar Patel (848395)
 * */
public abstract class Navigator {
	
	public final int EAST_THRESHOLD = 3;
	public final float MAX_CAR_SPEED = (float) 2;
	public MyAIController controller;
	private float carSpeed = 3;


	

	public abstract void update(float delta, ArrayList<Coordinate> coordsToNavigate); 
	
	

	/*
	 * readjust the orientation of the car based on current alignment
	 * @param lastTurnDirection the last direction in which the car made a turn
	 * @param delta 
	 */
	public void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
		if(lastTurnDirection != null){
			if(!controller.getSensor().isTurningRight() && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(controller.getOrientation(),delta);
			}
			else if(!controller.getSensor().isTurningLeft() && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(controller.getOrientation(),delta);
			}
		}
		
	}
	
	
	/**
	 * Adjust the orientation of the car if it is moving too much to the left.
	 * @param orientation the current direction faced
	 * @param delta
	 */
	public void adjustLeft(WorldSpatial.Direction orientation, float delta) {
		
		switch(orientation){
		case EAST:
			if( controller.getAngle() > WorldSpatial.EAST_DEGREE_MIN + EAST_THRESHOLD ){
				controller.turnRight(delta);
			}
			break;
		case WEST:
			if( controller.getAngle() > WorldSpatial.WEST_DEGREE ){
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if( controller.getAngle() > WorldSpatial.NORTH_DEGREE ){
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if( controller.getAngle() > WorldSpatial.SOUTH_DEGREE ){
				controller.turnRight(delta);
			}
			break;

		default:
			break;
		}
		
	}

	/**
	 * Adjust the orientation of the car if it is moving too much to the right.
	 * @param orientation the current direction faced
	 * @param delta
	 */
	public void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if( controller.getAngle() > WorldSpatial.SOUTH_DEGREE && 
					controller.getAngle() < WorldSpatial.EAST_DEGREE_MAX ){
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			if( controller.getAngle() < WorldSpatial.WEST_DEGREE ){
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if( controller.getAngle() < WorldSpatial.NORTH_DEGREE ){
				controller.turnLeft(delta);
			}
			break;
		case SOUTH:
			if( controller.getAngle() < WorldSpatial.SOUTH_DEGREE ){
				controller.turnLeft(delta);
			}
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * Apply a 90 degree counter-clockwise turn to the car.
	 * @param orientation current direction the car is facing
	 * @param delta 
	 */
	public void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnLeft(delta);
			}
			break;
		case SOUTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnLeft(delta);
			}
			break;

		default:
			break;
		
		}
		
	}
	
	/**
	 * Apply a 90 degree clockwise turn to the car.
	 * @param orientation current direction the car is facing
	 * @param delta 
	 */
	public void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnRight(delta);
				
			}
			break;
		case WEST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnRight(delta);
			}
			break;

		default:
			break;
		
		}
		
	}
	
	public float getCarSpeed() {
		return this.carSpeed;
	}
	public void setCarSpeed(float carSpeed) {
		this.carSpeed = carSpeed;
	}

}

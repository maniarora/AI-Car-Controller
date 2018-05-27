package strategies;

import java.util.HashMap;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import navigation.Navigator;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * This class serves as the basic Exploration strategy we use. As provided 
 * in the base code, we find the nearest Left-wall and stick to it and follow it
 *  
 * @author: Manindra Arora (827703) Ninad Kavi (855506)  Ujashkumar Patel (848395)
 * 
 * Group 37
 */

public class SimpleWallFollower extends Explorer{
	/**
	 * This method encompasses most of the functionality of our design, i.e 
	 * finding a path for the car to move on. It uses data received from the 
	 * sensor to generate a path and then supplies the necessary instruction to
	 * the navigator in order to move the car
	 * 
	 * @param controller: the MyAIController used to implement the functionality
	 * @param navigator: the navigator which moves the car about 
	 * @param delta: delta since last timestep
	 * @param sensor: the sensor which checks the car's surroundings
	 * */
	public void update(MyAIController controller, Navigator navigator, float delta, Sensor sensor) {
		
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		controller.checkStateChange();

		// If you are not following a wall initially, find a wall to stick to!
		if(!sensor.isFollowingWall()){
			if(controller.getSpeed() < navigator.MAX_CAR_SPEED){
				controller.applyForwardAcceleration();
			}
			// Turn towards the north
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
				navigator.applyLeftTurn(controller.getOrientation(),delta);
			}
			if(sensor.checkNorth()){
				// Turn right until we go back to east!
				if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
					controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
					navigator.applyRightTurn(controller.getOrientation(),delta);
				}
				else{
					sensor.setFollowingWall(true);
				}
			}
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			
			// Readjust the car if it is misaligned.
			navigator.readjust(controller.getLastTurnDirection(),delta);
			
			if(sensor.isTurningRight()){
				navigator.applyRightTurn(controller.getOrientation(),delta);
			}
			else if(sensor.isTurningLeft()){
				// Apply the left turn if you are not currently near a wall.
				if(!sensor.checkFollowingWall(controller.getOrientation())){
					navigator.applyLeftTurn(controller.getOrientation(),delta);
				}
				else{
					sensor.setTurningLeft(false);
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(sensor.checkFollowingWall(controller.getOrientation())){
				// Maintain some velocity
				if(controller.getSpeed() < navigator.getCarSpeed()){
					controller.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(sensor.checkWallAhead(controller.getOrientation())){
					controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
					sensor.setTurningRight(true);				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
				sensor.setTurningLeft(true);
			}
		}
	}
}
	



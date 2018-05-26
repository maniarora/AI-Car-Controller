package strategies;

import java.util.HashMap;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import navigation.Navigator;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class SimpleWallFollower extends Explorer{

//	@Override
//	public void update(HashMap<Coordinate, MapTile> currentView, float delta, MyAIController controller, Navigator navigator) {
//	
//			
//			if(controller.getSpeed() < navigator.MAX_CAR_SPEED){
//				controller.applyForwardAcceleration();
//			}
//			// Turn towards the north
//			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
//				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
//				navigator.applyLeftTurn(controller.getOrientation(),delta);
//			}
//			if(controller.getSensor().checkNorth(currentView)){
//				// Turn right until we go back to east!
//				if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
//					controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
//					navigator.applyRightTurn(controller.getOrientation(),delta);
//				}
//				else{
//					controller.getSensor().setFollowingWall(true);
//				}
//			}
//		}

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
			if(sensor.checkNorth(currentView)){
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
				if(!sensor.checkFollowingWall(controller.getOrientation(),currentView)){
					navigator.applyLeftTurn(controller.getOrientation(),delta);
				}
				else{
					sensor.setTurningLeft(false);
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(sensor.checkFollowingWall(controller.getOrientation(),currentView)){
				// Maintain some velocity
				if(controller.getSpeed() < navigator.carSpeed){
					controller.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(sensor.checkWallAhead(controller.getOrientation(),currentView)){
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
	



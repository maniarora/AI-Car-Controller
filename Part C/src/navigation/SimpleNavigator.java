package navigation;

import java.util.ArrayList;
import java.util.HashMap;

import mycontroller.MyAIController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class SimpleNavigator extends Navigator {
	

	
	public SimpleNavigator(MyAIController myAIController) {
		super();
		EAST_THRESHOLD = 3;
		MAX_CAR_SPEED = (float) 2.0;
		this.controller = myAIController;
	}
	
	//simple wall follower
	@Override
	public void move(float delta) {
		
		if(controller.getSpeed() < MAX_CAR_SPEED){
			controller.applyForwardAcceleration();
		}
		// Turn towards the north
		if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
			controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
			applyLeftTurn(controller.getOrientation(),delta);
		}
		if(controller.getSensor().checkNorth(controller.getView())){
			// Turn right until we go back to east!
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
				applyRightTurn(controller.getOrientation(),delta);
			}
			else{
				controller.getSensor().setFollowingWall(true);
			}
		}
	}

	@Override
	
	public void update(float delta, ArrayList<Coordinate> coordsToNavigate) {
//		Coordinate coord = null;
//		
//		if(coordsToNavigate.isEmpty()) {
//			System.out.println("No coordinates to follow given");
//		}
//		else {
//			coord = coordsToNavigate.get(0);
//			float targetDegree = (float) getDegreeOfCoord(coord);
//			
//			// Remove out-dated moves (only keep track of the last TRACKED_MOVES moves)
//            if (moveHistory.size() > TRACKED_MOVES) {
//                moveHistory.poll();
//            }
//            
//            if(allSame(moveHistory) && controller.getHealth() < previousHealth
//            		&& moveHistory.size()>0) {
//            	escaping = true;
//            	escapeMove = moveHistory.peek().getOppositeMove();
//            }
//            
//            if(escaping) {
//            	updateEscape(delta, coord, targetDegree);
//            }
//            
//            RelativeDirection targetDirection = getDirection(targetDegree);
//            if(targetDirection == RelativeDirection.FORWARD ||
//            		targetDirection == RelativeDirection.BACKWARD) {
//            	goForwardOrBackward(targetDirection, adjustVelocity(coord));
//            }
//            else {
//            	makeBestTurningMove(targetDegree, coord, delta);
//            }
//
//		}
//		
	}

//
	
	
		
}
	
	

	
	



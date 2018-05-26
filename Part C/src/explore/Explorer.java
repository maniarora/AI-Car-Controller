package explore;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class Explorer {
	
	private boolean hasTraversed = false;
	
	
	
	public Explorer(boolean hasTraversed) {
		this.hasTraversed = hasTraversed;
	}
	
	
	
	public void update(float delta) {
	// Gets what the car can see
	HashMap<Coordinate, MapTile> currentView = getView();
	ArrayList<Coordinate> coords = new ArrayList<>();
	for (Coordinate name: currentView.keySet()){ 
		MapTile value = currentView.get(name); 

	    if (value.getType().equals(MapTile.Type.TRAP) && ((TrapTile) value).getTrap().equals("lava")){ 
	    	int keyVal = ((LavaTrap) value).getKey(); 
	    	if(keyLocs.size() == this.TOTAL_KEYS-1) {
        		for(Coordinate c : keyLocs.keySet()) {
        			coords.add(c);
        			M.applyBrake();
        			
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
//	        navigator.update(delta, coords); 
			
	    }
	} 
	System.out.println(keyLocs.entrySet());
	
	
	checkStateChange();

	// If you are not following a wall initially, find a wall to stick to!
	if(!isFollowingWall){
		followWall(currentView, delta);
	}
	// Once the car is already stuck to a wall, apply the following logic
	else{
		
		// Readjust the car if it is misaligned.
		readjust(lastTurnDirection,delta);
		
		if(isTurningRight){
			applyRightTurn(getOrientation(),delta);
		}
		else if(isTurningLeft){
			// Apply the left turn if you are not currently near a wall.
			if(!checkFollowingWall(getOrientation(),currentView)){
				applyLeftTurn(getOrientation(),delta);
			}
			else{
				isTurningLeft = false;
			}
		}
		// Try to determine whether or not the car is next to a wall.
		else if(checkFollowingWall(getOrientation(),currentView)){
			// Maintain some velocity
			if(getSpeed() < CAR_SPEED){
				applyForwardAcceleration();
			}
			// If there is wall ahead, turn right!
			if(checkWallAhead(getOrientation(),currentView)){
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;				
				
			}

		}
		// This indicates that I can do a left turn if I am not turning right
		else{
			lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
			isTurningLeft = true;
		}
	}
}

	

}

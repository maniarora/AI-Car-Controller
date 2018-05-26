package mycontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utilities.Coordinate;
import world.WorldSpatial;

public class Direction {
	
	/** An enum for forwards and backwards, since they weren't supplied. */
	public static enum RelativeDirection{
		FORWARD, BACKWARD, RIGHT, LEFT
	};
	
	/** A HashMap where a directoin maps to a 2D int array for indexing purposes. */
    public static final HashMap<WorldSpatial.Direction, int[]> MOD_MAP = new HashMap<WorldSpatial.Direction, int[]>() {
        {
            put(WorldSpatial.Direction.EAST, new int[] { 1, 0 });
            put(WorldSpatial.Direction.NORTH, new int[] { 0, 1 });
            put(WorldSpatial.Direction.WEST, new int[] { -1, 0 });
            put(WorldSpatial.Direction.SOUTH, new int[] { 0, -1 });
        }
    };
    
    /** A HashMap where each key maps to the counterclockwise direction. */
    public static final Map<WorldSpatial.Direction, WorldSpatial.Direction> LEFT_OF = new HashMap<WorldSpatial.Direction, WorldSpatial.Direction>() {
        {
            put(WorldSpatial.Direction.EAST, WorldSpatial.Direction.NORTH);
            put(WorldSpatial.Direction.NORTH, WorldSpatial.Direction.WEST);
            put(WorldSpatial.Direction.WEST, WorldSpatial.Direction.SOUTH);
            put(WorldSpatial.Direction.SOUTH, WorldSpatial.Direction.EAST);
        }
    };
    
    /**
     * Method for getting the counterclockwise orientation from the given
     * orientation.
     * 
     * @return 
     */
    public static WorldSpatial.Direction getLeftOf(Direction orientation) {
        return LEFT_OF.get(orientation);
    }

    /**
     * Method for getting the clockwise orientation from the given orientation. We
     * iterate through the leftOf map and get the key corresponding to the given
     * value.
     * 
     * @return 
     */
    public static WorldSpatial.Direction getRightOf(Direction orientation) {
        for (Entry<WorldSpatial.Direction, WorldSpatial.Direction> entry : LEFT_OF.entrySet()) {
            if (orientation.equals(entry.getValue())) {
                return entry.getKey();
            }
            
        }
        return null;
    }
  
    /**
     * Given an orientation and a relative direction, return the direction either
     * clockwise or counterclockwise of that given direction, based on the relative
     * direction.
     * 
     * @param orientation
     * @param direction
     * @return
     */
//    public static WorldSpatial.Direction getToSideOf(Direction orientation, RelativeDirection direction) {
//        if (direction == RelativeDirection.LEFT) {
//            return getLeftOf(orientation);
//        } else {
//            return getRightOf(orientation);
//        }
//    }
//    
//    
//    public static Direction getToSideOf(Direction orientation, RelativeDirection direction) {
//        if (direction == RelativeDirection.BACKWARD) {
//            return getLeftOf(getLeftOf(orientation));
//        } else {
//            return orientation;
//        }
//    }
//    
//    
//    /**
//     * Gets the euclidian distance between two Coordinates.
//     * 
//     * @param c1
//     *            The first Coordinate
//     * @param c2
//     *            The second Coordinate
//     * @return The euclidian distance between two coordinates.
//     */
    public static float distanceBetweenCoords(Coordinate c1, Coordinate c2) {
        float xDiff = Math.abs(c1.x - c2.x);
        float yDiff = Math.abs(c1.y - c2.y);
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}

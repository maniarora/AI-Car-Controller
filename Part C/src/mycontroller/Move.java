package mycontroller;

import mycontroller.Direction.RelativeDirection;
import utilities.Coordinate;

public class Move {
	
	public enum SpeedState {DECCELERATING, CONSTANT, ACCELERATING};
	
	public Coordinate currentCoordinate;
	public Coordinate targetCoordinate;
	private final float degree;	
	private final SpeedState speedState;
	private final Direction.RelativeDirection turnDirection;
	
	public Move(Coordinate currentCoordinate, Coordinate targetCoordinate, float degree, 
			SpeedState oppositeState, Direction.RelativeDirection turnDirection) {
		this.speedState = oppositeState;
		this.currentCoordinate = currentCoordinate;
		this.targetCoordinate = targetCoordinate;
		this.degree = degree;
		this.turnDirection = turnDirection;
	}
	
//	Comparing move objects for equality
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move otherMove = (Move) obj;
            // One move is only equal to another if all of the attributes are the same.
            return (this.currentCoordinate.equals(otherMove.currentCoordinate) &&
                    this.targetCoordinate.equals(otherMove.targetCoordinate) &&
                    Float.compare(this.degree, otherMove.degree) == 0 &&
                    this.speedState == otherMove.speedState &&
                    this.turnDirection == otherMove.turnDirection);
        }
        return false;
    }

	
    public Move getOppositeMove() {
    	SpeedState oppositeState = null;
        Direction.RelativeDirection oppositeDirection = null;
        if (oppositeState == SpeedState.DECCELERATING) {
        	oppositeState = SpeedState.ACCELERATING;
        } else {
//        	Opposite move of maintaining constant speed would be to accelerate.
        	oppositeState = SpeedState.DECCELERATING;
        }
        if (turnDirection == Direction.RelativeDirection.RIGHT) {
            oppositeDirection = Direction.RelativeDirection.LEFT;
        } else {
            oppositeDirection = Direction.RelativeDirection.RIGHT;
        }
        return new Move(currentCoordinate, targetCoordinate, degree, oppositeState, oppositeDirection);
    }
	
    public SpeedState getSpeedState() {
        return this.speedState;
    }
    
    public RelativeDirection getTurnDirection() {
    	return this.turnDirection;
    }
}

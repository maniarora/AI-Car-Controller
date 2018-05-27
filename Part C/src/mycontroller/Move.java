package mycontroller;


import utilities.Coordinate;
import world.WorldSpatial.RelativeDirection;

public class Move {
	
	public enum SpeedState {DECCELERATING, CONSTANT, ACCELERATING};
	
	public Coordinate currentCoordinate;
	public Coordinate targetCoordinate;
	private final float degree;	
	private final SpeedState speedState;
	private final RelativeDirection turnDirection;
	
	public Move(Coordinate currentCoordinate, Coordinate targetCoordinate, float degree, 
			SpeedState oppositeState, RelativeDirection turnDirection) {
		this.speedState = oppositeState;
		this.currentCoordinate = currentCoordinate;
		this.targetCoordinate = targetCoordinate;
		this.degree = degree;
		this.turnDirection = turnDirection;
	}
	
	 /**
     * Overrides equals, used when comparing Move objects for equality
     *
     * @param obj The object being tested for equality
     * @return boolean true if the object being compared is considered equal to the calling instance, false otherwise.
     */
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

   
 }


package mycontroller;
import java.util.*;

import com.badlogic.gdx.math.Vector2;

import mycontroller.Direction.RelativeDirection;
import utilities.Coordinate;
import utilities.PeekTuple;
import world.WorldSpatial;
public class Navigator {

	private MyAIController controller;
	private Move.SpeedState desiredState;
	private LinkedList<Move> moveHistory;
	private Boolean escaping;
	private Integer durationOfEscapeMove;
	private float previousHealth;
	private Move escapeMove;
	private Integer numEscapeMoves;
	
	private final Double MAX_SPEED = 2.0;
	private final Integer TRACKED_MOVES = 30;
	
	public Navigator(MyAIController controller, LinkedList<Move> moveHistory, Boolean escaping, Float previousHealth,
			Integer numEscapeMoves) {
	
		this.controller = controller;
		this.moveHistory = new LinkedList<Move>();
		this.escaping = false;
		this.previousHealth = controller.getHealth();
		this.numEscapeMoves = 0;
	}
	
	
	public void update(float delta, ArrayList<Coordinate> coordsToNavigate) {
		Coordinate coord = null;
		
		if(coordsToNavigate.isEmpty()) {
			System.out.println("No coordinates to follow given");
		}
		else {
			coord = coordsToNavigate.get(0);
			float targetDegree = (float) getDegreeOfCoord(coord);
			
			// Remove out-dated moves (only keep track of the last TRACKED_MOVES moves)
            if (moveHistory.size() > TRACKED_MOVES) {
                moveHistory.poll();
            }
            
            if(allSame(moveHistory) && controller.getHealth() < previousHealth
            		&& moveHistory.size()>0) {
            	escaping = true;
            	escapeMove = moveHistory.peek().getOppositeMove();
            }
            
            if(escaping) {
            	updateEscape(delta, coord, targetDegree);
            }
            
            RelativeDirection targetDirection = getDirection(targetDegree);
            if(targetDirection == RelativeDirection.FORWARD ||
            		targetDirection == RelativeDirection.BACKWARD) {
            	goForwardOrBackward(targetDirection, adjustVelocity(coord));
            }
            else {
            	makeBestTurningMove(targetDegree, coord, delta);
            }

		}
		
	}



	private void makeBestTurningMove(float targetDegree, Coordinate coord, float delta) {

        // Generate combinations of acceleration, reverse acceleration, Left & Right turns
        Vector2 currVelocity = controller.getVelocity();
        Vector2[] testSpeeds = getTestSpeeds(currVelocity);
        Move.SpeedState[] speedChanges = getSpeedChanges(testSpeeds, currVelocity);
        Direction.RelativeDirection[] testDirections = {Direction.RelativeDirection.LEFT,
                Direction.RelativeDirection.RIGHT};

        // Find the best direction and the index of the best change in velocity to reach the destination
        Direction.RelativeDirection bestDirection = null;
        int bestSpeedInd = 0;
        float minDist = Float.MAX_VALUE;
        for (int speed = 0; speed < 3; speed++) {
            for (int d = 0; d < 2; d++) {

                // Use controller.peek(...) to determine best combination to reach target coordinate
                // TODO: BUG! peek(...) method doesn't accurately predict coordinate. Re-write peek(...).
                WorldSpatial.RelativeDirection testDirection = null;
                if (testDirections[d] == Direction.RelativeDirection.RIGHT) {
                    testDirection = WorldSpatial.RelativeDirection.RIGHT;
                }
                else {
                    testDirection = WorldSpatial.RelativeDirection.LEFT;
                }
                PeekTuple approxDest = controller.peek(testSpeeds[speed], targetDegree, testDirection, delta);
                Coordinate approxCoord = approxDest.getCoordinate();
                float projectedDistanceFromTarget = Direction.distanceBetweenCoords(coord, approxCoord);

                // Moves that lead to reachable destinations closest to the predicted coordinates are the best
                if (approxDest.getReachable() && projectedDistanceFromTarget < minDist) {
                    minDist = Direction.distanceBetweenCoords(coord, approxCoord);
                    bestDirection = testDirections[d];
                    bestSpeedInd = speed;
                }
            }
        }
        // Move the car!
        moveCar(currVelocity.len(), testSpeeds[bestSpeedInd].len(), delta, bestDirection);

        // Update Queue of Last Moves
        moveHistory.offer(new Move(new Coordinate(controller.getPosition()), coord, controller.getAngle(), speedChanges[bestSpeedInd],
                bestDirection));
        
    }

	private void updateEscape(float delta, Coordinate coord, float targetDegree) {
		
		if(escaping && numEscapeMoves>0 && allSame(moveHistory)) {
			moveForward(delta, coord);
			numEscapeMoves +=1;
			return;
		}
		
		if(targetDegree == controller.getAngle()) {
			escaping = false;
			escapeMove = null;
		}
		
		if(escaping) {
			numEscapeMoves +=1;
			moveCar(escapeMove, delta, coord);
		}
	}
	
	private Boolean allSame(LinkedList<Move> moveHistory) {
		LinkedList<Move> moves = new LinkedList<Move>(moveHistory);
		Move firstMove = moves.poll();
		Move thisMove;
		
		do {
			thisMove = moves.poll();
			
			if(thisMove == null || firstMove == null || !firstMove.equals(thisMove)) {
				return false;
			}
		}while(!moves.isEmpty());
			
		return true;
	}
	
	private void moveForward(float delta, Coordinate targetCoord) {
		Move.SpeedState speedState;
		RelativeDirection direction = Direction.RelativeDirection.FORWARD;
		
		if(escapeMove.getSpeedState() == Move.SpeedState.ACCELERATING) {
			controller.applyForwardAcceleration();
			speedState = Move.SpeedState.ACCELERATING;
		}
		else {
			controller.applyReverseAcceleration();
			speedState = Move.SpeedState.DECCELERATING;
		}
		previousHealth = controller.getHealth();
		moveHistory.offer(new Move(new Coordinate(controller.getPosition()), targetCoord, controller.getAngle(), speedState, direction));
	}
	
	private void moveCar(Move escapeMove, float delta, Coordinate targetCoord) {
		Move.SpeedState speedState;
		RelativeDirection direction;
		
		if(escapeMove.getSpeedState() == Move.SpeedState.ACCELERATING) {
			controller.applyForwardAcceleration();
			speedState = Move.SpeedState.ACCELERATING;
		}
		else {
			controller.applyReverseAcceleration();
			speedState = Move.SpeedState.DECCELERATING;
		}
		if (escapeMove.getTurnDirection() == RelativeDirection.RIGHT) {
			controller.turnRight(delta);
			direction = Direction.RelativeDirection.RIGHT;
		}
		else {
			controller.turnLeft(delta);
			direction = Direction.RelativeDirection.LEFT;
		}
		previousHealth = controller.getHealth();
		moveHistory.offer(new Move(new Coordinate(controller.getPosition()), targetCoord, controller.getAngle(), speedState, direction));
	}
	
    private void moveCar(float currentVelocity, float desiredVelocity, float delta,
            Direction.RelativeDirection bestDirection) {
		if (currentVelocity < desiredVelocity) {
			controller.applyForwardAcceleration();
		} 
		else {
			controller.applyReverseAcceleration();
		}
		if (bestDirection == Direction.RelativeDirection.RIGHT) {
			controller.turnRight(delta);
		} 
		else {
			controller.turnLeft(delta);
		}
		previousHealth = controller.getHealth();
	}
    
    private Vector2[] getTestSpeeds(Vector2 currVelocity) {
        float addedX = (Double.compare(currVelocity.x, 0.0) == 0 ? (float) .001 : 0);
        float addedY = (Double.compare(currVelocity.y, 0.0) == 0 ? (float) .001 : 0);
        Vector2[] testSpeeds = {
                new Vector2(currVelocity.x * (float) 1.1 + addedX, currVelocity.y * (float) 1.1 + addedY),
                new Vector2(currVelocity.x * (float) 1.05 + addedX, currVelocity.y * (float) 1.05 + addedY),
                new Vector2(currVelocity.x * (float) 0.95 + addedX, currVelocity.y * (float) 0.95 + addedY),
                new Vector2(currVelocity.x * (float) 0.9 + addedX, currVelocity.y * (float) 0.9 + addedY)};

        return testSpeeds;
    }
    
    private Move.SpeedState[] getSpeedChanges(Vector2[] testSpeeds, Vector2 currVelocity) {
        float currentSpeed = currVelocity.len();
        Move.SpeedState[] speedChanges = new Move.SpeedState[testSpeeds.length];
        for (int i = 0; i < testSpeeds.length; i++) {
            if (testSpeeds[i].len() < currentSpeed) {
                speedChanges[i] = Move.SpeedState.DECCELERATING;
            } else {
                speedChanges[i] = Move.SpeedState.ACCELERATING;
            }
        }
        return speedChanges;
    }

    private void goForwardOrBackward(Direction.RelativeDirection d, Move.SpeedState speedChange) {
        if (d == Direction.RelativeDirection.FORWARD) {
            if (speedChange == Move.SpeedState.ACCELERATING) {
                controller.applyForwardAcceleration();
            } else if (speedChange == Move.SpeedState.DECCELERATING) {
                controller.applyReverseAcceleration();
            }
        } else if (d == Direction.RelativeDirection.BACKWARD) {
            if (speedChange == Move.SpeedState.ACCELERATING) {
                controller.applyReverseAcceleration();
            } else if (speedChange == Move.SpeedState.DECCELERATING) {
                controller.applyForwardAcceleration();
            }
        }
        // Update the last observed health
        previousHealth = controller.getHealth();
    }
    
    private Move.SpeedState adjustVelocity(Coordinate coordinate) {
        Coordinate carPosition = new Coordinate(controller.getPosition());

        // If the PathFinder is telling us to slow down, or if we're above the max speed, slow down!
        if (Direction.distanceBetweenCoords(coordinate, carPosition) <= 3 / 3 || controller.getSpeed() > MAX_SPEED) {
            return Move.SpeedState.DECCELERATING;
        }
        // If the PathFinder is happy with our acceleration, or we're at max speed, maintain current speed
        else if (Direction.distanceBetweenCoords(coordinate, carPosition) < 3 || controller.getSpeed() == MAX_SPEED) {
            return Move.SpeedState.CONSTANT;
        }
        // If the PathFinder is saying to go to the coordinate at or beyond the edge of our visual map & we're below
        // max speed, Accelerate!
        else {
            return Move.SpeedState.ACCELERATING;
        }
    }
    
    private double getDegreeOfCoord(Coordinate coord) {

        // Calculate the deltas as the next minus the current
        double delta_x = (double) coord.x - (double) controller.getX();
        double delta_y = (double) coord.y - (double) controller.getY();

        // Calculate the angle in radians using atan2
        double theta = Math.atan2(delta_y, delta_x);

        // Calculate the angle in degrees (coordinate from the east, 0 degrees; 270 is given as -90, 225 as -135, etc.)
        double angle = theta * 180 / Math.PI;


        // If angle is negative, make it positive (e.g. -90 to 270)
        if (Double.compare(angle, 0.0) < 0) {
            angle += 360.0;
        }
        return angle;
    }
    
    private Direction.RelativeDirection getDirection(double degree) {

        double tarAngle = degree;
        double carAngle = controller.getAngle();

        if (tarAngle - carAngle == 0) {
            return Direction.RelativeDirection.FORWARD;
        } else if (tarAngle + 180 == carAngle ||
                tarAngle - 180 == carAngle) {
            return Direction.RelativeDirection.BACKWARD;
        } else if (Math.abs(carAngle - tarAngle) < 180) {
            if (carAngle < tarAngle) {
                return Direction.RelativeDirection.LEFT;
            } else {
                return Direction.RelativeDirection.RIGHT;
            }
        } else {
            if (carAngle < tarAngle) {
                return Direction.RelativeDirection.RIGHT;
            } else {
                return Direction.RelativeDirection.LEFT;
            }
        }
    }
    
    

}

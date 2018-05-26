package navigation;
import java.util.*;

import com.badlogic.gdx.math.Vector2;

import mycontroller.Direction;
import mycontroller.Move;
import mycontroller.MyAIController;
import mycontroller.Direction.RelativeDirection;
import mycontroller.Move.SpeedState;
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
	
    /**
     * update takes delta, and a list of coordinates to follow, and sends messages to the car to turn and accelerate or
     * reverse accelerate in order to follow the first coordinate in the list. The list is used for extensibility
     * purposes.
     *
     * @param delta          The time step specified in Simulation.
     * @param coordsToFollow The list of coordinates received from a PathFinder class, used to direct the car.
     */
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


    /**
     * Generates and tests different alternatives for acceleration and turning, selecting and making the move that is
     * predicted by peek(...) to take the car closest to the targeted coordinate.
     *
     * @param tarDegree The degree of the targeted coordinate.
     * @param coord     The targeted coordinate given by the PathFinder class.
     * @param delta     The time step specified in Simulation.
     */
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

    /**
     * updateEscape is an alternate update method, used during 'escape' mode.
     * I.e. when the car is running into a wall without stopping.
     *
     * @param delta     The time step specified in Simulation
     * @param coord     The coordinate given by the PathFinder class, directing the car
     * @param tarDegree The degree the targeted coordinate is from the direction the car is currently facing.
     */
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
	
    /**
     * allSame takes a list of previous moves, and returns true if they are all the same.
     *
     * @param moveHistory A LinkedList of the moves that occurred immediately before the current update.
     * @return Boolean true if all the moves in the list are the same, false otherwise.
     */
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
	
    /**
     * moveForward takes a Move, a time step, and a target coordinate, and moves Forward in the direction described
     * in the escape move.
     *
     * @param delta       The time step specified in Simulation.
     * @param targetCoord The coordinate targeted by PathFinder.
     */
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

    /**
     * moveCar takes a specified escape move, and moves in the direction and with the acceleration specified by that
     * move.
     *
     * @param escapeMove  The Move generated when entering escape mode -> the opposite move to running into the wall.
     * @param delta       The time step specified in Simulation.
     * @param targetCoord The coordinate targeted by PathFinder.
     */
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
	
    /**
     * moveCar takes a an existing velocity, a desired velocity, a time step, and a direction, and controls the car to
     * move in that direction.
     *
     * @param currentVelocity The current velocity the car is travelling at.
     * @param desiredVelocity The faster or slower velocity that was identified as desirable
     * @param delta           The time step specified in Simulation.
     * @param bestDirection   The direction identified as desirable.
     */
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
    
    /**
     * getTestSpeeds generates an array of Vector2 objects representing different speeds, to be given to peek(...) to
     * generate different possible moves the car can make.
     *
     * @param currVelocity The current velocity of the car.
     * @return An array of Vector2 objects representing the velocities to feed into peek(...).
     */
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
    
    /**
     * getSpeedChanges generates an array of Move.SpeedChange objects representing different speeds, to be given to
     * peek(...) to generate different possible moves the car can make.
     *
     * @param testSpeeds   The array of different possible speeds to be fed into peek(...).
     * @param currVelocity The current velocity of the car.
     * @return Move.SpeedState[] an array of Move.SpeedChange objects representing the different changes in speed that
     * correlate exactly with the different Vector2 objects in testSpeeds.
     */
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
    
    /**
     * goForwardOrBackward takes a direction and a speed change, and directs the car only forward or backwards, based
     * on those arguments.
     *
     * @param d           Represents the direction specified by the calling object.
     * @param speedChange Represents the change in speed specified by the calling object.
     */
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
    
    /**
     * adjustVelocity takes a coordinate and indicates whether the PathFinder is communicating that the car should slow,
     * maintain its speed, or accelerate.
     *
     * @param coordinate The targeted coordinate given by the PathFinder
     * @return A Move.SpeedState that indicates the change in speed desired by the PathFinder.
     */
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
    
    /**
     * getDegreeOfCoord Gets the degree of the Coordinate, in relation to the direction the car is currently facing.
     *
     * @param coord The coordinate being targeted
     * @return double representing the degree of the coordinate in relation to the current direction of the car.
     */
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
    
    /**
     * getDirection takes a degree and returns the direction of that degree.
     *
     * @param degree A double representing the degree being converted into a direction.
     * @return A Direction.RelativeDirection representation of the direction.
     */
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

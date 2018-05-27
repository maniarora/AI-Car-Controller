package navigation;

import java.util.ArrayList;
import java.util.HashMap;

import mycontroller.Move;
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
	
	public void moveCar(Move move, float delta){
		
	}

	
	@Override
	public void update(float delta, ArrayList<Coordinate> coordsToNavigate) {
	}
		
}
	
	

	
	



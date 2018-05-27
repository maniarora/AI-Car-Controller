package navigation;

import java.util.ArrayList;
import java.util.HashMap;

import mycontroller.Move;
import mycontroller.MyAIController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * This class inherits the Navigator.java, to implement and enable the car to
 * move from one point to another.
 * @author Manindra Arora (827703), Ninad Kavi (855506), Ujashkumar Patel (848395)
 * 
 * */
public class SimpleNavigator extends Navigator {
	
	public SimpleNavigator(MyAIController myAIController) {
		super();
		this.controller = myAIController;
	}
	
	public void moveCar(Move move, float delta){
		
	}

	
	@Override
	public void update(float delta, ArrayList<Coordinate> coordsToNavigate) {
	}


		
}
	
	

	
	



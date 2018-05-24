package mycontroller;

import java.util.Arrays;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController {

	public MyAIController(Car car) {
		super(car);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		
		
		applyForwardAcceleration();
		
		
		
	}

}

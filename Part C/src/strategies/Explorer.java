package strategies;

import java.util.HashMap;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import navigation.Navigator;
import tiles.MapTile;
import utilities.Coordinate;


/**
 * This abstract class is used to explore the map initially to identify the locations of keys
 * 
 * The update method is called by MyAIController to move the car about
 * 
 * @author: Manindra Arora (827703) Ninad Kavi (855506)  Ujashkumar Patel (848395)
 * 
 * Group 37
 */
public abstract class Explorer {
	
	/**
	 * This method encompasses most of the functionality of our design
	 * 
	 * @param controller: the MyAIController used to implement the functionality
	 * @param navigator: the navigator which moves the car about 
	 * @param delta: delta since last timestep
	 * @param sensor: the sensor which checks the car's surroundings
	 * 
	 * */
	public abstract void update(MyAIController controller, Navigator navigator, float delta, Sensor sensor);

}

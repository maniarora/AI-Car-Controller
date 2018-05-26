package strategies;

import java.util.HashMap;

import mycontroller.MyAIController;
import mycontroller.Sensor;
import navigation.Navigator;
import tiles.MapTile;
import utilities.Coordinate;

public abstract class Explorer {
	
	public abstract void update(MyAIController controller, Navigator navigator, float delta, Sensor sensor);

}

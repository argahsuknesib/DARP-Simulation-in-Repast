package improvedDialARide;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;



import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
;

/**
 * Class that set up the context
 *
 */
public class Simulation implements ContextBuilder<Object>{
	
	/**
	 * Main method
	 */
	@Override
	public Context build(Context<Object> context) {
		context.setId("basicDialARide");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace <Object> space = spaceFactory.createContinuousSpace("space", context ,
				new RandomCartesianAdder<Object>() ,
				new repast.simphony.space.continuous.WrapAroundBorders(), 50 , 50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),	new SimpleGridAdder<Object>(), true, 50, 50));
		
			
		Parameters params = RunEnvironment.getInstance().getParameters();
		int numVehicles = (int) params.getValue("numberVehicle");
		Random random = new Random();
		
		for(int i = 1; i<=numVehicles;i++)
		{
			context.add(new Vehicle(context, grid, space, "Vehicle"+i));
		}
		
	
		
		Client sourceagent;
		for(int i=0; i<(int) params.getValue("numberRequest"); i++){
			sourceagent = new Client(space, i+1 );
			context.add(sourceagent);
		}
		
			
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}
		
		return context;
	}

	/**
	 * Method that generates random values for simulation
	 * @param iteration : current iteration
	 * @return Array of Double
	 */
	public Double[] generateRandoms(int iteration){
		Double[] tab = new Double[4];
		tab[0] = (double) ThreadLocalRandom.current().nextInt(75, 100 + 1);
		tab[1] = (double) ThreadLocalRandom.current().nextInt(35, 50 + 1);
		tab[2] = (double) ThreadLocalRandom.current().nextInt(50, 120 + 1);
		Random r = new Random();
		double randomValue;
		if(iteration < 125)
		{
			randomValue = 0.6 + (0.8 - 0.6) * r.nextDouble();
		} else if (iteration < 250) {
			randomValue = 0.98 + (1.02 - 0.98) * r.nextDouble();
		} else {
			randomValue = 2 + (6 - 2) * r.nextDouble();
		}
		tab[3] = randomValue;
		return tab;
	}
}
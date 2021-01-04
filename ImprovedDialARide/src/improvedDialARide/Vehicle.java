/**
 * 
 */
package improvedDialARide;

/**
 * @author whiskygrandee
 *
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import repast.simphony.context.Context;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Direction;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * Class that modelizes Vehicle agent
 * 
 *
 */
public class Vehicle {
	private String uuid;
	private boolean hasCustomer;
	private Context<Object> context;
	private Grid<Object> grid;
	private ContinuousSpace<Object> space;
	private String name;
	private double speed;
	private Client client;
	private ArrayList<AgentMessage> mailbox;
	private static final double THRESHOLD = 2;

	/**
	 * Constructor
	 * 
	 * @param context : Context<Object> which is the current context of the
	 *                simulation
	 * @param grid    : Grid<Object> which is the grid projection
	 * @param space   : ContinuousSpace<Object> which is the space projection
	 * @param name    : String that represents the name of this vehicle
	 * @param speed   : double that represents
	 */
	public Vehicle(Context<Object> context, Grid<Object> grid, ContinuousSpace<Object> space, String name) {
		this.context = context;
		this.grid = grid;
		this.space = space;
		this.name = name;
		this.uuid = UUID.randomUUID().toString();
		this.hasCustomer = false;
		client = null;
		mailbox = new ArrayList<AgentMessage>();
	}
	
	public void hello() {
		AgentMessage m = new AgentMessage(this.name, "hello: I have a client", "inform");
		HashMap<String, Object> content = new HashMap<String, Object>();
		content.put("Location", this.grid.getLocation(this));
		m.setContent(content);
		send(m);
	}
	
	
	/**
	 * Method that makes the vehicle move
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {

		AgentMessage m = read();
		while (m != null) {
			System.out.println(m);
			m = read();
		}
		
		GridPoint pt = grid.getLocation(this);
		if (!hasCustomer) {
			List<Object> clients = new ArrayList<Object>();

			for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {

				if (obj instanceof Client) {
					if (((Client) obj).isRideRequested()) {
						clients.add(obj);
					}
				}
			}
			// Pickup
			if (clients.size() > 0) {
				int index = RandomHelper.nextIntFromTo(0, clients.size() - 1);
				this.client = (Client) clients.get(index);
				context.remove(clients.get(index));
				hasCustomer = true;
				hello();
			} else {
				// Marauding
				// Direction.EAST,Direction.EAST ...
				space.moveByVector(this, 1, RandomHelper.nextDoubleFromTo(0, Math.PI * 2),
						RandomHelper.nextDoubleFromTo(0, Math.PI * 2));
				grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY());
			}

		} else {
			// Drop off
			if (Math.abs((pt.getX() - client.getMyRequest().getDestination().getX())) < THRESHOLD
					&& Math.abs((pt.getY() - client.getMyRequest().getDestination().getY())) < THRESHOLD) {
				client.arrived();
				context.add(client);
				space.moveTo(client, pt.getX(), pt.getY());
				grid.moveTo(client, (int) pt.getX(), (int) pt.getY());
				hasCustomer = false;
				client = null;

			} else {
				// go to
				double angle = SpatialMath.calcAngleFor2DMovement(space, space.getLocation(this),
						client.getMyRequest().getDestination());
				space.moveByVector(this, 1, angle, 0);
				grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY());
			}

		}
	}

	protected void send(AgentMessage m) {
		for (Object obj : context)
			if (obj instanceof Vehicle && ((Vehicle) obj).name.compareTo(name)!=0)
				((Vehicle) obj).receive(m);
	}

	private void receive(AgentMessage m) {
		mailbox.add(m);
	}

	private AgentMessage read() {
		if (mailbox.size() > 0)
			return mailbox.remove(0);
		return null;
	}

	@Override
	public String toString() {
		return "Vehicle [name=" + name + ", speed=" + speed + ", client=" + client + "]";
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public boolean isHasCustomer() {
		return hasCustomer;
	}

	/**
	 * Setter
	 * 
	 * @param hasCustomer
	 */
	public void setHasCustomer(boolean hasCustomer) {
		this.hasCustomer = hasCustomer;
	}

	/**
	 * Method that calculates the travel time (estimation)
	 * 
	 * @param request : Request to compute
	 * @return int that represents the ticks number of the estimated travel time
	 */
	public int calcTravelTime(Request request) {
		NdPoint currentLocation = space.getLocation(this);
		NdPoint s1location = request.getOrigin();
		NdPoint s2location = request.getDestination();

		// Calc time between current position and first source
		double distance1 = this.space.getDistance(currentLocation, s1location);
		// Calc time between first source and second source
		double distance2 = this.space.getDistance(s1location, s2location);
		// Added times
		double totaldistance = distance1 + distance2;
		int totaltime = (int) (totaldistance / (this.speed * 0.5));

		return totaltime;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Setter
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	// UI functions

	/**
	 * Getter
	 * 
	 * @return
	 */
	public double getRed() {
		if (this.hasCustomer) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public double getGreen() {
		if (this.hasCustomer) {
			return 0.0;
		} else {
			return 1.0;
		}
	}
}

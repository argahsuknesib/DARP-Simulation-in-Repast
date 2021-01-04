/**
 * 
 */
package improvedDialARide;

import java.util.concurrent.ThreadLocalRandom;

import com.sun.media.rtsp.protocol.Request;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

/**
 * @author whiskygrandee
 *
 */
public class Client {
	private ContinuousSpace<Object> space;
	private int id;
	private boolean rideRequested;
	private boolean isArrived;
	public Request myRequest;	
	/**
	 * Constructor
	 * @param space : ContinousSpace<Object> that represents the space projection
	 * @param id : int that represents the id of this Source
	 */
	public Client(ContinuousSpace<Object> space,  int id) {
		super();
		this.space = space;
		this.id = id;
		this.rideRequested = false;
		myRequest = null;

		isArrived  = false;
	}

	
	@ScheduledMethod(start = 10, interval = 10)
	public void askARide(){
		
		if (!rideRequested && RandomHelper.nextIntFromTo(0, 100) > 80 && !isArrived)
			{	
			myRequest = randomRequest();
			rideRequested = true;
			}
	}

	
	public boolean isRideRequested() {
		return rideRequested;
	}
	
	/**
	 * Getter
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter
	 * @return
	 */
	public Request getMyRequest() {
		return myRequest;
	}


	/**
	 * Getter
	 * @return
	 */
	public NdPoint getLocation(){
		NdPoint location = space.getLocation(this);
		return location;
	}


	@Override
	public String toString() {
		return "Client [id=" + id + ", rideRequested=" + rideRequested + ", myRequest=" + myRequest + "]";
	}

	/**
	 * Method that construct a new random request
	 * @return Request : random request
	 */
	public Request randomRequest(){
		int destination = this.id;
		NdPoint origin = space.getLocation(this);
		double nX = RandomHelper.nextDoubleFromTo(0, space.getDimensions().getWidth());
		double nY = RandomHelper.nextDoubleFromTo(0, space.getDimensions().getHeight());
		
		int timewindow = ThreadLocalRandom.current().nextInt(50, 200 + 1);
		
		NdPoint dest = new NdPoint(nX,nY); 
		
		return new Request(this.id, origin, dest, timewindow,(int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount());	
	}
	
	public void arrived() {
		myRequest = null;
		rideRequested = false;
		isArrived = true;
	}


	public boolean isArrived() {
		
		return isArrived;
	}

}

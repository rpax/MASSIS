package rpax.massis.model.agents;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import rpax.massis.ia.AIController;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.building.Building;
import rpax.massis.model.building.RoomConnector;
import rpax.massis.model.building.SimRoom;
import rpax.massis.model.building.SimulationObject;
import rpax.massis.model.location.Location;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.model.managers.movement.steering.CollisionAvoidance;
import rpax.massis.model.managers.movement.steering.Containment;
import rpax.massis.model.managers.movement.steering.FollowPath;
import rpax.massis.model.managers.movement.steering.SteeringBehavior;
import rpax.massis.model.managers.movement.steering.SteeringCombinationBehavior;
import rpax.massis.sh3d.plugins.metadata.MetaDataUtils;
import rpax.massis.util.geom.KPolygonUtils;
import rpax.massis.util.geom.KVector;
import rpax.massis.util.io.JsonState;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
/**
 * Represents an agent in the simulation environment
 * @author rpax
 *
 */
public abstract class Agent extends SimulationObject implements
		MetadataConstants {

	private static final long serialVersionUID = 1L;
	/**
	 * Current velocity
	 */
	protected KVector velocity = new KVector(Math.random(), Math.random());
	/**
	 * La aceleracion
	 */
	protected KVector acceleration = new KVector(0, 0);
	/**
	 * Number of points of the vision polygon
	 */
	private final int visionRadioPolygonNumPoints = 8;
	/**
	 * Radio of the vision polygon
	 */
	private double visionRadio = 3 * 100;
	protected double maxforce = 200;
	protected double maxspeed = 10;
	// ==================================
	// Cached values & Flags - transient
	// current/last Known room
	private SimRoom lastKnowRoom = null;
	private boolean lastKnownRoomUpdated = false;
	private boolean peopleInVisionRadioUpdated = false;
	private List<Agent> peopleInVisionArea = null;

	//
	private boolean visionFinderUpdated = false;
	private Shape visionPolygon;
	private double lastAngleOfVision;
	private KPolygon boundaryPolygon;
	private AIController<? extends Agent> iaController;
	public static final double VISION_RADIO_SCALE = 1;
	private final SteeringBehavior steeringBehavior;

	public Agent(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment, String resourcesFolder) {
		super(metadata, location, movementManager, animationManager,
				environment, resourcesFolder);
		this.peopleInVisionArea = new ArrayList<Agent>();
		this.createVisionRadioPolygon();
		/*
		 * Compute cached values
		 */
		this.clearCache();
		this.steeringBehavior = createSteeringBehavior();

	}
	/**
	 *  Creates an AI controller for this agent
	 * @return the AI Controller of this agent
	 */
	protected abstract AIController<? extends Agent> createIAController();

	@Override
	public final void step() {
		if (this.iaController == null)
		{
			this.iaController = this.createIAController();
		}
		this.iaController.logic();
	}
	/**
	 * 
	 * @return the simulation context of this agent
	 */
	public final SimulationContext<? extends Agent> getContext() {
		if (this.iaController == null)
		{
			this.iaController = this.createIAController();
		}
		return this.iaController.getContext();
	}
	/**
	 * Creates the vision polygon
	 */
	protected void createVisionRadioPolygon() {
		boundaryPolygon = KPolygon.createRegularPolygon(
				visionRadioPolygonNumPoints, visionRadio);
		boundaryPolygon.translateTo(this.getXY());

	}
	/**
	 * Checks if the vision polygon must be recomputed, an if it so, recomputes it
	 */
	private void computeVisionFinderCalc() {
		if (this.visionFinderUpdated)
			return;
		/*
		 * rotates the current angle 
		 */
		boundaryPolygon.rotate(this.getAngle() - lastAngleOfVision);
		/*
		 * Set last angle of vision
		 */
		this.lastAngleOfVision = this.getAngle();
		/*
		 * Move the polygon accordingly
		 */
		boundaryPolygon.translateTo(this.getXY());
		/*
		 * Intersect the polygon with the room boundaries
		 */
		visionPolygon = KPolygonUtils.intersection(boundaryPolygon, this
				.getRoom().getPolygon());
		/*
		 * If the operation was done successfully
		 */
		if (visionPolygon == null)
		{
			visionPolygon = this.getPolygon();
		}
		/*
		 * set the flag to 1
		 */
		this.visionFinderUpdated = true;
	}

	@Override
	public void moveTo(Location other) {
		super.moveTo(other);
		this.clearCache();
	}
	/**
	 * Clear cached values 
	 */
	public void clearCache() {
		this.lastKnownRoomUpdated = false;
		this.visionFinderUpdated = false;
		this.peopleInVisionRadioUpdated = false;
	}
	/**
	 * 
	 * @return the {@link Shape} of the vision polygon
	 */
	public Shape getVisionRadioShape() {
		computeVisionFinderCalc();
		return this.visionPolygon;

	}
	/**
	 * 
	 * @param p the point to check
	 * @return if the point is contained in the vision area
	 */
	public boolean isPointContainedInVisionArea(KPoint p) {
		if (this.getLocation().distance2D(p) > this.visionRadio)
		{
			return false;
		}
		return this.getVisionRadioShape().contains(p.x, p.y);
	}
	/**
	 * Computation of the visible agents
	 */
	private void computePeopleInVisionArea() {

		if (!this.peopleInVisionRadioUpdated)
		{
			this.peopleInVisionRadioUpdated = true;
			ArrayList<Agent> peopleInVisionArea_tmp = new ArrayList<>();

			for (Agent agent : this.getAgentsInRange(this.getVisionRadio()))
			{
				{
					peopleInVisionArea_tmp.add(agent);
				}
			}
			this.peopleInVisionArea = peopleInVisionArea_tmp;

		}
	}

	public boolean isObjectPerceived(Agent other) {
		return other.getLocation().distance2D(this.getLocation()) < this.visionRadio
				&& other.getRoom() == this.getRoom();
	}

	public Iterable<Agent> getAgentsInVisionRadio() {
		this.computePeopleInVisionArea();
		return this.peopleInVisionArea;
	}

	public Iterable<Agent> getAgentsInRange(double range) {
		return this.getEnvironment().getAgentsInRange(this, range);
	}

	public double getVisionRadio() {
		return this.visionRadio;
	}

	public boolean isInDoorArea() {
		for (RoomConnector sd : this.getRoom().getConnectedRoomConnectors())
		{
			if (sd.getPolygon().getAABB()
					.intersects(this.getPolygon().getAABB()))
			{
				return true;
			}
		}
		return false;
	}
	/*
	 * Speed up methods
	 */
	public void setLastKnowRoom(SimRoom lastKnowRoom) {
		this.lastKnowRoom = lastKnowRoom;
		this.lastKnownRoomUpdated = true;
	}

	private void computeLastKnownRoom() {
		if (this.lastKnownRoomUpdated)
		{
			return;
		}
		if (lastKnowRoom != null)
		{
			findRoomByLastKnownRoom();
		}
		else
		{
			for (SimRoom sr : this.getLocation().getFloor().getRooms())
			{
				if (sr.getPolygon().getRadius() > KPoint.distance(
						sr.getPolygon().center, this.getPolygon().center))
				{
					if (sr.getPolygon().contains(this.getPolygon().center))
					{
						lastKnowRoom = sr;
						break;
					}
				}
			}
		}
		this.lastKnownRoomUpdated = true;
	}

	public SimRoom getRoom() {
		this.computeLastKnownRoom();
		return this.lastKnowRoom;
	}

	public Collection<Agent> getVehiclesInRoom() {
		this.computeLastKnownRoom();
		return this.lastKnowRoom.getPeopleIn();
	}

	private SimRoom findRoomByLastKnownRoom() {
		final double x = this.getX();
		final double y = this.getY();
		final SimRoom lastKnown = this.lastKnowRoom;
		for (SimRoom sr : lastKnown.getRoomsOrderedByDistance())
		{
			if (sr.getPolygon().contains(x, y))
			{
				lastKnowRoom = sr;
				return lastKnowRoom;
			}
		}
		return this.lastKnowRoom;
	}

	public int getVisionRadioPolygonNumPoints() {
		return visionRadioPolygonNumPoints;
	}

	public double getMaxSpeed() {
		return this.maxspeed;
	}

	public KVector getVelocity() {
		return this.velocity;
	}

	public double getMaxForce() {
		return this.maxforce;
	}

	public KVector getAcceleration() {
		return acceleration;
	}

	@Override
	public JsonState getState() {

		return new VehicleState(this, super.getState());
	}

	protected static class VehicleState implements JsonState {

		private final KVector velocity;
		private final double visionRadio;
		private final double maxforce;
		private final double maxspeed;
		private final JsonState data;

		public VehicleState(Agent v, JsonState data) {
			this.data = data;
			this.velocity = v.velocity.copy();
			this.visionRadio = v.visionRadio;
			this.maxforce = v.maxforce;
			this.maxspeed = v.maxspeed;
		}

		@Override
		public Object restore(Building building) {
			Agent v = (Agent) this.data.restore(building);
			v.velocity = this.velocity;
			v.visionRadio = this.visionRadio;
			v.maxforce = this.maxforce;
			v.maxspeed = this.maxspeed;
			return v;
		}
	}


	
	public static final Agent createAgent(Home home, HomePieceOfFurniture f,
			SimLocation location, MovementManager movement,
			AnimationManager animation, EnvironmentManager environment,
			String resourcesFolder) {
		Map<String, String> metadata = MetaDataUtils.getMetaData(home, f);
		Agent newAgent = null;
		String className = metadata.get(CLASS_NAME);
		if (className != null)
		{
			try
			{
				newAgent = (Agent) Class
						.forName(className)
						.getConstructor(java.util.Map.class, SimLocation.class,
								MovementManager.class, AnimationManager.class,
								EnvironmentManager.class, String.class)
						.newInstance(metadata, location, movement, animation,
								environment, resourcesFolder);

			}
			catch (Exception e)
			{
				System.err
						.println("Could not create an agent instance of class "
								+ className);
				e.printStackTrace();
			}

		}

		return newAgent;

	}

	public void setVelocity(KVector v) {
		this.velocity.x = v.x;
		this.velocity.y = v.y;
	}

	public void setAcceleration(KVector acc) {
		this.acceleration.x = acc.x;
		this.acceleration.y = acc.y;
	}

	

	

	public List<KPoint> getPath() {
		return this.getMovementManager().getPathOf(this);
	}

	public boolean approachTo(Location location) {
		return this.getMovementManager().approachTo(this, location);
	}

	public SteeringBehavior createSteeringBehavior() {
		return new SteeringCombinationBehavior(this, new Containment(this),
				new CollisionAvoidance(this, 125),
				new FollowPath(this, 25, 100));
	}

	public SteeringBehavior getSteeringBehavior() {
		return steeringBehavior;
	}

}

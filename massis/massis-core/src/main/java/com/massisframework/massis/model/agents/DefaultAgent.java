package com.massisframework.massis.model.agents;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.massisframework.massis.model.building.Building;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.building.RoomConnector;
import com.massisframework.massis.model.building.SimRoom;
import com.massisframework.massis.model.building.SimulationObject;
import com.massisframework.massis.model.building.WayPoint;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.location.SimLocation;
import com.massisframework.massis.model.managers.AnimationManager;
import com.massisframework.massis.model.managers.EnvironmentManager;
import com.massisframework.massis.model.managers.movement.ApproachCallback;
import com.massisframework.massis.model.managers.movement.MovementManager;
import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.model.managers.movement.steering.CollisionAvoidance;
import com.massisframework.massis.model.managers.movement.steering.Containment;
import com.massisframework.massis.model.managers.movement.steering.FollowPath;
import com.massisframework.massis.model.managers.movement.steering.SteeringBehavior;
import com.massisframework.massis.model.managers.movement.steering.SteeringCombinationBehavior;
import com.massisframework.massis.model.managers.pathfinding.PathFindingManager;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult;
import com.massisframework.massis.util.SimObjectProperty;
import com.massisframework.massis.util.geom.KPolygonUtils;
import com.massisframework.massis.util.geom.KVector;
import com.massisframework.massis.util.io.JsonState;

import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

/**
 * Represents an agent in the simulation environment
 *
 * @author rpax
 *
 */
public class DefaultAgent extends SimulationObject implements LowLevelAgent {

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
	private final int visionRadioPolygonNumPoints = DEFAULT_VISION_POLY_POINTS;
	/**
	 * Radio of the vision polygon
	 */
	private double visionRadio = DEFAULT_VISION_RADIO;
	protected double maxforce = DEFAULT_MAX_FORCE;
	protected double maxspeed = DEFAULT_MAX_SPEED;
	// ==================================
	// Cached values & Flags - transient
	// current/last Known room
	private SimRoom lastKnowRoom = null;
	private boolean lastKnownRoomUpdated = false;
	private boolean peopleInVisionRadioUpdated = false;
	private List<LowLevelAgent> peopleInVisionArea = null;
	//
	private boolean visionFinderUpdated = false;
	private Shape visionPolygon;
	private double lastAngleOfVision;
	private KPolygon boundaryPolygon;
	public static final double VISION_RADIO_SCALE;
	private final SteeringBehavior steeringBehavior;
	private final boolean isObstacle;
	private final boolean isDynamic;
	private Object highLevelData;
	private static final int DEFAULT_VISION_POLY_POINTS;
	private static final double DEFAULT_VISION_RADIO, DEFAULT_MAX_FORCE,
			DEFAULT_MAX_SPEED;

	static
	{
		final ResourceBundle bundle = ResourceBundle
				.getBundle(DefaultAgent.class.getName());
		DEFAULT_VISION_RADIO = Double
				.parseDouble(bundle.getString("visionRadio"));
		VISION_RADIO_SCALE = Double
				.parseDouble(bundle.getString("visionRadioScale"));
		DEFAULT_MAX_FORCE = Double.parseDouble(bundle.getString("maxforce"));
		DEFAULT_MAX_SPEED = Double.parseDouble(bundle.getString("maxspeed"));
		DEFAULT_VISION_POLY_POINTS = Integer
				.parseInt(bundle.getString("visionRadioPolygonNumPoints"));

	}

	public DefaultAgent(Map<String, String> metadata, SimLocation location,
			MovementManager movementManager, AnimationManager animationManager,
			EnvironmentManager environment, PathFindingManager pathManager)
	{
		super(metadata, location, movementManager, animationManager,
				environment, pathManager);
		this.peopleInVisionArea = new ArrayList<>();
		this.createVisionRadioPolygon();
		/*
		 * Compute cached values
		 */
		this.clearCache();
		this.steeringBehavior = createSteeringBehavior();
		this.isObstacle = getBooleanFromMetadata(
				SimObjectProperty.IS_OBSTACLE.toString(), metadata, true);

		this.isDynamic = getBooleanFromMetadata(
				SimObjectProperty.IS_DYNAMIC.toString(), metadata, false);

	}

	// @Override
	// public final void step()
	// {
	// this.highLevelController.logic();
	// }
	/**
	 * Creates the vision polygon
	 */
	protected final void createVisionRadioPolygon()
	{
		this.boundaryPolygon = KPolygon
				.createRegularPolygon(this.visionRadioPolygonNumPoints,
						this.visionRadio);
		this.boundaryPolygon.translateTo(this.getXY());

	}

	/**
	 * Checks if the vision polygon must be recomputed, an if it so, recomputes
	 * it
	 */
	private void computeVisionFinderCalc()
	{
		if (this.visionFinderUpdated)
		{
			return;
		}
		/*
		 * rotates the current angle
		 */
		this.boundaryPolygon.rotate(this.getAngle() - this.lastAngleOfVision);
		/*
		 * Set last angle of vision
		 */
		this.lastAngleOfVision = this.getAngle();
		/*
		 * Move the polygon accordingly
		 */
		this.boundaryPolygon.translateTo(this.getXY());
		/*
		 * Intersect the polygon with the room boundaries
		 */
		this.visionPolygon = KPolygonUtils.intersection(this.boundaryPolygon,
				this.getRoom().getPolygon());
		/*
		 * If the operation was done successfully
		 */
		if (this.visionPolygon == null)
		{
			this.visionPolygon = this.getPolygon();
		}
		/*
		 * set the flag to 1
		 */
		this.visionFinderUpdated = true;
	}

	@Override
	public void moveTo(Location other)
	{
		super.moveTo(other);
		this.clearCache();
	}

	/**
	 * Clear cached values
	 */
	public final void clearCache()
	{
		this.lastKnownRoomUpdated = false;
		this.visionFinderUpdated = false;
		this.peopleInVisionRadioUpdated = false;
	}

	/**
	 *
	 * @return the {@link Shape} of the vision polygon
	 */
	@Override
	public Shape getVisionRadioShape()
	{
		computeVisionFinderCalc();
		return this.visionPolygon;

	}

	/**
	 *
	 * @param p
	 *            the point to check
	 * @return if the point is contained in the vision area
	 */
	@Override
	public boolean isPointContainedInVisionArea(KPoint p)
	{
		if (this.getLocation().distance2D(p) > this.visionRadio)
		{
			return false;
		}
		return this.getVisionRadioShape().contains(p.x, p.y);
	}

	/**
	 * Computation of the visible agents
	 */
	private void computePeopleInVisionArea()
	{

		if (!this.peopleInVisionRadioUpdated)
		{
			this.peopleInVisionRadioUpdated = true;
			final ArrayList<LowLevelAgent> peopleInVisionArea_tmp = new ArrayList<>();

			for (final LowLevelAgent agent : this
					.getAgentsInRange(this.getVisionRadio()))
			{
				{
					peopleInVisionArea_tmp.add(agent);
				}
			}
			this.peopleInVisionArea = peopleInVisionArea_tmp;

		}
	}

	@Override
	public boolean isObjectPerceived(LowLevelAgent other)
	{
		return other.getLocation()
				.distance2D(this.getLocation()) < this.visionRadio
				&& other.getRoom() == this.getRoom();
	}

	@Override
	public Iterable<LowLevelAgent> getAgentsInVisionRadio()
	{
		this.computePeopleInVisionArea();
		return this.peopleInVisionArea;
	}

	@Override
	public Iterable<LowLevelAgent> getAgentsInRange(double range)
	{
		return this.getEnvironment().getAgentsInRange(this, range);
	}

	@Override
	public double getVisionRadio()
	{
		return this.visionRadio;
	}

	@Override
	public boolean isInDoorArea()
	{
		for (final RoomConnector sd : this.getRoom()
				.getConnectedRoomConnectors())
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

	public void setLastKnowRoom(SimRoom lastKnowRoom)
	{
		this.lastKnowRoom = lastKnowRoom;
		this.lastKnownRoomUpdated = true;
	}

	private void computeLastKnownRoom()
	{
		if (this.lastKnownRoomUpdated)
		{
			return;
		}
		if (this.lastKnowRoom != null)
		{
			findRoomByLastKnownRoom();
		} else
		{
			for (final SimRoom sr : this.getLocation().getFloor().getRooms())
			{
				if (sr.getPolygon().getRadius() > KPoint.distance(
						sr.getPolygon().center, this.getPolygon().center))
				{
					if (sr.getPolygon().contains(this.getPolygon().center))
					{
						this.lastKnowRoom = sr;
						break;
					}
				}
			}
		}
		this.lastKnownRoomUpdated = true;
	}

	@Override
	public SimRoom getRoom()
	{
		this.computeLastKnownRoom();
		return this.lastKnowRoom;
	}

	@Override
	public Collection<LowLevelAgent> getAgentsInRoom()
	{
		this.computeLastKnownRoom();
		return this.lastKnowRoom.getPeopleIn();
	}

	private SimRoom findRoomByLastKnownRoom()
	{
		final double x = this.getX();
		final double y = this.getY();
		final SimRoom lastKnown = this.lastKnowRoom;
		for (final SimRoom sr : lastKnown.getRoomsOrderedByDistance())
		{
			if (sr.getPolygon().contains(x, y))
			{
				this.lastKnowRoom = sr;
				return this.lastKnowRoom;
			}
		}
		return this.lastKnowRoom;
	}

	public int getVisionRadioPolygonNumPoints()
	{
		return this.visionRadioPolygonNumPoints;
	}

	@Override
	public double getMaxSpeed()
	{
		return this.maxspeed;
	}

	@Override
	public void setMaxSpeed(double maxspeed)
	{
		this.maxspeed = maxspeed;
	}

	@Override
	public void setMaxForce(double maxforce)
	{
		this.maxforce = maxforce;
	}

	@Override
	public KVector getVelocity()
	{
		return this.velocity;
	}

	@Override
	public double getMaxForce()
	{
		return this.maxforce;
	}

	@Override
	public KVector getAcceleration()
	{
		return this.acceleration;
	}

	@Override
	public JsonState<Building> getState()
	{

		return new VehicleState(this, super.getState());
	}

	@Override
	public boolean isObstacle()
	{
		return this.isObstacle;
	}

	@Override
	public boolean isDynamic()
	{
		return this.isDynamic;
	}

	private boolean getBooleanFromMetadata(String key,
			Map<String, String> metadata, boolean defaultValue)
	{
		final String strVal = StringUtils.defaultIfEmpty(metadata.get(key),
				String.valueOf(defaultValue));
		return Boolean.parseBoolean(strVal);
	}

	@Override
	public double getBodyRadius()
	{
		return this.getPolygon().getRadius();
	}

	@Override
	public void approachToNamedLocation(String name,
			ApproachCallback approachCallback)
	{
		final Location namedLocation = this.getEnvironment()
				.getNamedLocation(name);
		Objects.requireNonNull(namedLocation);
		this.approachTo(namedLocation, approachCallback);
	}

	@Override
	public boolean isInNamedLocation(String name, int radiusWithin)
	{
		final Location namedLocation = this.getEnvironment()
				.getNamedLocation(name);
		Objects.requireNonNull(namedLocation);
		return namedLocation.isInSameFloor(namedLocation)
				&& namedLocation.distance2D(this.getLocation()) < radiusWithin;
	}

	// @Override
	// public Location approachToRandomTarget() {
	// /*
	// * Common variables
	// */
	// Location target = null;
	// KPoint p = new KPoint();
	// Random rnd = ThreadLocalRandom.current();
	// do {
	// /*
	// * Auxiliary "random" room & its shape
	// */
	// SimRoom sr;
	// KPolygon roomPoly;
	// Floor roomFloor;
	// do {
	// /*
	// * Initialization
	// */
	// sr = this.getEnvironment().getRandomRoom();
	// roomPoly = sr.getPolygon();
	// roomFloor = sr.getLocation().getFloor();
	//
	// final Rectangle2D.Double bounds = roomPoly.getBounds2D();
	// final int bounds_width = (int) bounds.getWidth();
	// final int bounds_height = (int) bounds.getHeight();
	//
	// /*
	// * Random point in bounds
	// */
	// p.x = bounds.getX() + rnd.nextInt(bounds_width);
	// p.y = bounds.getY() + rnd.nextInt(bounds_height);
	// /*
	// * Try to get a point outside obstacles
	// */
	// p = roomFloor.getNearestPointOutsideOfObstacles(p);
	// /*
	// * If the room does not contain the point, repeat with another
	// * rnd room
	// */
	// } while (!roomPoly.contains(p));
	// /*
	// * Create the target.
	// */
	// target = new Location(p, roomFloor);
	// /*
	// * Exists the (remote) possibility that the agent is in the rnd
	// * target selected.In that case, repeat the whole loop.
	// */
	// } while (this.approachTo(target));
	// return target;
	// }

	protected static class VehicleState implements JsonState<Building> {

		private final KVector velocity;
		private final double visionRadio;
		private final double maxforce;
		private final double maxspeed;
		private final JsonState<Building> data;

		public VehicleState(DefaultAgent v, JsonState<Building> data)
		{
			this.data = data;
			this.velocity = v.velocity.copy();
			this.visionRadio = v.visionRadio;
			this.maxforce = v.maxforce;
			this.maxspeed = v.maxspeed;
		}

		@Override
		public Object restore(Building building)
		{
			final DefaultAgent v = (DefaultAgent) this.data.restore(building);
			v.velocity = this.velocity;
			v.visionRadio = this.visionRadio;
			v.maxforce = this.maxforce;
			v.maxspeed = this.maxspeed;
			return v;
		}
	}

	public void setVelocity(KVector v)
	{
		this.velocity.x = v.x;
		this.velocity.y = v.y;
	}

	public void setAcceleration(KVector acc)
	{
		this.acceleration.x = acc.x;
		this.acceleration.y = acc.y;
	}

	@Override
	public void approachTo(final Location location,
			final ApproachCallback approachCallback)
	{
		// // return this.getMovementManager().approachTo(this, location);
		// // this.path
		// throw new UnsupportedOperationException();

		// Maybe the agent is slightly inside an obstacle.
		final Floor currentFloor = getLocation().getFloor();
		final KPoint currentXY = DefaultAgent.this.getLocation().getXY();
		final KPoint nearestPoint = currentFloor
				.getNearestPointOutsideOfObstacles(currentXY);
		if (nearestPoint.x != currentXY.x || nearestPoint.y != currentXY.y)
		{

//			// Get the direction to the nearest point.
//			// move the agent to the nearest point + its radius
//
//			//
//			final KVector displacement = KVector
//					.sub(nearestPoint, currentXY)
//					.normalize()
//					.mult(this.getMaxForce());
//			// final KPoint nextLocXY = KVector.add(currentXY,displacement);
//			this.applySteeringForcesAndMove(displacement);
//			// this.moveTo(new Location(nextLocXY, currentFloor));
			this.pathManager.removeFromCache(DefaultAgent.this);
			this.clearCache();
//			return;
		}

		// 1. getPath
		this.pathManager.findPath(this, location, new FindPathResult() {

			@Override
			public void onSuccess(Path path)
			{
				// Now we have a path.
				// lets follow it!
				// 1. Check for special teleports
				for (final WayPoint wayPoint : path.getPoints())
				{

					if (wayPoint.canExecuteWayPointAction(DefaultAgent.this))
					{
						if (wayPoint.executeWayPointAction(DefaultAgent.this))
						{
							DefaultAgent.this.clearCache();
							approachCallback.onSucess(DefaultAgent.this);
							return;
						}
					}
				}

				// Steering
				final DefaultAgent v = DefaultAgent.this;
				v.applySteeringForcesAndMove(v.getSteeringBehavior().steer());
				if (DefaultAgent.this.getLocation().distance2D(
						// UF
						location) <= DefaultAgent.this.getBodyRadius() * 1.5f)
				{
					approachCallback.onTargetReached(DefaultAgent.this);
					// Avoids inverse path following
					DefaultAgent.this.pathManager
							.removeFromCache(DefaultAgent.this);

				} else
				{
					approachCallback.onSucess(DefaultAgent.this);
				}

			}

			@Override
			public void onError(PathFinderErrorReason reason)
			{
				// ERROR!
				Logger.getLogger(DefaultAgent.class.getName()).log(
						Level.WARNING, "Error when finding path: {0}", reason);
				approachCallback.onPathFinderError(reason);
			}
		});

	}

	public final SteeringBehavior createSteeringBehavior()
	{
		return new SteeringCombinationBehavior(this, new Containment(this),
				new CollisionAvoidance(this, 125),
				new FollowPath(this, 25, 100));
	}

	public SteeringBehavior getSteeringBehavior()
	{
		return this.steeringBehavior;
	}

	@Override
	public Object getHighLevelData()
	{
		return this.highLevelData;
	}

	@Override
	public void setHighLevelData(Object highLevelData)
	{
		this.highLevelData = highLevelData;
	}

	@Override
	public boolean hasPath()
	{
		return getPath() != null;
	}

	@Override
	public Path getPath()
	{
		return this.pathManager.getPathOf(this);
	}

	/**
	 * Applies the forces given to the current position and velocity of the
	 * agent, producing a new position and a new velocity. The agent is updated
	 * and moved accordingly
	 *
	 * @param vehicle
	 *            the agent to move
	 * @param forces
	 *            the forces applied to that agent
	 */
	@Override
	public void applySteeringForcesAndMove(KVector forces)
	{
		forces.mult(this.getMaxForce());
		final KVector steering = KVector.limit(forces, this.getMaxForce());
		// steering = steering / mass
		this.setAcceleration(steering);
		final KVector velocity = KVector.limit(
				KVector.add(steering, this.getVelocity()), this.getMaxSpeed());
		this.setVelocity(velocity);
		final KVector position = KVector.add(this.getXY(), this.getVelocity());

		final Location newLocation = new Location(position,
				this.getLocation().getFloor());
		this.moveTo(newLocation);
	}

	@Override
	public SimRoom getRandomRoom()
	{
		return this.getEnvironment().getRandomRoom();
	}
}

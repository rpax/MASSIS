/**
 * 
 */
package rpax.massis.model.building;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import rpax.massis.model.location.Location;
import rpax.massis.model.location.SimLocation;
import rpax.massis.model.managers.AnimationManager;
import rpax.massis.model.managers.EnvironmentManager;
import rpax.massis.model.managers.movement.MovementManager;
import rpax.massis.sim.Simulation;
import rpax.massis.util.Indexable;
import rpax.massis.util.geom.CoordinateHolder;
import rpax.massis.util.io.JsonState;
import rpax.massis.util.io.Restorable;
import rpax.massis.util.io.SimulationSaver;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonHolder;

/**
 * Basic element of the simulation. It is the result of processing the elements
 * of SweetHome3D. 
 * 
 * @author rpax
 * 
 */
public abstract class SimulationObject implements PolygonHolder, Steppable,
		Stoppable, Indexable, CoordinateHolder, Restorable {

	private static final long serialVersionUID = 1L;
	/**
	 * The id of this object
	 */
	protected final int id;
	/**
	 * Properties of this element. 
	 */
	private final Map<String, Integer> properties = new HashMap<>();
	// Managers
	private final MovementManager movement;
	private final AnimationManager animation;
	private final EnvironmentManager environment;
	/**
	 * Location of this object
	 */
	private final SimLocation location;
	/**
	 * Flag for knowing if this element has changed. Useful for I/O operations
	 */
	private boolean changed;
	/**
	 * Resources folder.
	 */
	protected final String resourcesFolder;
	/**
	 * Main constructor
	 * @param metadata the SweetHome3D metadata of this element
	 * @param location the location of this element
	 * @param movementManager in charge of movement
	 * @param animationManager in charge of animation
	 * @param environment in charge of retrieving information from the environment
	 * @param resourcesFolder the resources folder
	 */
	public SimulationObject(final Map<String, String> metadata,
			SimLocation location, MovementManager movementManager,
			AnimationManager animationManager, EnvironmentManager environment,
			String resourcesFolder) {
		this.resourcesFolder = resourcesFolder;
		this.id = Integer.parseInt(metadata.get("id"));
		// check para ver si ha sido reconocido?
		this.changed = false;
		this.movement = movementManager;
		this.animation = animationManager;
		this.environment = environment;
		this.location = location;
		this.getLocation().attach(this);
	}

	public final SimLocation getLocation() {
		return this.location;
	}

	@Override
	public final int getID() {
		return this.id;
	}
	/**
	 * Moves the agent to an specific location
	 * @param other the target location
	 */
	public void moveTo(Location other) {
		this.location.translateTo(other);
		this.animate();
		//
		this.setChanged();

	}

	protected final void setChanged() {
		this.changed = true;
	}

	public void animate() {
		this.animation.animate(this);
	}

	@Override
	public final double getX() {
		return this.location.getX();
	}

	@Override
	public final double getY() {
		return this.location.getY();
	}

	@Override
	public final KPoint getXY() {

		return this.getPolygon().center;
	}
	/**
	 * Returns the coordinates of this object.
	 * @param coord available 1D lenght 2 array
	 * @return the same array, filled with the coordinates of this object
	 */
	public double[] getXYCoordinates(final double[] coord) {
		coord[0] = this.location.getX();
		coord[1] = this.location.getY();
		return coord;
	}

	@Override
	public final KPolygon getPolygon() {
		return this.location.getPolygon();
	}

	@Override
	public final void step(SimState state) {
		step();
		if (state instanceof Simulation)
		{
			SimulationSaver ss = ((Simulation) state).getSimulationSaver();
			if (this.changed && ss != null)
			{
				ss.notifyChanged(this);
				this.changed = false;
			}
		}
	}
	/**
	 * TODO rpax. Remove it from here.
	 * @return the connectors on the floor of this agent
	 */
	public List<RoomConnector> getRoomsConnectorsInSameFloor() {
		return this.getLocation().getFloor().getRoomConnectors();
	}

	public void step() {
		// nothing by default
	}

	@Override
	public void stop() {
	}
	/**
	 * Returns the numeric value of the simulation object property
	 * @param propertyName the property name
	 * @return its value, 0 if does not exist.
	 */
	public int getProperty(String propertyName) {
		if (!this.properties.containsKey(propertyName))
			return 0;
		return this.properties.get(propertyName);
	}
	
	public boolean hasProperty(String propertyName) {
		return this.properties.containsKey(propertyName);
	}

	public void setProperty(String propertyName, int value) {
		this.properties.put(propertyName, value);
	}

	public void removeProperty(String propertyName) {
		this.properties.remove(propertyName);
	}

	public Map<String, Integer> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	@Override
	public String toString() {
		return "[SimulationObject#" + this.getID() + " {" + this.location
				+ "}]";
	}

	public double getAngle() {
		return this.location.getAngle();
	}

	@Override
	public JsonState getState() {
		return new SimulationObjectState(this);
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimulationObject))
			return false;

		SimulationObject other = (SimulationObject) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private static class SimulationObjectState implements JsonState {

		protected int id;
		protected HashMap<String, Integer> properties;
		protected JsonState locationState;

		public SimulationObjectState(SimulationObject obj) {
			this.id = obj.id;
			this.properties = new HashMap<>(obj.getProperties());
			this.locationState = obj.getLocation().getState();
		}

		@Override
		public SimulationObject restore(Building building) {
			SimulationObject simObj = building.getSimulationObject(id);
			for (Entry<String, Integer> entry : this.properties.entrySet())
			{
				simObj.setProperty(entry.getKey(), entry.getValue());
			}
			locationState.restore(building);
			simObj.animate();
			return simObj;
		}

	}

	public MovementManager getMovementManager() {
		return movement;
	}

	public EnvironmentManager getEnvironment() {
		return environment;
	}

}

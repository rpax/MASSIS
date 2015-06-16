package rpax.massis.model.managers;

import rpax.massis.model.agents.Agent;
import rpax.massis.model.building.Building;
import rpax.massis.model.building.SimRoom;
import rpax.massis.model.location.Location;
import rpax.massis.util.collections.filters.Filters;

/**
 * Manages the environment information of an agent
 * 
 * @author rpax
 * 
 */
public class EnvironmentManager {

	protected Building building;

	public EnvironmentManager(Building building) {
		this.building = building;
	}

	public Iterable<Agent> getAgentsInRange(Location l, double range) {
		return l.getFloor().getAgentsInRange((int) (l.getX() - range),
				(int) (l.getY() - range), (int) (l.getX() + range),
				(int) (l.getY() + range));

	}

	public Iterable<Agent> getAgentsInRange(Agent a, double range) {
		return Filters.allExcept(this.getAgentsInRange(a.getLocation(), range),
				a);
	}

	public SimRoom getRandomRoom() {
		return this.building.getRandomRoom();
	}

	public Location getNamedLocation(String name) {
		return this.building.getNamedLocation(name);
	}

}

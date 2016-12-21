package com.massisframework.massis.model.managers.pathfinding;

import java.util.HashMap;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.building.Floor;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.model.managers.movement.Path;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult;

public class PathFindingManager {
	/**
	 * Cached paths
	 */
	private final HashMap<PathFollower, Path> paths = new HashMap<>();
	/**
	 * Cached targets
	 */
	private final HashMap<PathFollower, Location> targets = new HashMap<>();

	public void findPath(final PathFollower vehicle, final Location toLoc,
			final FindPathResult callback) {
		/*
		 * Check if the target is the same, and therefore, if the path can be
		 * reused. TODO more checks are necessary (if the agent is in the same room, for example).
		 */
		if (!toLoc.equals(this.targets.get(vehicle))) {
			this.removeFromCache(vehicle);
		}
		/*
		 * Exists a cached path?
		 */
		Path path = this.paths.get(vehicle);
		if (path == null) {

			final Floor agentFloor = vehicle.getLocation().getFloor();
			agentFloor.findPath(vehicle.getLocation(), toLoc,
					new FindPathResult() {
						/*
						 * Path is cached
						 */
						@Override
						public void onSuccess(Path path) {
							PathFindingManager.this.paths.put(vehicle, path);
							PathFindingManager.this.targets.put(vehicle,
									new Location(toLoc));
							// continuabamos.
							callback.onSuccess(path);
						}

						@Override
						public void onError(PathFinderErrorReason reason) {
							// fixInvalidLocation(vehicle, toLoc);
							// return false
							callback.onError(reason);
						}
					});
		}
		else {
			//path not null, already in cache
			callback.onSuccess(path);
		}
	}
	
	public void removeFromCache(Object v) {
		this.paths.remove(v);
		this.targets.remove(v);
	}
	/**
	 * Returns the cached path of the agent provided
	 *
	 * @param v
	 *            the agent
	 * @return the agent's path, an empty list if has no path.
	 */
	public Path getPathOf(LowLevelAgent v) {
		Path path = this.paths.get(v);
		if (path == null || path.isEmpty()) {
			return null;
		}
		return path;
	}
}
